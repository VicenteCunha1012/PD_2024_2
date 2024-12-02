package pt.isec.pd.Server.Springboot.Controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import pt.isec.pd.Server.Data.Database;
import pt.isec.pd.Server.Data.DatabaseUtils;
import pt.isec.pd.Server.Springboot.security.TokenService;
import pt.isec.pd.Shared.Entities.User;
import pt.isec.pd.Shared.Hasher;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final TokenService tokenService;

    public AuthController(TokenService tokenService)
    {
        this.tokenService = tokenService;
    }


    @PostMapping("/register")
    public ResponseEntity register(@RequestBody User user) {
        boolean result;
        try {
            result = DatabaseUtils.registar(
                    Hasher.HashUserPass(user),
                    Database.database.getConn()
            );
        } catch (Exception e) {
            result = false;
        }

        if(result) {
            return new ResponseEntity(HttpStatus.OK);
        }
        return new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);

    }


    @PostMapping("/login")
    public ResponseEntity login(Authentication authentication) {
        return new ResponseEntity(
                this.tokenService.generateToken(authentication),
                HttpStatus.OK
        );
    }
}
