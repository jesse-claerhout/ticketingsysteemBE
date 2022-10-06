package be.optis.opticketapi.security;

import be.optis.opticketapi.models.Account;
import be.optis.opticketapi.models.Role;
import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Date;

@Component
public class JWTUtil {

    private final OpticketAccountDetailsService accountDetailsService;

    @Value("${jwt_secret}")
    private String secret;

    @Value("${tokenDurationS}")
    private Long tokenDurationS;

    @Autowired
    public JWTUtil(OpticketAccountDetailsService accountDetailsService) {
        this.accountDetailsService = accountDetailsService;
    }

    public String generateToken(Account account) throws IllegalArgumentException, JWTCreationException {
        var role = accountDetailsService.loadUserByUsername(account.getEmail()).getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .findFirst() // there is only ever one, hence first
                .orElse(null);
        assert role != null; // this should never happen unless we change OpticketAccountDetailsService

        return JWT.create()
                .withSubject(account.getEmail())
                .withIssuedAt(new Date())
                .withIssuer("Opticket")
                .withExpiresAt(Timestamp.valueOf(LocalDateTime.now().plusSeconds(tokenDurationS)))
                .withClaim("name", account.getFirstName() + " " + account.getLastName())
                .withClaim("handyman", role.equals(Role.HANDYMAN.getSpringSecurityRole()))
                .sign(Algorithm.HMAC256(secret));
    }

    public String validateTokenAndRetrieveSubject(String token) throws JWTVerificationException {
        JWTVerifier verifier = JWT.require(Algorithm.HMAC256(secret))
                .withIssuer("Opticket")
                .build();
        DecodedJWT jwt = verifier.verify(token);
        return jwt.getSubject();
    }
}