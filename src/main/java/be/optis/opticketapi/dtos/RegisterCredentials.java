package be.optis.opticketapi.dtos;

import be.optis.opticketapi.validators.PasswordsEqualConstraint;
import lombok.*;

import javax.validation.constraints.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@PasswordsEqualConstraint(message = "Wachtwoorden moeten hetzelfde zijn")
public class RegisterCredentials {

    @Email(message = "Moet een geldig e-mailadres zijn")
    @NotBlank
    @Pattern(regexp = ".*(@cronos.be)$", message = "Moet een Cronos e-mailadres zijn")
    private String email;

    @NotBlank(message = "Voornaam en achternaam mogen niet leeg zijn")
    private String firstName, lastName;

    @Size(min = 8)
    @NotNull
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)[a-zA-Z\\d\\w\\W]{8,}$",
            message = "Wachtwoord moet minimum een hoofdletter, kleine letter en nummer bevatten " +
                    "en moet minimum 8 karakters lang zijn")
    private String password;

    @NotNull
    private String repeatPassword;


}
