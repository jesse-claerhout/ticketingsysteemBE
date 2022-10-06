package be.optis.opticketapi.models.ticket.history;

import be.optis.opticketapi.models.Account;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class HandymanCommentEntry extends HistoryEntry {

    private Account handyman;
}
