package pt.isec.pd;

import pt.isec.pd.Client.Logic.ClientManager;
import pt.isec.pd.Client.UI.ClientUI;

import java.io.IOException;

public class MainClient {
    public static void main(String[] args) throws IOException {

        if (args.length != 2) {
            System.out.println("sintaxe Java Client ServerIP ServerPort");
            return;
        }

        System.out.println("<Client> Bom dia!");


        ClientUI clientUI = new ClientUI(
                new ClientManager(args[0], Integer.parseInt(args[1]))
        );

        clientUI.start();

    }
}
