package pt.isec.pd;

import pt.isec.pd.ObservableClient.RMIHelper.RMIHelper;
import pt.isec.pd.ObservableClient.UI.InputHandler;
import pt.isec.pd.Server.RMI.GetAppInfo;
import pt.isec.pd.Server.RMI.GetAppInfoImpl;
import pt.isec.pd.Shared.IO;

import static pt.isec.pd.ObservableClient.RMIHelper.RMIHelper.GetRemoteReference;

public class MainObservableClient {
    //ligar ao servico
    //ver se da pra fazer so com um thread
    //se nao é a vida
    public static void main(String[] args) {
        if(args.length != 1) {
            System.out.println("Sintaxe java MainObservableClient rmi_uri");
        }
        GetAppInfo remoteImpl = RMIHelper.GetRemoteReference(args[0]);
        if(remoteImpl == null) {
            System.out.println("Ocorreu um erro a obter a referência remota.");
            System.exit(1);
        } else {
            System.out.println("Conexão bem sucedida");
        }

        String input;
        boolean keepRunning = true;
        //ainda falta logica de observable
        while (keepRunning) {
            input = IO.readString("Comando > ", false);
            try {
                keepRunning = InputHandler.HandleInput(input, remoteImpl);
            } catch (Exception e) {
                System.out.println("Ocorreu um erro a tentar correr uma função remota: "+ e.getMessage());
            }
        }

    }
}
