package be.optis.opticketapi.models.ticket;

import be.optis.opticketapi.models.Account;
import be.optis.opticketapi.models.Comment;
import be.optis.opticketapi.models.Image;
import be.optis.opticketapi.models.ticket.history.HistoryEntry;
import be.optis.opticketapi.models.ticket.location.TicketLocation;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Ticket {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int ticketId;

    private String title;

    @Builder.Default
    private LocalDateTime date = LocalDateTime.now();

    private LocalDate deliveryDate;

    @Column(length = 4000)
    private String description;

    private TicketState state;

    private TicketPriority priority;

    @OneToOne(cascade = CascadeType.ALL)
    private TicketLocation location;

    @ManyToOne
    private Account creator;

    @ManyToMany
    @Builder.Default
    private List<Account> followers = new ArrayList<>();

    private Boolean appointed;

    private Boolean visibleToAll;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "ticket")
    @Builder.Default
    private List<Comment> comments = new ArrayList<>();

    @OneToMany(cascade = CascadeType.ALL)
    @Builder.Default
    private List<HistoryEntry> history = new ArrayList<>();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Ticket ticket)) return false;
        return Objects.equals(getTitle(), ticket.getTitle()) && Objects.equals(getDate(), ticket.getDate()) && Objects.equals(getDeliveryDate(), ticket.getDeliveryDate()) && Objects.equals(getDescription(), ticket.getDescription()) && getState() == ticket.getState() && getPriority() == ticket.getPriority() && Objects.equals(getLocation(), ticket.getLocation()) && Objects.equals(getCreator(), ticket.getCreator()) && Objects.equals(getFollowers(), ticket.getFollowers()) && Objects.equals(getVisibleToAll(), ticket.getVisibleToAll());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

}
