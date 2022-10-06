package be.optis.opticketapi.dtos;

import be.optis.opticketapi.models.ticket.Ticket;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ImageDTO {

    private String[] fileNames;

    private int ticketId;
}
