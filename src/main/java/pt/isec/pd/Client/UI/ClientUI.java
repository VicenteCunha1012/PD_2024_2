package pt.isec.pd.Client.UI;

import ch.qos.logback.core.net.server.Client;
import org.springdoc.core.properties.SpringDocConfigProperties;
import pt.isec.pd.Client.Logic.ClientManager;
import pt.isec.pd.Client.Logic.Requests.AuthRequests;
import pt.isec.pd.Client.Logic.Requests.GroupRequests;
import pt.isec.pd.Shared.AccessLevel;
import pt.isec.pd.Shared.Entities.Group;
import pt.isec.pd.Shared.Entities.ListedGroup;
import pt.isec.pd.Shared.Entities.User;
import pt.isec.pd.Shared.IO;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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
        }

        System.out.println("\n +--------------------------- Os meus Grupos ---------------------------+ ");

        for (int i = 0; i < groupsList.size(); ++i) {
            System.out.println( "  " + (i+1) + ". " + groupsList.get(i).getName());
        }

        while (option < 0 || option > groupsList.size() - 1) {
            option = IO.readInt("  > ");
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

        String invitee;
        String expenseDescription;
        double value;
        ArrayList<Integer> groupMembersId;

        String novoNome;

        while (true) {
            switch (IO.chooseOption("+----------------------- Grupo " + clientManager.getTargetGroupName() + " ------------------------+ ",
                    "", "Alterar nome do grupo", "Convidar", "Adicionar Despesa", "Editar despesa",
                    "Eliminar despesa", "Pagar despesa", "Listar histórico de despesas", "Listar Pagamentos",
                    "Eliminar Pagamento", "Exportar Despesas para ficheiro CSV", "Ver total de gastos do grupo",
                    "Ver saldo e informações", "Eliminar Grupo", "Sair do Grupo", "Voltar"
            )) {

                /*
                CASE ALTERAR NOME
                 */
                case 1:
                    novoNome = IO.readString("   Novo nome > ", false);

                    break;
                /*
                CASE CONVIDAR
                 */
                case 2:
                    invitee = IO.readString("   E-mail do convidado > ", true);


                    break;

                    /*
                    CASE ADICIONAR DESPESA
                     */
                case 3:

            }
        }
    }

}


