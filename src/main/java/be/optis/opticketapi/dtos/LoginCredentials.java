package be.optis.opticketapi.dtos;

import lombok.*;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class LoginCredentials {

    @NotBlank(message = "E-mailadres mag niet leeg zijn")
    private String email;

    @NotBlank(message = "Wachtwoord mag niet leeg zijn")
    private String password;
}
