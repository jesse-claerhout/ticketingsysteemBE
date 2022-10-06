package be.optis.opticketapi.services;

import be.optis.opticketapi.dtos.LoginCredentials;
import be.optis.opticketapi.dtos.NotificationDTO;
import be.optis.opticketapi.dtos.RegisterCredentials;
import be.optis.opticketapi.models.Account;
import be.optis.opticketapi.models.notification.Notification;
import be.optis.opticketapi.payload.request.TokenRefreshRequest;
import be.optis.opticketapi.payload.response.JwtResponse;
import be.optis.opticketapi.payload.response.TokenRefreshResponse;
import be.optis.opticketapi.repositories.AccountRepository;
import be.optis.opticketapi.repositories.TicketRepository;
import be.optis.opticketapi.security.AuthUtil;
import be.optis.opticketapi.security.JWTUtil;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Comparator;
import java.util.List;
import java.util.NoSuchElementException;

@Service
@AllArgsConstructor
public class AccountService {

    private final AccountRepository accountRepository;
    private final TicketRepository ticketRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authManager;
    private final JWTUtil jwtUtil;
    private final RefreshTokenService refreshTokenService;
    private final ModelMapper modelMapper;
    private final AuthUtil authUtil;

    public Account createNewAccount(RegisterCredentials credentials) {
        var encodedPass = passwordEncoder.encode(credentials.getPassword());

        var account = Account.builder()
                .firstName(credentials.getFirstName())
                .lastName(credentials.getLastName())
                .email(credentials.getEmail())
                .password(encodedPass)
                .build();

        return accountRepository.saveAndFlush(account);
    }

    private void authenticateLoginCredentials(LoginCredentials credentials) {
        try {
            var authInputToken = new UsernamePasswordAuthenticationToken(credentials.getEmail(), credentials.getPassword());
            authManager.authenticate(authInputToken);
        } catch (AuthenticationException exception) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
    }

    private Account findAccountByEmail(String email) {
        return accountRepository.findByEmail(email).orElseThrow(() -> new IllegalArgumentException("Email niet in database"));
    }

    public JwtResponse createJWTResponse(Object credentials) {
        Account account = null;

        if (credentials instanceof RegisterCredentials) {
            account = createNewAccount(((RegisterCredentials) credentials));
        }
        if (credentials instanceof LoginCredentials) {
            authenticateLoginCredentials((LoginCredentials) credentials);
            account = findAccountByEmail(((LoginCredentials) credentials).getEmail());
        }

        var token = jwtUtil.generateToken(account);
        var refreshToken = refreshTokenService.createRefreshToken(account.getEmail());

        return new JwtResponse(token, refreshToken.getToken());
    }

    public TokenRefreshResponse createTokenRefreshResponse(TokenRefreshRequest request) {
        var requestRefreshToken = request.getRefreshToken();
        var refreshToken = refreshTokenService.checkIfTokenExists(requestRefreshToken);
        var account = refreshToken.getAccount();
        var jwt_token = jwtUtil.generateToken(account);
        return new TokenRefreshResponse(jwt_token, refreshToken.getToken());
    }

    public List<NotificationDTO> getInbox() {
        var account = authUtil.getLoggedInAccount();
        var result = account.getInbox().stream()
                .map(this::mapNotification)
                .sorted(Comparator.comparing(NotificationDTO::getDateTime).reversed())
                .toList();

        account.getInbox().forEach(n -> {
            n.setSeen(true);
            ticketRepository.save(n.getTicket());
        });

        return result;
    }

    private NotificationDTO mapNotification(Notification notification) {
        var dto = modelMapper.map(notification, NotificationDTO.class);
        dto.setTicketId(notification.getTicket().getTicketId());
        return dto;
    }

    public int unseenNotificationsCount() {
        return authUtil.getLoggedInAccount().getInbox().stream().filter(n -> !n.isSeen()).toList().size();
    }

    public void deleteNotification(Integer id) {
        var account = authUtil.getLoggedInAccount();

        var notification = account.getInbox().stream()
                .filter(n -> n.getNotificationId() == id)
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("no notification found with this id"));

        account.getInbox().remove(notification);

        ticketRepository.save(notification.getTicket());
    }

    public void clearInbox() {
        var account = authUtil.getLoggedInAccount();

        var affectedTickets = account.getInbox().stream()
                .map(Notification::getTicket)
                .distinct()
                .toList();

        account.getInbox().clear();
        ticketRepository.saveAll(affectedTickets);
    }
}
