package be.optis.opticketapi.models.notification;

import be.optis.opticketapi.models.ticket.Ticket;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;

@Entity
@NoArgsConstructor
public class NewTicketNotification extends Notification {

    public NewTicketNotification(Ticket ticket) {
        super(ticket);
    }

    @Override
    public String getText() {
        return String.format("Nieuw ticket aangemaakt: \"%s\" (#%d)", ticket.getTitle(), ticket.getTicketId());
    }
}
