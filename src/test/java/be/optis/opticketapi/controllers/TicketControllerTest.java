package be.optis.opticketapi.controllers;

import be.optis.opticketapi.repositories.TicketRepository;
import be.optis.opticketapi.security.JWTUtil;
import be.optis.opticketapi.security.OpticketAccountDetailsService;
import be.optis.opticketapi.services.TicketService;
import be.optis.opticketapi.utils.RequestCreator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.Collections;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
@WebMvcTest(TicketController.class)
class TicketControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TicketService ticketService;

    @MockBean
    private OpticketAccountDetailsService accountDetailsService;

    @MockBean
    private JWTUtil jwtUtil;

    @Test
    void getMyTickets_NotLoggedIn_StatusUnauthorized() throws Exception {
        var request = MockMvcRequestBuilders
                .get("/api/tickets/my-tickets")
                .accept(MediaType.APPLICATION_JSON);

        mockMvc.perform(request).andExpect(status().isUnauthorized());
    }

    @Test
    void getMyTickets_LoggedIn_StatusOk() throws Exception {
        var request = RequestCreator.createGetRequest(jwtUtil, "/api/tickets/my-tickets", "ROLE_USER");

        doReturn(new User("johnsmith@cronos.be", "Helloworld1", Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"))))
                .when(accountDetailsService).loadUserByUsername(any());

        mockMvc.perform(request).andExpect(status().isOk());
    }

    @Test
    void getPublicTickets() throws Exception {
        var request = RequestCreator.createGetRequest(jwtUtil, "/api/tickets/open-tickets", "ROLE_HANDYMAN");

        doReturn(new User("johnsmith@cronos.be", "Helloworld1", Collections.singletonList(new SimpleGrantedAuthority("ROLE_HANDYMAN"))))
                .when(accountDetailsService).loadUserByUsername(any());

        mockMvc.perform(request).andExpect(status().isOk());
    }

    @Test
    void handymanSubscribe() throws Exception {
        var request = RequestCreator.createGetRequest(jwtUtil, "/api/tickets/open-tickets", "ROLE_HANDYMAN");

        doReturn(new User("johnsmith@cronos.be", "Helloworld1", Collections.singletonList(new SimpleGrantedAuthority("ROLE_HANDYMAN"))))
                .when(accountDetailsService).loadUserByUsername(any());

        mockMvc.perform(request).andExpect(status().isOk());
    }
}