package be.optis.opticketapi.models.ticket.history;

import be.optis.opticketapi.models.ticket.TicketState;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class StateChangeEntry extends HistoryEntry {

    private TicketState newState;
}
