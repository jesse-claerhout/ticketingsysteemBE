package be.optis.opticketapi.payload.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TokenRefreshResponse {
    private String jwt_token;
    private String refreshToken;
    private String tokenType = "Bearer";

    public TokenRefreshResponse(String jwt_token, String refreshToken) {
        this.jwt_token = jwt_token;
        this.refreshToken = refreshToken;
    }
}
