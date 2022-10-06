package be.optis.opticketapi.config;

import be.optis.opticketapi.models.Role;
import be.optis.opticketapi.security.JWTFilter;
import be.optis.opticketapi.security.OpticketAccountDetailsService;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.http.HttpServletResponse;

@Configuration
@EnableWebSecurity
@AllArgsConstructor
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final JWTFilter jwtFilter;
    private final OpticketAccountDetailsService accountDetailsService;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable()
                .httpBasic().disable()
                .cors()
                .and()
                .authorizeHttpRequests()
                .mvcMatchers(HttpMethod.PUT, "/api/tickets/{id}/edit-handyman").hasRole("HANDYMAN")
                .mvcMatchers(HttpMethod.PUT, "/api/tickets/{id}/edit-user").hasRole("USER")
                .mvcMatchers("/api/auth/**").permitAll()
                .mvcMatchers("/api/tickets/**").authenticated()
                .mvcMatchers("/api/locations/**").authenticated()
                .mvcMatchers("/api/account/**").authenticated()
                .mvcMatchers("/api/comments/**").authenticated()
                .and()
                .userDetailsService(accountDetailsService)
                .exceptionHandling()
                .authenticationEntryPoint(
                        (request, response, authException) ->
                                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized")
                )
                .and()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS);

        http.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }
}