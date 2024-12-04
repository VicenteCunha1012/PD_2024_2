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
import pt.isec.pd.Server.Helper.Helper;
import pt.isec.pd.Server.RMI.GetAppInfoImpl;
import pt.isec.pd.Server.RMI.NotificationServerImpl;
import pt.isec.pd.Server.RMI.ServiceRegisterer;
import pt.isec.pd.Server.Springboot.security.RsaKeysProperties;

import java.io.File;
import java.rmi.RemoteException;
import java.time.Instant;

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
        String APP_INFO_URI = "rmi://localhost:1099/get_app_info";
        String NOTIFICATION_SERVER = "rmi://localhost:1100/update_server";

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

        Helper.sbAppContext =  SpringApplication.run(MainServer.class, args);

        if(!ServiceRegisterer.CreateRegistry(1099) || !ServiceRegisterer.CreateRegistry(1100)) {
            System.out.println("Não foi possível exportar o registo, já deve ter sido criado.");
        }

        GetAppInfoImpl appInfoImpl;
        try {
            appInfoImpl = new GetAppInfoImpl();
            NotificationServerImpl.instance = new NotificationServerImpl();
        } catch (RemoteException e) {
            System.out.println("Não foi possível iniciar uma ou mais implementações.");
            Helper.shutDown();
            return;
        }

        if(ServiceRegisterer.BindRegistrationToImplementation(APP_INFO_URI, appInfoImpl)) {
            System.out.println("Implementação de GetAppInfo binded a " + APP_INFO_URI);
        } else {
            if(ServiceRegisterer.RebindRegistrationToImplementation(APP_INFO_URI, appInfoImpl)) {
                System.out.println("Deu para fazer rebind. A implementação de GetAppInfo está binded a " + APP_INFO_URI);
            } else {
                System.out.println("Não foi possível fazer bind nem rebind a " + APP_INFO_URI);
                Helper.shutDown();
                return;
            }
            System.out.println("Não foi possível fazer bind da implementação de GetAppInfo a " + APP_INFO_URI + "... \n Vou tentar fazer rebind");
        }

        if(ServiceRegisterer.BindRegistrationToImplementation(NOTIFICATION_SERVER, NotificationServerImpl.instance)) {
            System.out.println("Implementação de NotificationServer binded a " + NOTIFICATION_SERVER);
        } else {
            if(ServiceRegisterer.RebindRegistrationToImplementation(NOTIFICATION_SERVER, NotificationServerImpl.instance)) {
                System.out.println("Deu para fazer rebind. A implementação de NotificationServer está binded a "+ NOTIFICATION_SERVER);
            } else {
                System.out.println("Não foi possível fazer bind nem rebind a " + NOTIFICATION_SERVER);
                Helper.shutDown();
                return;
            }
        }







    }
}