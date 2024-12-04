------------------------------------------------- Users ----------------------------------------------------------------
CREATE TABLE users (
                       id              INTEGER     CONSTRAINT user_pk PRIMARY KEY AUTOINCREMENT,
                       creation_date   TIMESTAMP   DEFAULT CURRENT_TIMESTAMP,
                       name            TEXT    NOT NULL,
                       contact         TEXT    NOT NULL    CONSTRAINT user_pk_2    UNIQUE,
                       email           TEXT    NOT NULL    CONSTRAINT user_pk_3    UNIQUE,
                       password        TEXT    NOT NULL,
                       target_group_id INTEGER REFERENCES groups(id) DEFAULT NULL
);

------------------------------------------------- Groups ---------------------------------------------------------------

CREATE TABLE groups (
                        id              INTEGER  CONSTRAINT group_pk PRIMARY KEY AUTOINCREMENT,
                        creation_date   TIMESTAMP   DEFAULT CURRENT_TIMESTAMP,
                        name            TEXT                    NOT NULL CONSTRAINT group_pk_2 UNIQUE,
                        owner_id        INTEGER                 NOT NULL REFERENCES users(id)
);

-- Tabela de Relação Users <-> Groups | Multiplos users podem fazer parte de multiplos grupos
CREATE TABLE group_members (
                               group_id        INTEGER REFERENCES groups(id),
                               user_id         INTEGER REFERENCES users(id),
                               PRIMARY KEY (group_id, user_id)
);

CREATE TRIGGER prevent_owner_from_leaving
    BEFORE DELETE ON group_members
    FOR EACH ROW
BEGIN
    -- Verifica se o user que está a tentar sair do grupo é owner do mesmo
    SELECT RAISE(ABORT, 'O administrador do grupo não pode abandonar o grupo sem antes apontar um novo administrador')
        WHERE OLD.user_id = (SELECT owner_id FROM groups WHERE id = OLD.group_id);
END;

CREATE TRIGGER ensure_owner_is_group_member
    BEFORE UPDATE OF owner_id ON groups
    FOR EACH ROW
BEGIN
    -- Verifica se o novo owner é um membro do grupo
    SELECT RAISE(ABORT, 'O novo administrador do grupo deve ser um membro do mesmo')
        WHERE NOT EXISTS (
        SELECT 1 FROM group_members
        WHERE group_id = OLD.id AND user_id = NEW.owner_id
    );
END;

CREATE TRIGGER auto_add_creator_to_group
    AFTER INSERT ON groups
    FOR EACH ROW
BEGIN
    -- Insert the group owner into group_members only if not already a member
    INSERT INTO group_members (group_id, user_id)
    SELECT NEW.id, NEW.owner_id
        WHERE NOT EXISTS (
        SELECT 1
        FROM group_members
        WHERE group_id = NEW.id AND user_id = NEW.owner_id
    );
END;

CREATE TRIGGER ignore_duplicate_group_members
    BEFORE INSERT ON group_members
    FOR EACH ROW
BEGIN
    -- Ignore the insert if the same group_id and user_id already exist in group_members
    SELECT RAISE(IGNORE)
        WHERE EXISTS (
        SELECT 1
        FROM group_members
        WHERE group_id = NEW.group_id AND user_id = NEW.user_id
    );
END;

------------------------------------------------- Invites --------------------------------------------------------------

CREATE TABLE invites (
                         id              INTEGER CONSTRAINT invite_pk    PRIMARY KEY AUTOINCREMENT,
                         creation_date   TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                         status          TEXT          NOT NULL DEFAULT 'pending' CONSTRAINT chk_estado CHECK (status IN ('pending', 'accepted', 'rejected')),
                         group_id        INTEGER CONSTRAINT group_id REFERENCES groups(id),
                         invitee_id      INTEGER CONSTRAINT invitee_id   REFERENCES users(id),
                         inviter_id      INTEGER CONSTRAINT inviter_id REFERENCES users(id),
                         CONSTRAINT chk_no_self_invite CHECK (inviter_id != invitee_id)
    );

CREATE TRIGGER prevent_duplicate_invite
    BEFORE INSERT ON invites
    FOR EACH ROW
BEGIN
    -- Verifica se o convidado já faz parte do grupo em questão
    SELECT RAISE(ABORT, 'Este utilizador já faz parte do grupo em questão')
        WHERE EXISTS (SELECT 1 FROM group_members
                  WHERE group_id = NEW.group_id AND user_id = NEW.invitee_id);
END;

CREATE TRIGGER prevent_self_invite
    BEFORE INSERT ON invites
    FOR EACH ROW
