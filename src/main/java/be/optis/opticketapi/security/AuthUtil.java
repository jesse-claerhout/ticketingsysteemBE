package be.optis.opticketapi.security;

import be.optis.opticketapi.models.Account;
import be.optis.opticketapi.repositories.AccountRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class AuthUtil {

    private AccountRepository accountRepository;

    public Account getLoggedInAccount() {
        var email = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return accountRepository.findByEmail(email).orElse(null);
    }
}
