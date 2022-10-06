package be.optis.opticketapi.dtos;

import be.optis.opticketapi.models.ticket.TicketPriority;
import be.optis.opticketapi.models.ticket.TicketState;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
public class EditTicketDTO extends CreateTicketDTO {

    public EditTicketDTO(@NotBlank(message = "Titel mag niet leeg zijn") String title, @NotBlank(message = "Beschrijving mag niet leeg zijn") String description, @NotNull(message = "Prioriteit aangeven is verplicht") TicketPriority priority, @NotBlank(message = "Gebouw aangeven is verplicht") String location, @NotBlank(message = "Locatie specificeren is verplicht") String specificLocation, @NotNull(message = "Zichtbaarheid mag niet leeg zijn") Boolean visibleToAll, String state, LocalDate deliveryDate) {
        super(title, description, priority, location, specificLocation, visibleToAll);
        setDeliveryDate(deliveryDate);
        setState(state);
    }

    // Readable state from TicketState enum
    @NotNull(message = "State mag niet null zijn")
    private String state;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate deliveryDate;
}
