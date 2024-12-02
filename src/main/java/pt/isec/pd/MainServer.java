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
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.OAuth2TokenValidatorResult;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import pt.isec.pd.Server.Data.Database;
import pt.isec.pd.Server.Springboot.security.RsaKeysProperties;

import java.io.File;
import java.time.Instant;

@SpringBootApplication
@EnableConfigurationProperties(RsaKeysProperties.class)
@ConfigurationPropertiesScan
public class    MainServer {

    private RsaKeysProperties rsaKeysProperties;

    public MainServer(RsaKeysProperties rsaKeysProperties) {
        this.rsaKeysProperties = rsaKeysProperties;
    }

    @Bean
    public JwtEncoder jwtEncoder() {
        JWK rsaJwk = new RSAKey.Builder(rsaKeysProperties.publicKey())
                .privateKey(rsaKeysProperties.privateKey())
                .build();

        JWKSet jwkSet = new JWKSet(rsaJwk);

        ImmutableJWKSet<SecurityContext> jwkSource = new ImmutableJWKSet<>(jwkSet);

        return new NimbusJwtEncoder(jwkSource);
    }


    /*
    @Bean
    public JwtDecoder jwtDecoder() {
        return NimbusJwtDecoder.withPublicKey(rsaKeysProperties.publicKey()).build();
    }*/

    @Bean
    public JwtDecoder jwtDecoder() {

        NimbusJwtDecoder jwtDecoder = NimbusJwtDecoder.withPublicKey(rsaKeysProperties.publicKey()).build();

        jwtDecoder.setJwtValidator(token -> {
            if (token.getExpiresAt().isBefore(Instant.now())) {

                return OAuth2TokenValidatorResult.failure(new OAuth2Error("invalid_token", "Token expired", null));
            }
            return OAuth2TokenValidatorResult.success();
        });

        return jwtDecoder;
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