package pt.isec.pd.Server.Springboot.security;


import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;
import pt.isec.pd.Server.Data.Database;
import pt.isec.pd.Server.Data.DatabaseUtils;
import pt.isec.pd.Shared.Hasher;

import java.util.List;

@Component
public class UserAuthenticationProvider implements AuthenticationProvider
{

    public UserAuthenticationProvider() {
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException
    {
        String username = authentication.getName();
        String password = Hasher.HashString(authentication.getCredentials().toString());

        boolean loginResult;

        try {
            loginResult = DatabaseUtils.login(username, password, Database.database.getConn());
        } catch (Exception e) {
            System.out.println(e.getMessage());
            loginResult = false;
        }

        if (loginResult) {
            return new UsernamePasswordAuthenticationToken(username, password, List.of());
        }
        System.out.println("nao logged in");
        return null;
    }


    @Override
    public boolean supports(Class<?> authentication)
    {
        return authentication.equals(UsernamePasswordAuthenticationToken.class);
    }
}
