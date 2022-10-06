/**
 * This filter runs for each request. It checks if there is a Bearer token, verifies it and sets authentication data
 * from the account for that request in the authentication property of the SecurityContext.
 */

package be.optis.opticketapi.security;

import com.auth0.jwt.exceptions.JWTVerificationException;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
@AllArgsConstructor
public class JWTFilter extends OncePerRequestFilter {

    private final OpticketAccountDetailsService accountDetailsService;
    private final JWTUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String authHeader = request.getHeader("Authorization");

        // Check header
        if (authHeader == null || authHeader.isBlank() || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        // Check JWT
        String jwt = authHeader.substring(7);
        if (jwt.isBlank()) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid JWT Token in Bearer Header");
            filterChain.doFilter(request, response);
            return;
        }

        try {
            String email = jwtUtil.validateTokenAndRetrieveSubject(jwt);
            UserDetails userDetails = accountDetailsService.loadUserByUsername(email);
            UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(email,
                    userDetails.getPassword(), userDetails.getAuthorities());
            // Set user details in SecurityContext
            if (SecurityContextHolder.getContext().getAuthentication() == null)
                SecurityContextHolder.getContext().setAuthentication(authToken);

            filterChain.doFilter(request, response);
        } catch (JWTVerificationException exc) {
            filterChain.doFilter(request, response);
        }
    }
}