package pt.isec.pd.Client.UI;

import pt.isec.pd.Client.Logic.Client;
import pt.isec.pd.Client.Logic.Requests.AuthRequests;
import pt.isec.pd.Client.Logic.Requests.ExpenseRequests;
import pt.isec.pd.Client.Logic.Requests.GroupRequests;
import pt.isec.pd.Shared.AccessLevel;
import pt.isec.pd.Shared.Entities.*;
import pt.isec.pd.Shared.IO;

import java.io.IOException;
import java.sql.Date;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Interface de utilizador do cliente
 */
public class ClientUI {
    public static boolean isRunning = true;
    private Client client;

    public ClientUI(Client client) {
        this.client = client;
    }

    /**
     * Começar loop da UI
     * @throws IOException
     */
    public void start() throws IOException { this.menu(); }

    /**
     * Starting point de cada iteração do menu, dependendo do "nível" da ui, mostrar menus diferentes
     */
    public void menu() {
        while(isRunning) {
            IO.clearScreen();
            try {
                switch(client.getAccessLevel()) {
                    case EXIT:
                        isRunning = false;
                        break;
                    case BEFORE_LOGIN:
                        this.startingMenu();
                        break;
                    case BEFORE_GROUP_SELECT:
                        this.groupSelectMenu();
                        break;
                    case IN_GROUP_CONTEXT:
                        this.groupActionsMenu();
                        break;
                    default:
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    /**
     * Menu da UI para quando o access level é BEFORE_LOGIN
     * @throws IOException
     */
    private void startingMenu() throws IOException {
        String userName;
        String email;
        String contacto;
        String password;

        AuthRequests authRequests = new AuthRequests(client.getUrl());

        switch (IO.chooseOption(" +--------------------------- Menu Principal ---------------------------+ ", "",
                "Registar", "Iniciar sessão", "Sair"
        )) {

            case 1:
                userName = IO.readString("  Nome de Utilizador > ", false);

                email = IO.readString("  Email > ", false);

                contacto = IO.readString("  Telefone > ", false);

                password = IO.readString("  Palavra-passe > ", false);

                if (!authRequests.register(new User(userName, contacto, email, password))) {
                    System.out.println("  Falha ao efetuar registo!");
                    System.out.println("  Prima ENTER para tentar novamente");
                    System.in.read();
                } else {
                    System.out.println("  Registado com successo!!");
                    System.out.println("  Prima ENTER para continuar");
                    System.in.read();
                }

                break;

            case 2:
                email = IO.readString("  Email > ", false);

                password = IO.readString("  Palavra-passe > ", false);

                String token = authRequests.login(email, password);

                if (token == null) {
                    System.out.println("  Nome de Utilizador ou Palavra-Passe incorreto!");
                } else {
                    client.setToken(token);
                    client.setEmail(email);
                    client.setAccessLevel(AccessLevel.BEFORE_GROUP_SELECT);
                }

                break;
            case 3:
                this.isRunning = false;
                break;

            default:
                System.out.println("Isso não é nada.🫤");

        }

    }

    /**
     * Menu da UI para quando o access level é BEFORE_GROUP_SELECT
     * @throws Exception
     */
    private void groupSelectMenu() throws Exception {
        int option = -1;
        List<ListedGroup> groupsList;

        GroupRequests groupRequests = new GroupRequests(client.getUrl());

        while ((groupsList = groupRequests.listGroups(client.getToken())) == null) {
            switch (IO.chooseOption("  Falha ao listar os seus grupos", "", "Tentar novamente", "Terminar sessão", "Sair")) {
                case 2:
                    client.setAccessLevel(AccessLevel.BEFORE_LOGIN);
                    client.setToken("");
                    break;
                case 3:
                    this.isRunning = false;
                    client.setToken("");
                    break;
                default:
                    break;
            }
            return;
        }

        System.out.println("\n +--------------------------- Os meus Grupos ---------------------------+ ");

        for (int i = 0; i < groupsList.size(); ++i) {
            System.out.println("  " + (i+1) + " - " + groupsList.get(i).getName());
        }
        System.out.println("  0 - Voltar");

        while ((option) < 0 || (option-1) > groupsList.size() - 1) {
            option = IO.readInt("\n  Escolha o seu grupo > ");
        }

        if (option == 0) {
            client.setAccessLevel(AccessLevel.BEFORE_LOGIN);
            return;
        } else {
            client.setAccessLevel(AccessLevel.IN_GROUP_CONTEXT);
            client.setTargetGroupName(groupsList.get(option-1).getName());
        }

    }

    /**
     * Menu da UI para quando o access level é IN_GROUP_CONTEXT_ADMIN ou IN_GROUP_CONTEXT_MEMBER
     * @throws Exception
     */
    private void groupActionsMenu() throws Exception {
        int option = -2;

        ExpenseRequests expenseRequests = new ExpenseRequests(client.getUrl(), client.getTargetGroupName());
        GroupRequests groupRequests = new GroupRequests(client.getUrl());

        while (true) {
            switch (IO.chooseOption("+----------------------- Grupo " + client.getTargetGroupName() + " ------------------------+ ",
                    "",  "Adicionar Despesa", "Listar Despesas", "Eliminar despesa", "Voltar"
            )) {
                    /*
                    CASE ADICIONAR DESPESA
                    */
                case 1:
                    String description;
                    int my_id = -1;
                    double value;
                    List<Integer> debtors = new ArrayList<>();
                    List<ListedUser> users = groupRequests.listGroupMembers(client.getTargetGroupName(), client.getToken());

                    if (users == null) {
                        System.out.println("  Erro ao listar os membros do grupo!!");
                        System.out.println("  Prima ENTER para voltar!");
                        System.in.read();
                        return;
                    }

                    for (ListedUser user : users) {
                        if (user.getEmail().equals(client.getEmail())) {
                            my_id = user.getId();
                        }
                    }

                    users.removeIf(user -> user.getEmail().equals(client.getEmail()));

                    description = IO.readString("  Descrição da despesa > ", false);
                    do {
                        value = IO.readNumber("  Valor da despesa > ");
                    } while (value <= 0);

                    System.out.println("  Com quem partilhar a despesa?");

                    for (int i = 0; i < users.size(); i++) {
                        System.out.println("   \t" + (i+1) + " - " + users.get(i).toString());
                    }

                    System.out.println("   \t0 - Terminar\n  \t-1 - Cancelar\n");

                    while (true) {
                        option = IO.readInt("   > ");

                        if (option == 0 || option == -1) { break; }
                        else if (
                                (option-1) < users.size() && (option-1) > -1 &&
                                        !debtors.contains(users.get(option-1).getId())
                        ) {
                            debtors.add(users.get(option-1).getId());
                            System.out.println("Adicionei!");
                        }
                    }

                    if (option == -1) { break;}

                    switch (
                            IO.chooseOption("Também vais pagar uma parte da despesa?", "", "Sim", "Não")
                    ) {
                        case 1:
                            if (my_id != -1) { debtors.add(my_id); }
                            break;
                        case 2:
                            break;
                        default:
                            continue;
                    }

                    if (expenseRequests.addGroupExpense(
                            new Expense(
                                    Date.valueOf(LocalDateTime.now().toLocalDate()),
                                    description,
                                    value,
                                    my_id,
                                    client.getTargetGroupId(),
                                    debtors
                            ),
                            client.getToken()
                    )) {
                        System.out.println("  Despesa inserida com sucesso.");
                        System.out.println("  Prima ENTER para continuar");
                        System.in.read();
                    } else {
                        System.out.println("  Ocorreu um erro a inserir esta despesa.");
                        System.out.println("  Prima ENTER para voltar");
                        System.in.read();
                    }

                    break;

                    /*
                    CASE LISTAR DESPESAS
                    */
                case 2:
                    List<ListedExpense> list = expenseRequests.listGroupExpenses(client.getToken());

                    if (list == null) {
                        System.out.println("  Algo de errado aconteceu ao listar as despesas do grupo '" + client.getTargetGroupName() + "'");
                    } else {
                        System.out.println("\n  + Despesas ------------------------------+\n");
                        for (int i = 0; i < list.size(); i++) {
                            System.out.println("\t" + (i+1) + ".\n\t-----" + list.get(i).toString());
                        }
                        System.out.println("  +----------------------------------------+");
                    }

                    System.out.println("  Prima ENTER para continuar");
                    System.in.read();

                    break;

                    /*
                    CASE ELIMINAR DESPESA
                    */
                case 3:
                    int ex_id = -1;
                    List<ListedExpense> listToDelete = expenseRequests.listGroupExpenses(client.getToken());

                    if (listToDelete == null) {
                        System.out.println("  Algo de errado aconteceu ao listar as despesas do grupo '" + client.getTargetGroupName() + "'");
                        System.out.println("  Prima ENTER para voltar");
                        System.in.read();
                        return;
                    } else {
                        System.out.println("\n  + Despesas ------------------------------+\n");
                        for (int i = 0; i < listToDelete.size(); i++) {
                            System.out.println("\t" + (i+1) + ".\n\t-----" + listToDelete.get(i).toString());
                        }
                        System.out.println("\t0. Cancelar");
                        System.out.println("  +----------------------------------------+");

                        System.out.println("\n  Selecione a despesa que pretende eliminar");

                        while (true) {
                            option = IO.readInt("   > ");

                            if (option == 0) { return; }

                            if ((option-1) >= listToDelete.size() || (option-1) < 0) {
                                continue;
                            } else {
                                ex_id = listToDelete.get(option-1).getId();
                                break;
                            }

                        }

                        if (expenseRequests.deleteGroupExpense(ex_id, client.getToken())) {
                            System.out.println("  Despesa eliminada com sucesso");
                            System.out.println("  Prima ENTER para continuar");
                            System.in.read();
                        } else {
                            System.out.println("  Ocorreu um erro ao eliminar a despesa.");
                            System.out.println("  Prima ENTER para voltar");
                            System.in.read();
                            return;
                        }
                    }

                    break;

                    /*
                    CASE VOLTAR
                    */
                case 4:
                    client.setAccessLevel(AccessLevel.BEFORE_GROUP_SELECT);
                    return;
            }
        }
    }

}


