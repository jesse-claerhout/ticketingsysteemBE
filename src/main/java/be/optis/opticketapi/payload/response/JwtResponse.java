package be.optis.opticketapi.payload.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.autoconfigure.security.oauth2.resource.OAuth2ResourceServerProperties;

@AllArgsConstructor
@Getter
@Setter
public class JwtResponse {

    private String jwt_token;
    private String type = "Bearer";
    private String refreshToken;

    public JwtResponse(String jwt_token, String refreshToken){
        this.jwt_token = jwt_token;
        this.refreshToken = refreshToken;
    }

}