package be.optis.opticketapi.services;

import be.optis.opticketapi.dtos.RegisterCredentials;
import be.optis.opticketapi.models.Account;
import be.optis.opticketapi.repositories.AccountRepository;
import be.optis.opticketapi.repositories.TicketRepository;
import be.optis.opticketapi.security.AuthUtil;
import be.optis.opticketapi.security.JWTUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AccountServiceTest {

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private TicketRepository ticketRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JWTUtil jwtUtil;

    @Mock
    private RefreshTokenService refreshTokenService;

    @Mock
    private ModelMapper modelMapper;

    @Mock
    private AuthUtil authUtil;

    private AccountService subject;

    @BeforeEach
    void setUp() {
        subject = new AccountService(accountRepository, ticketRepository, passwordEncoder, authenticationManager, jwtUtil, refreshTokenService, modelMapper, authUtil);
    }

    @Test
    void createNewAccount_CreatesAccount() {
        var credentials = new RegisterCredentials(
                "johnsm@cronos.be",
                "John",
                "Smith",
                "Helloworld1",
                "Helloworld1"
        );
        when(passwordEncoder.encode(credentials.getPassword())).thenReturn("Encoded password");

        subject.createNewAccount(credentials);

        verify(accountRepository).saveAndFlush(ArgumentCaptor.forClass(Account.class).capture());
    }

}