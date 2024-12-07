------------------------------------------------- Users ----------------------------------------------------------------
CREATE TABLE users (
                       id              INTEGER     CONSTRAINT user_pk PRIMARY KEY AUTOINCREMENT,
                       creation_date   TIMESTAMP   DEFAULT CURRENT_TIMESTAMP,
                       name            TEXT    NOT NULL,
                       contact         TEXT    NOT NULL    CONSTRAINT user_pk_2    UNIQUE,
                       email           TEXT    NOT NULL    CONSTRAINT user_pk_3    UNIQUE,
                       password        TEXT    NOT NULL
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

------------------------------------------------- DB_Version -------------------------------------------------------------

CREATE TABLE db_versions (
                             id  INTEGER CONSTRAINT db_versions_pk
);