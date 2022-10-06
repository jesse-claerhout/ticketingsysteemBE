package be.optis.opticketapi.dtos;

import be.optis.opticketapi.models.ticket.TicketPriority;
import be.optis.opticketapi.models.ticket.TicketState;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class TicketDTO {

    private int ticketId;

    private String title;

    private String state;

    private TicketPriority priority;

    private String ticketLocationBuildingAddress;

    private String thumbnailURL;

    private Boolean created;

    private Boolean follows;

    private Boolean appointed;
}


