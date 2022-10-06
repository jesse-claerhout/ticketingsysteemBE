package be.optis.opticketapi.models.ticket;

public enum TicketPriority {
    P1("P1"), P2("P2"), P3("P3"), P4("P4"), P5("P5");

    private final String stringRepresentation;

    TicketPriority(String stringRepresentation) {
        this.stringRepresentation = stringRepresentation;
    }

    public String asString() {
        return stringRepresentation;
    }
}
