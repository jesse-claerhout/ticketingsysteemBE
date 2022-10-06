package be.optis.opticketapi.models.notification;

import be.optis.opticketapi.models.ticket.Ticket;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@NoArgsConstructor
@Getter
@Setter
public abstract class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int notificationId;

    private LocalDateTime dateTime;

    @Setter
    private boolean seen;

    @ManyToOne
    protected Ticket ticket;

    public abstract String getText();

    public Notification(Ticket ticket) {
        this.ticket = ticket;
        this.dateTime = LocalDateTime.now();
    }
}
