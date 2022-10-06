package be.optis.opticketapi.models;

import be.optis.opticketapi.models.ticket.Ticket;
import lombok.*;

import javax.persistence.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Image {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int imageId;

    private String fileName;

    private String imageURL;

    @ManyToOne
    private Ticket ticket;
}
