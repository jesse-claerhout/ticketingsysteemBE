package be.optis.opticketapi.controllers;

import antlr.Token;
import be.optis.opticketapi.dtos.LoginCredentials;
import be.optis.opticketapi.dtos.RegisterCredentials;
import be.optis.opticketapi.exceptions.TokenRefreshException;
import be.optis.opticketapi.models.Account;
import be.optis.opticketapi.models.RefreshToken;
import be.optis.opticketapi.payload.request.TokenRefreshRequest;
import be.optis.opticketapi.payload.response.JwtResponse;
import be.optis.opticketapi.payload.response.TokenRefreshResponse;
import be.optis.opticketapi.security.JWTUtil;
import be.optis.opticketapi.services.AccountService;
import be.optis.opticketapi.services.RefreshTokenService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.tags.Tags;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.util.Collections;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@AllArgsConstructor
@Tag(name = "Authorization Controller")
public class AuthController {

    private final AccountService accountService;

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Register with firstname, lastname, email, password and repeatPassword, returns a JWT Token, token type and refreshtoken.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Succesfully registered, JWT, token type and refreshtoken returned.",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = JwtResponse.class))}),
            @ApiResponse(responseCode = "400", description = "Email already exists in database.", content = @Content),
    })
    public JwtResponse registerHandler(@Valid @RequestBody RegisterCredentials body) {
        return accountService.createJWTResponse(body);
    }

    @PostMapping("/login")
    @Operation(summary = "Login with email and password, returns a JWT Token, token type and refreshtoken.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Succesfully logged in, JWT, token type and refreshtoken returned.",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = JwtResponse.class))}),
            @ApiResponse(responseCode = "400", description = "Wrong Credentials; email doesn't exist or password is incorrect.", content = @Content)
    })
    public JwtResponse loginHandler(@Valid @RequestBody LoginCredentials body) {
        return accountService.createJWTResponse(body);
    }

    @PostMapping("/refreshtoken")
    @Operation(summary = "Requests refreshtoken and returns a new JWT token.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "refreshtoken is valid, new JWT token returned.",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = TokenRefreshResponse.class))}),
            @ApiResponse(responseCode = "400", description = "refreshtoken is expired.",
                    content = @Content),
    })
    public TokenRefreshResponse refreshtoken(@Valid @RequestBody TokenRefreshRequest request) {
        return accountService.createTokenRefreshResponse(request);
    }
}
