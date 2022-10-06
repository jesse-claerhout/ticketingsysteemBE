package be.optis.opticketapi.security;

import be.optis.opticketapi.repositories.AccountRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.Collections;

@Component
@AllArgsConstructor
public class OpticketAccountDetailsService implements UserDetailsService {

    private final AccountRepository accountRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        var account = accountRepository.findByEmail(email).orElseThrow(
                () -> new UsernameNotFoundException("Could not find account with email " + email)
        );

        return new User(email, account.getPassword(),
                Collections.singletonList(new SimpleGrantedAuthority(account.getRole().getSpringSecurityRole())));
    }
}
