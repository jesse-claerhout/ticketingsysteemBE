package be.optis.opticketapi.controllers;

import be.optis.opticketapi.dtos.RegisterCredentials;
import be.optis.opticketapi.models.Account;
import be.optis.opticketapi.models.RefreshToken;
import be.optis.opticketapi.security.JWTUtil;
import be.optis.opticketapi.security.OpticketAccountDetailsService;
import be.optis.opticketapi.services.AccountService;
import be.optis.opticketapi.services.RefreshTokenService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@WebMvcTest(AuthController.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AccountService accountService;

    @MockBean
    private RefreshTokenService refreshTokenService;

    @MockBean
    private JWTUtil jwtUtil;

    @MockBean
    private AuthenticationManager authManager;

    @MockBean
    private OpticketAccountDetailsService accountDetailsService;

    private ObjectMapper objectMapper;

    @BeforeAll
    void setUp() {
        objectMapper = new ObjectMapper();
    }

    @Test
    void register_ValidCredentials_StatusCreated() throws Exception {
        var credentials = new RegisterCredentials(
                "johnsmith@cronos.be",
                "John",
                "Smith",
                "Helloworld1",
                "Helloworld1"
        );

        var resultingAccount = Account.builder()
                .firstName(credentials.getFirstName())
                .lastName(credentials.getLastName())
                .email(credentials.getEmail())
                .password(credentials.getPassword())
                .build();

        var request = MockMvcRequestBuilders
                .post("/api/auth/register")
                .content(objectMapper.writeValueAsString(credentials))
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON);

        doReturn(resultingAccount).when(accountService).createNewAccount(any());
        doReturn(new RefreshToken()).when(refreshTokenService).createRefreshToken(credentials.getEmail());

        mockMvc.perform(request).andExpect(status().isCreated());
    }

    @Test
    void register_PasswordsDontMatch_StatusBadRequest() throws Exception {
        var credentials = new RegisterCredentials(
                "johnsmith@cronos.be",
                "John",
                "Smith",
                "Helloworld1",
                "Helloworld123"
        );

        var request = MockMvcRequestBuilders
                .post("/api/auth/register")
                .content(objectMapper.writeValueAsString(credentials))
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(request).andExpect(status().isBadRequest());
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "Hello1",       // lt 8 chars
            "helloworld1",  // no capital letter
            "HELLOWORLD1",  // no lowercase letter
            "Helloworld"    // no number
    })
    @NullSource
    void register_InvalidPassword_StatusBadRequest(String password) throws Exception {
        var credentials = new RegisterCredentials(
                "johnsmith@cronos.be",
                "John",
                "Smith",
                password,
                password
        );

        var request = MockMvcRequestBuilders
                .post("/api/auth/register")
                .content(objectMapper.writeValueAsString(credentials))
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(request).andExpect(status().isBadRequest());
    }

    @ParameterizedTest
    @ValueSource(strings = {"johnsmith", "johmsmith@gmail.com", "@cronos.be", ""})
    @NullSource
    void register_InvalidEmail_StatusBadRequest(String email) throws Exception {
        var credentials = new RegisterCredentials(
                email,
                "John",
                "Smith",
                "Helloworld1",
                "Helloworld1"
        );

        var request = MockMvcRequestBuilders
                .post("/api/auth/register")
                .content(objectMapper.writeValueAsString(credentials))
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(request).andExpect(status().isBadRequest());
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "  "})
    @NullSource
    void register_EmptyFirstName_StatusBadRequest(String firstName) throws Exception {
        var credentials = new RegisterCredentials(
                "johnsmith@cronos.be",
                firstName,
                "Smith",
                "Helloworld1",
                "Helloworld1"
        );

        var request = MockMvcRequestBuilders
                .post("/api/auth/register")
                .content(objectMapper.writeValueAsString(credentials))
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(request).andExpect(status().isBadRequest());
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "  "})
    @NullSource
    void register_EmptyLastName_StatusBadRequest(String lastName) throws Exception {
        var credentials = new RegisterCredentials(
                "johnsmith@cronos.be",
                "John",
                lastName,
                "Helloworld1",
                "Helloworld1"
        );

        var request = MockMvcRequestBuilders
                .post("/api/auth/register")
                .content(objectMapper.writeValueAsString(credentials))
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(request).andExpect(status().isBadRequest());
    }
}