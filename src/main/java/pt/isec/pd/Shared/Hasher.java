package pt.isec.pd.Shared;

import pt.isec.pd.Shared.Entities.User;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

/**
 * Classe para fazer hash das passwords
 */
public class Hasher {
    /**
     * Função para fazer hash das passwords
     * @param input é hashed com o SHA-256
     * @return é o input depois de ser hashed
     */
    public static String HashString(String input) {
        MessageDigest digest;
        try {
            digest = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            return "shouldHash" + input;
        }

        byte[] hashBytes = digest.digest(input.getBytes());

        return Base64.getEncoder().encodeToString(hashBytes);
    }

    public static User HashUserPass(User user) {
        return new User(
                user.getName(),
                user.getContact(),
                user.getEmail(),
                Hasher.HashString(user.getPassword())
        );
    }
}
