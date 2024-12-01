package pt.isec.pd.Client.UI;

import pt.isec.pd.Shared.Hasher;
import pt.isec.pd.Shared.IO;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Interface de utilizador do cliente
 */
public class ClientUI {
    public static boolean isRunning = true;
    private String message = "";

    public ClientUI() {}

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
        String fullName;
        String email;
        String phoneNumber;
        String password;

        switch (IO.chooseOption("+--------------------------- Menu Principal ---------------------------+ ", "",
                "Registar", "Iniciar sessão", "Sair"
        )) {

            case 1:
                fullName = IO.readString("  Nome completo > ", false);

                email = IO.readString("  Email > ", false);

                phoneNumber = IO.readString("  Telefone > ", false);

                password = Hasher.HashString(IO.readString("  Palavra-passe > ", false));

                break;
            case 2:

                email = IO.readString("  Email > ", false);

                password = Hasher.HashString(IO.readString("  Palavra-passe > ", false));


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
    private void SecondMenu() throws Exception {
        String fullName;
        String phoneNumber;
        String password;
        String newGroupName;
        int inviteId;
        String response;

        this.ShowUserInfoMenu();

        switch (IO.chooseOption(" +-------------------------------- Menu --------------------------------+ ", "",
                "Editar perfil", "Selecionar grupo", "Criar grupo", "Os meus convites", "Os meus grupos", "Terminar Sessão"
        )) {
            /*
            ------------------
            CASE EDITAR PERFIL
            -----------------
             */
            case 1:
                System.out.println("Se não quiser mudar os valores, repita-os");
                fullName = IO.readString("  Nome completo > ", false);
                phoneNumber = IO.readString("  Telefone > ", true);
                password = Hasher.HashString(IO.readString("  Palavra-passe > ", false));

                break;

                /*
                --------------------
                CASE SELECIONAR GRUPO
                ---------------------
                 */
            case 2:
                //o cliente pede ao servidor para lhe dizer a lista de grupos em que ele está


                System.out.println(" +------------------------------- Grupos -------------------------------+");

                System.out.println(" +----------------------------------------------------------------------+");

                int target_group_id = IO.readInt("   ID do grupo a selecionar > ");



                break;

                /*
                ----------------
                CASE CRIAR GRUPO
                ----------------
                 */
            case 3:
                newGroupName = IO.readString("  Nome do grupo > ", false);


                break;

                /*
                ---------------------
                CASE OS MEUS CONVITES
                ---------------------
                 */
            case 4:




                break;

                /*
                -------------
                LISTAR GRUPOS
                -------------
                 */
            case 5:

                    System.out.println(" +------------------------------- Grupos -------------------------------+");

                    System.out.println(" +----------------------------------------------------------------------+");

                break;

                /*
                ---------------
                TERMINAR SESSÃO
                ---------------
                 */
            case 6:

                break;

            default:
                this.message = "Isso não é nada. 🫤";
        }

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
            switch (IO.chooseOption("+----------------------- Grupo ------------------------+ ",
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

    /**
     * Informações básicas sobre o utilizador (provenientes do Singleton de BasicUserInfo)
     */
    private void ShowUserInfoMenu() {

        System.out.printf(
                " +------------------------- As suas informações ------------------------+\n  Nome: %s\tEmail: %s\tTelefone: %s\t"
        );
    }

}


