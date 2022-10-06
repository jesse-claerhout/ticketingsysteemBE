package be.optis.opticketapi.payload.request;

import lombok.*;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class TokenRefreshRequest {

    @NotBlank
    private String refreshToken;

}
