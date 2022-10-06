package be.optis.opticketapi.services;

import be.optis.opticketapi.models.notification.StateChangeNotification;
import be.optis.opticketapi.models.ticket.Ticket;
import be.optis.opticketapi.models.ticket.TicketState;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class NotificationService {

    public void sendStateChangeNotification(Ticket ticket, TicketState state) {
        var notification = new StateChangeNotification(ticket, state);
        ticket.getCreator().getInbox().add(notification);
        ticket.getFollowers().forEach(f -> f.getInbox().add(notification));
    }
}
