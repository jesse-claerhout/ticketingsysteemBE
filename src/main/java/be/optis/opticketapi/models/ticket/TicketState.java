package be.optis.opticketapi.models.ticket;

import lombok.Getter;

import java.util.Arrays;

@Getter
public enum TicketState {
    OPEN("Open"),
    APPOINTED_TO_HANDYMAN("Werkman aangesteld"),
    BUSY("Bezig"),
    WAITING_FOR_MATERIALS("Wachten op materiaal"),
    WAITING_FOR_VALIDATION("Wachten op goedkeuring"),
    CLOSED("Gesloten"),
    DELETED("Verwijderd");

    private final String readable;

    TicketState(String readable) {
        this.readable = readable;
    }

    public static TicketState fromReadable(String readable) {
        return Arrays.stream(values())
                .filter(s -> s.getReadable().equals(readable))
                .findFirst().orElseThrow(() -> new IllegalArgumentException("No state found for this argument"));
    }
}
