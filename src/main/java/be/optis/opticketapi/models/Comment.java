package be.optis.opticketapi.models;

import be.optis.opticketapi.models.ticket.Ticket;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int commentId;

    @Builder.Default
    private LocalDateTime date = LocalDateTime.now();

    private String content;

    @ManyToOne
    @JoinColumn(name = "ticket_ticket_id")
    private Ticket ticket;
    ;

    @ManyToOne
    @JoinColumn(name = "account_account_id")
    private Account account;
}
