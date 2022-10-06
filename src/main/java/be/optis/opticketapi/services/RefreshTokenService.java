package be.optis.opticketapi.services;

import be.optis.opticketapi.exceptions.TokenRefreshException;
import be.optis.opticketapi.models.RefreshToken;
import be.optis.opticketapi.payload.response.TokenRefreshResponse;
import be.optis.opticketapi.repositories.AccountRepository;
import be.optis.opticketapi.repositories.RefreshTokenRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
public class RefreshTokenService {

    @Value("${jwtRefreshExpirationMs}")
    private Long refreshTokenDurationMs;

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    @Autowired
    private AccountRepository accountRepository;

    public Optional<RefreshToken> findByToken(String token) {
        return refreshTokenRepository.findByToken(token);
    }

    public RefreshToken createRefreshToken(String email) {
        var refreshToken = new RefreshToken();
        refreshToken.setAccount(accountRepository.findByEmail(email).get());
        refreshToken.setExpiryDate(Instant.now().plusMillis(refreshTokenDurationMs));
        refreshToken.setToken(UUID.randomUUID().toString());
        refreshToken = refreshTokenRepository.save(refreshToken);
        return refreshToken;
    }

    @Transactional
    public RefreshToken checkIfTokenExists(String refreshToken) {
        var token = refreshTokenRepository.findByToken(refreshToken)
                .orElseThrow(() -> new TokenRefreshException(refreshToken, "Refresh token is not in database!"));

        if (token.getExpiryDate().compareTo(Instant.now()) < 0) {
            refreshTokenRepository.delete(token);
            throw new TokenRefreshException(token.getToken(), "Refresh token was expired. Please make a new signin request");
        }
        return token;
    }
}
