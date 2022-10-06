package be.optis.opticketapi;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.annotation.PostConstruct;
import java.util.TimeZone;

@SpringBootApplication
@OpenAPIDefinition(info = @Info(title = "Opticket API", version = "0.1", description = "Opticket Ticketingsysteem REST API"))
public class OpticketApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(OpticketApiApplication.class, args);
    }

    @PostConstruct
    public void init() {
        /*
         * We use LocalDateTime::now a lot in this application. In retrospect, we probably should've use Instant::now
         * instead.
         *
         * As a fix I am forcing the application to use UTC timezone so everything works the same as in the
         * prod environment. Formatting this date to the user's timezone is the responsibility of the frontend.
         */
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
    }
}
