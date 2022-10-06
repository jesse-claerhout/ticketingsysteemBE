package be.optis.opticketapi.dtos;

import be.optis.opticketapi.models.Role;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommentDTO {

    private int commentId;

    private String content;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    private LocalDateTime date;

    private int ticketTicketId;

    private String ticketTitle;

    private String accountFirstName;

    private String accountLastName;

    private Role accountRole;

    private boolean created;
}
