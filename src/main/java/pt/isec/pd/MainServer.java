package pt.isec.pd;

import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import com.nimbusds.jose.proc.SecurityContext;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import pt.isec.pd.Server.Data.Database;
import pt.isec.pd.Server.Springboot.security.RsaKeysProperties;

import java.io.File;
import java.sql.SQLException;

@SpringBootApplication
@EnableConfigurationProperties(RsaKeysProperties.class)
@ConfigurationPropertiesScan
public class MainServer {

    private RsaKeysProperties rsaKeysProperties;

    public MainServer(RsaKeysProperties rsaKeysProperties) {
        this.rsaKeysProperties = rsaKeysProperties;
    }

    @Bean
    public JwtEncoder jwtEncoder() {
        // Build the RSA JWK with your public and private keys
        JWK rsaJwk = new RSAKey.Builder(rsaKeysProperties.publicKey())  // Public key
                .privateKey(rsaKeysProperties.privateKey())  // Private key
                .build();

        // Create a JWK set with the created JWK
        JWKSet jwkSet = new JWKSet(rsaJwk);

        // Create an ImmutableJWKSet (which implements JWKSource<SecurityContext>)
        ImmutableJWKSet<SecurityContext> jwkSource = new ImmutableJWKSet<>(jwkSet);

        // Create and return the JwtEncoder
        return new NimbusJwtEncoder(jwkSource);
    }


    @Bean
    public JwtDecoder jwtDecoder() {
        // Use the public key for verification
        JWK jwk = new RSAKey.Builder(rsaKeysProperties.publicKey())
                .build();

        return NimbusJwtDecoder.withPublicKey(rsaKeysProperties.publicKey()).build();
    }


    public static void main(String[] args) {

        if (args.length != 1) {
            System.out.println("Sintaxe java Servidor DBPath");
            return;
        }


        File dbPath = new File(args[0]);

        boolean dbFileExists = dbPath.exists();

        if (dbFileExists) {
            if(!dbPath.canRead() || !dbPath.canWrite()){
                System.out.println("Falta de permissões no ficheiro da base de dados.");
                return;
            }
        }

        try {
            Database.database.setupDB(args[0], dbFileExists);
        } catch (Exception e) {
            System.out.println("Erro a iniciar ao iniciar BD: \"" + e.getMessage() + '\"');
            return;
        }

        SpringApplication.run(MainServer.class, args);
    }
}