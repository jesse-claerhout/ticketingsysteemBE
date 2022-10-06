package be.optis.opticketapi.dtos;

import be.optis.opticketapi.models.ticket.TicketPriority;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class TicketDetailDTO {

    private int ticketId;

    private String title;

    private String state;

    private TicketPriority priority;

    private String ticketLocationBuildingAddress;

    private String ticketLocationSpace;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    private LocalDateTime date;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate deliveryDate;

    private String description;

    private String media;

    private Boolean visibleToAll;

    private Boolean created;

    private Boolean follows;

    private Boolean appointed;
}
