package be.optis.opticketapi.models;

import be.optis.opticketapi.models.notification.Notification;
import be.optis.opticketapi.models.ticket.Ticket;
import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int accountId;

    private String firstName;

    private String lastName;

    @Column(unique = true)
    private String email;

    private String password; // Encoded

    @OneToMany
    @Builder.Default
    private List<Ticket> createdTickets = new ArrayList<>();

    @ManyToMany
    @Builder.Default
    private List<Ticket> followedTickets = new ArrayList<>();

    @Builder.Default
    private Role role = Role.USER;

    @OneToMany
    @Builder.Default
    private List<Ticket> subscribedTickets = new ArrayList<>();

    @OneToMany(cascade = CascadeType.ALL)
    @Builder.Default
    private List<Notification> inbox = new ArrayList<>();

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "account")
    @Builder.Default
    private List<Comment> comments = new ArrayList<>();
}
