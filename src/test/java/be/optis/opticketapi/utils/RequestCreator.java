package be.optis.opticketapi.utils;

import be.optis.opticketapi.security.JWTUtil;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.Collection;
import java.util.List;

import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;

public class RequestCreator {

    public static RequestBuilder createGetRequest(JWTUtil jwtUtil, String requestPath, String forRole) {
        return createRequest(jwtUtil, requestPath, forRole, HttpMethod.GET, "");
    }


    public static RequestBuilder createPostRequest(JWTUtil jwtUtil, String requestPath, String forRole, String optionalBody) {
        return createRequest(jwtUtil, requestPath, forRole, HttpMethod.POST, optionalBody);
    }


    private static RequestBuilder createRequest(JWTUtil jwtUtil, String requestPath, String forRole, HttpMethod method, String optionalBody) {
        String token = "fakeToken";
        Collection<? extends GrantedAuthority> roles = forRole.isEmpty() ? List.of() : List.of(new SimpleGrantedAuthority(forRole));

        RequestBuilder request = null;

        switch (method) {
            case GET:
                request = MockMvcRequestBuilders
                        .get(requestPath)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + token);
                break;
            case POST:
                request = MockMvcRequestBuilders
                        .post(requestPath)
                        .content(optionalBody)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + token);
                break;
        }

        setSecurity(jwtUtil, roles, token);

        return request;
    }

    private static void setSecurity(JWTUtil jwtUtil, Collection<? extends GrantedAuthority> roles, String token) {
        // Mock the Security

    }

}
