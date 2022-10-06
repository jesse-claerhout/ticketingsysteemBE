package be.optis.opticketapi.repositories;

import be.optis.opticketapi.models.ticket.location.TicketLocation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TicketLocationRepository extends JpaRepository<TicketLocation, Integer> {
}