BEGIN
    -- Abort if the inviter is the same as the invitee
    SELECT RAISE(ABORT, 'Um utilizador não se pode convidar a ele próprio')
        WHERE NEW.inviter_id = NEW.invitee_id;
END;

CREATE TRIGGER auto_add_group_member_on_accept
    AFTER UPDATE OF status ON invites
    FOR EACH ROW
BEGIN
    -- Insert the invited user into group_members only if not already a member
    INSERT INTO group_members (group_id, user_id)
    SELECT NEW.group_id, NEW.invitee_id
        WHERE NEW.status = 'accepted'
      AND NOT EXISTS (
        SELECT 1
        FROM group_members
        WHERE group_id = NEW.group_id AND user_id = NEW.invitee_id
    );
END;

------------------------------------------------- Expenses -------------------------------------------------------------

CREATE TABLE expenses (
        id INTEGER CONSTRAINT expense_pk PRIMARY KEY AUTOINCREMENT,
        creation_date   TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
        description     TEXT(255)           NOT NULL,
        value           DECIMAL(10, 2)      NOT NULL,
        paid            BOOLEAN             NOT NULL DEFAULT false,
        paid_by         INTEGER REFERENCES users(id),
        group_id        INTEGER REFERENCES groups(id)
);

-- Tabela de Relação Expenses <-> Users | Conecta as Despesas aos Users pelos quais deve ser dividida
CREATE TABLE expense_shares (
                                id INTEGER CONSTRAINT expense_shares PRIMARY KEY AUTOINCREMENT,
                                expense_id  INTEGER REFERENCES expenses(id),
                                user_id     INTEGER REFERENCES users(id),
                                share       DECIMAL(10, 2)      NOT NULL,
                                paid        BOOLEAN  NOT NULL,
                                group_id INTEGER REFERENCES groups(id)
);

CREATE TRIGGER delete_expense_shares
    AFTER DELETE ON expenses
    FOR EACH ROW
BEGIN
    DELETE FROM expense_shares
    WHERE expense_id = OLD.id;
END;

------------------------------------------------- Payments -------------------------------------------------------------

CREATE TABLE payments (
        id              INTEGER     CONSTRAINT user_pk          PRIMARY KEY AUTOINCREMENT,
        payment_date    TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
        value           DECIMAL(10, 2)      NOT NULL,
        payer           INTEGER REFERENCES users(id),
        expense_id      INTEGER REFERENCES expenses(id)
);

CREATE TRIGGER prevent_overpayment
    BEFORE INSERT ON payments
    FOR EACH ROW
BEGIN
    -- Verifica se o user está a tentar pagar mais do que a sua parte da expense
    SELECT RAISE(ABORT, 'Estás a tentar pagar mais do que deves!')
        WHERE NEW.value > (
        SELECT share
        FROM expense_shares
        WHERE expense_id = NEW.expense_id AND user_id = NEW.payer
    );
END;

--------------------------------------------------- Debts --------------------------------------------------------------

CREATE TABLE debts (
                    id            INTEGER     PRIMARY KEY AUTOINCREMENT,
                    debtor_id     INTEGER     NOT NULL REFERENCES users(id),
                    creditor_id   INTEGER     NOT NULL REFERENCES users(id),
                    group_id      INTEGER     NOT NULL REFERENCES groups(id),
                    debt_value    DECIMAL(10, 2) NOT NULL,
                    paid          BOOLEAN     NOT NULL DEFAULT false,
                    creation_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                    CONSTRAINT chk_no_self_debt CHECK (debtor_id != creditor_id),
                    CONSTRAINT unique_debt_pair UNIQUE (debtor_id, creditor_id, group_id)
);

CREATE TRIGGER auto_insert_debts
    AFTER INSERT ON expenses
    FOR EACH ROW
BEGIN
    INSERT INTO debts (debtor_id, creditor_id, debt_value, paid, group_id)
    SELECT
        gm.user_id,
        NEW.paid_by,
        NEW.value / (
            (SELECT COUNT(*) - 1
             FROM group_members
             WHERE group_id = NEW.group_id)
        ),
        FALSE,
        NEW.group_id
    FROM group_members gm
    WHERE gm.group_id = NEW.group_id
      AND gm.user_id != NEW.paid_by
      AND (SELECT COUNT(*) - 1
           FROM group_members
           WHERE group_id = NEW.group_id) > 0
      AND NEW.value > 0
    ON CONFLICT (debtor_id, creditor_id, group_id) DO UPDATE
                                                          SET debt_value = debt_value + EXCLUDED.debt_value;
END;

------------------------------------------------- DB_Version -------------------------------------------------------------

CREATE TABLE db_versions (
                             id  INTEGER CONSTRAINT db_versions_pk
);