package be.optis.opticketapi.dtos;

import be.optis.opticketapi.models.ticket.TicketPriority;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateTicketDTO {

    @NotBlank(message = "Titel mag niet leeg zijn")
    private String title;

    @NotBlank(message = "Beschrijving mag niet leeg zijn")
    private String description;

    @NotNull(message = "Prioriteit aangeven is verplicht")
    private TicketPriority priority;

    /**
     * Mapped to {@link be.optis.opticketapi.models.ticket.location.Building}
     */
    @NotBlank(message = "Gebouw aangeven is verplicht")
    private String location;

    /**
     * E.g. coffee machine, specific room, etc. which could not be easily enumerated
     */
    @NotBlank(message = "Locatie specificeren is verplicht")
    private String specificLocation;

    @NotNull(message = "Zichtbaarheid mag niet leeg zijn")
    private Boolean visibleToAll;
}
