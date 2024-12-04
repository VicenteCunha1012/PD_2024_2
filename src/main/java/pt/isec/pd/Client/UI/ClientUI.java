package pt.isec.pd.Client.UI;

import ch.qos.logback.core.net.server.Client;
import org.springdoc.core.properties.SpringDocConfigProperties;
import pt.isec.pd.Client.Logic.ClientManager;
import pt.isec.pd.Client.Logic.Requests.AuthRequests;
import pt.isec.pd.Client.Logic.Requests.GroupRequests;
import pt.isec.pd.Shared.AccessLevel;
import pt.isec.pd.Shared.Entities.Group;
import pt.isec.pd.Shared.Entities.ListedExpense;
import pt.isec.pd.Shared.Entities.ListedGroup;
import pt.isec.pd.Shared.Entities.User;
import pt.isec.pd.Shared.IO;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Interface de utilizador do cliente
 */
public class ClientUI {
    public static boolean isRunning = true;
    private String message = "";
    private ClientManager clientManager;

    public ClientUI(ClientManager clientManager) {
        this.clientManager = clientManager;
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
                if(this.message != "") {
                    System.out.println("--------------------------");
                    System.out.println(this.message);
                    this.message = "";
                    System.out.println("--------------------------");
                }
                switch(clientManager.getAccessLevel()) {
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

        switch (IO.chooseOption(" +--------------------------- Menu Principal ---------------------------+ ", "",
                "Registar", "Iniciar sessão", "Sair"
        )) {

            case 1:
                userName = IO.readString("  Nome de Utilizador > ", false);

                email = IO.readString("  Email > ", false);

                contacto = IO.readString("  Telefone > ", false);

                password = IO.readString("  Palavra-passe > ", false);

                if (!AuthRequests.register(new User(userName, contacto, email, password), clientManager.getUrl())) {
                    System.err.println("  Falha ao efetuar registo!");
                } else {
                    System.out.println("  Registado com successo!!");
                }

                break;

            case 2:
                email = IO.readString("  Email > ", false);

                password = IO.readString("  Palavra-passe > ", false);

                String token = AuthRequests.login(email, password, clientManager.getUrl());

                if (token == null) {
                    System.err.println("  Nome de Utilizador ou Palavra-Passe incorreto!");
                } else {
                    clientManager.setToken(token);
                    clientManager.setEmail(email);
                    clientManager.setAccessLevel(AccessLevel.BEFORE_GROUP_SELECT);
                }

                break;
            case 3:
                this.isRunning = false;
                break;

            default:
                this.message = "Isso não é nada.🫤";

        }

    }

    /**
     * Menu da UI para quando o access level é BEFORE_GROUP_SELECT
     * @throws Exception
     */
    private void groupSelectMenu() throws Exception {
        List<ListedGroup> groupsList;
        int option = -1;

        while ((groupsList = GroupRequests.listGroups(clientManager.getUrl(), clientManager.getEmail(), clientManager.getToken())) == null) {
            switch (IO.chooseOption("  Falha ao listar os seus grupos", "", "Tentar novamente", "Terminar sessão", "Sair")) {
                case 2:
                    clientManager.setAccessLevel(AccessLevel.BEFORE_LOGIN);
                    clientManager.setToken("");
                    break;
                case 3:
                    this.isRunning = false;
                    clientManager.setToken("");
                    break;
                default:
                    break;
            }
            return;
        }

        System.out.println("\n +--------------------------- Os meus Grupos ---------------------------+ ");

        for (int i = 0; i < groupsList.size(); ++i) {
            System.out.println( "  " + (i+1) + ". " + groupsList.get(i).getName());
        }

        while (option < 0 || option > groupsList.size() - 1) {
            option = IO.readInt("\n  Escolha o seu grupo > ");
        }

        clientManager.setAccessLevel(AccessLevel.IN_GROUP_CONTEXT);

        clientManager.setTargetGroupName(groupsList.get(option-1).getName());
    }

    /**
     * Menu da UI para quando o access level é IN_GROUP_CONTEXT_ADMIN ou IN_GROUP_CONTEXT_MEMBER
     * @throws Exception
     */
    private void groupActionsMenu() throws Exception {
        int option;

        String novoNome;

        while (true) {
            switch (IO.chooseOption("+----------------------- Grupo " + clientManager.getTargetGroupName() + " ------------------------+ ",
                    "",  "Adicionar Despesa", "Listar Despesas", "Eliminar despesa", "Voltar"
            )) {
                    /*
                    CASE ADICIONAR DESPESA
                    */
                case 1:
                    String expenseDescription;
                    double value;
                    ArrayList<Integer> groupMembersId;

                    expenseDescription = IO.readString("  Descrição da despesa > ", false);
                    do {
                        value = IO.readNumber("  Valor da despesa > ");
                    } while (value <= 0);

                    System.out.println("   Com quem partilhar a despesa?");

                    System.out.println("   \t0. Terminar\n  \t-1. Cancelar\n");


                    switch (
                            IO.chooseOption("Também vais pagar uma parte da despesa?", "", "Sim", "Não")
                    ) {

                    }


                    if (GroupRequests.addGroupExpense(clientManager.getTargetGroupName(),)) {
                        this.message = "Despesa inserida com sucesso.";
                    } else {
                        this.message = "Ocorreu um erro a inserir esta despesa.";
                    }

                    break;

                    /*
                    CASE LISTAR DESPESAS
                    */
                case 2:
                    List<ListedExpense> list = GroupRequests.listGroupExpenses(
                            clientManager.getTargetGroupName(),
                            clientManager.getUrl()
                    );

                    System.out.println("\n  + Despesas ------------------------------+");
                    for (ListedExpense l : list) {
                        System.out.println(l.toString());
                    }
                    System.out.println("\n  +----------------------------------------+");

                    System.out.println("Prima ENTER para continuar");
                    System.in.read();

                    break;

                    /*
                    CASE ELIMINAR DESPESA
                    */
                case 3:

                    break;

                    /*
                    CASE VOLTAR
                    */
                case 4:
                    clientManager.setAccessLevel(AccessLevel.BEFORE_GROUP_SELECT);
                    return;
            }
        }
    }

}


