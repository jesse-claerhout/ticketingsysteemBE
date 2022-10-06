package be.optis.opticketapi.seeding;

import be.optis.opticketapi.dtos.RegisterCredentials;
import be.optis.opticketapi.models.Role;
import be.optis.opticketapi.models.ticket.Ticket;
import be.optis.opticketapi.models.ticket.TicketPriority;
import be.optis.opticketapi.models.ticket.TicketState;
import be.optis.opticketapi.models.ticket.location.Building;
import be.optis.opticketapi.models.ticket.location.TicketLocation;
import be.optis.opticketapi.repositories.AccountRepository;
import be.optis.opticketapi.repositories.TicketRepository;
import be.optis.opticketapi.services.AccountService;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component
public class Seeder implements CommandLineRunner {

    @Value("${spring.profiles.active:}")
    private String activeProfile; // empty if default

    private final AccountService accountService;
    private final AccountRepository accountRepository;
    private final TicketRepository ticketRepository;

    public Seeder(AccountService accountService, AccountRepository accountRepository, TicketRepository ticketRepository) {
        this.accountService = accountService;
        this.accountRepository = accountRepository;
        this.ticketRepository = ticketRepository;
    }

    @Override
    public void run(String... args) {
        // Add handyman, taking into account if it exists already because this runs on production too
        if (accountRepository.findByEmail("handyman@cronos.be").isEmpty()) {
            var handyman = accountService.createNewAccount(new RegisterCredentials(
                    "handyman@cronos.be",
                    "Werkman",
                    "Benny",
                    "Helloworld1",
                    "Helloworld1"
            ));
            handyman.setRole(Role.HANDYMAN);
            accountRepository.save(handyman);
        }

        // Skip rest if production
        if (activeProfile.equals("prod")) return;

        // ACCOUNTS
        var liam = accountService.createNewAccount(new RegisterCredentials(
                "spitali@cronos.be",
                "Liam",
                "Spitaels",
                "Helloworld1",
                "Helloworld1"
        ));

        var jesse = accountService.createNewAccount(new RegisterCredentials(
                "claerje@cronos.be",
                "Jesse",
                "Claerhout",
                "Helloworld1",
                "Helloworld1"
        ));
        var john = accountService.createNewAccount(new RegisterCredentials(
                "smithjo@cronos.be",
                "John",
                "Smith",
                "Helloworld1",
                "Helloworld1"
        ));
        var jane = accountService.createNewAccount(new RegisterCredentials(
                "smithja@cronos.be",
                "Jane",
                "Smith",
                "Helloworld1",
                "Helloworld1"
        ));
        jane.setRole(Role.HANDYMAN);

        // LOCATIONS
        var location1 = new TicketLocation(Building.BELLEVUE_5, "Keuken");
        var location2 = new TicketLocation(Building.VELDKANT_4, "Ingang");
        var location3 = new TicketLocation(Building.VELDKANT_4, "Ingang");
        var location4 = new TicketLocation(Building.VELDKANT_4, "Ingang");
        var location5 = new TicketLocation(Building.GASTON_GEENSLAAN_11B4, "Inkom");

        // TICKETS
        var ticket1 = Ticket.builder()
                .title("Koffiemachine kapot")
                .description("Geen koffie :(")
                .state(TicketState.BUSY)
                .priority(TicketPriority.P3)
                .location(location1)
                .creator(liam)
                .visibleToAll(true)
                .build();
        liam.getCreatedTickets().add(ticket1);

        var ticket2 = Ticket.builder()
                .title("Vaatwasser kapot")
                .description("Geen proper gerief :(")
                .state(TicketState.WAITING_FOR_MATERIALS)
                .priority(TicketPriority.P1)
                .location(location2)
                .creator(jesse)
                .visibleToAll(true)
                .build();
        ticket2.getFollowers().add(liam);
        liam.getFollowedTickets().add(ticket2);
        jesse.getCreatedTickets().add(ticket2);

        var ticket3 = Ticket.builder()
                .title("Deur kapot")
                .description("Kan niet binnen :(")
                .state(TicketState.DELETED)
                .priority(TicketPriority.P2)
                .location(location3)
                .creator(john)
                .visibleToAll(true)
                .build();
        ticket3.getFollowers().add(liam);
        john.getCreatedTickets().add(ticket3);
        liam.getFollowedTickets().add(ticket3);

        var ticket4 = Ticket.builder()
                .title("React frontend kapot")
                .description("Niks werkt :(")
                .state(TicketState.OPEN)
                .priority(TicketPriority.P2)
                .location(location4)
                .creator(jane)
                .visibleToAll(true)
                .build();
        jane.getCreatedTickets().add(ticket4);

        var ticket5 = Ticket.builder()
                .title("Deurbel werkt niet meer")
                .description("als je op de bel duwt, dan werkt hij niet. je moet kloppen want de bel doet het niet.")
                .state(TicketState.WAITING_FOR_MATERIALS)
                .priority(TicketPriority.P2)
                .location(location5)
                .creator(jesse)
                .visibleToAll(true)
                .build();
        jesse.getCreatedTickets().add(ticket5);

        if (Arrays.asList(args).contains("--allthetickets")) {
            for (int i = 0; i < 500; i++) {
                var location = new TicketLocation(Building.VELDKANT_33B, "Eerste verdiep");
                var ticket = Ticket.builder()
                        .title("Extra ticket " + i)
                        .description("Wow!")
                        .state(TicketState.OPEN)
                        .priority(TicketPriority.P3)
                        .location(location)
                        .creator(jane)
                        .visibleToAll(true)
                        .build();
                jane.getCreatedTickets().add(ticket);
                ticketRepository.save(ticket);
            }
        }

        ticketRepository.save(ticket1);
        ticketRepository.save(ticket2);
        ticketRepository.save(ticket3);
        ticketRepository.save(ticket4);
        ticketRepository.save(ticket5);

        accountRepository.save(liam);
        accountRepository.save(jesse);
        accountRepository.save(john);
        accountRepository.save(jane);

        LoggerFactory.getLogger(Seeder.class).info("Seeded database");
    }
}
