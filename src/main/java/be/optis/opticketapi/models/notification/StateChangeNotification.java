package be.optis.opticketapi.models.notification;

import be.optis.opticketapi.models.ticket.Ticket;
import be.optis.opticketapi.models.ticket.TicketState;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;

@Entity
@NoArgsConstructor
public class StateChangeNotification extends Notification {

    private TicketState newState;

    public StateChangeNotification(Ticket ticket, TicketState newState) {
        super(ticket);
        this.newState = newState;
    }

    @Override
    public String getText() {
        return String.format("%s is veranderd van status naar \"%s\".", ticket.getTitle(), newState.getReadable());
    }
}
