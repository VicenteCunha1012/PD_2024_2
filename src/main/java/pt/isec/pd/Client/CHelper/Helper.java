package pt.isec.pd.Client.CHelper;

import java.io.File;

/**
 * Classe de funções auxiliares para o Client
 */
public class Helper {
    /**
     * No contexto de escrever um ficheiro csv, só o podemos fazer quando o ficheiro ainda não existir e
     * pudermos escrever-lhe
     * @param filePath path do ficheiro a verificar
     * @return resultado
     */
    public static int checkFilePath(String filePath){
        File file = new File(filePath);
        if(file.exists()) return -1; //o ficheiro ja existe, façamos so para escrever em ficheiros novos
        if(!file.canWrite()) return -2; //nao da para escrever

        return 0;
    }
}
