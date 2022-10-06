package be.optis.opticketapi.repositories;

import be.optis.opticketapi.models.ticket.Ticket;
import be.optis.opticketapi.models.ticket.TicketState;
import be.optis.opticketapi.models.ticket.location.Building;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, Integer> {

    Page<Ticket> findAllByStateIsInAndLocationBuildingIsInAndTitleContainingIgnoreCase(List<TicketState> states,
                                                                                          List<Building> buildings,
                                                                                          String search,
                                                                                          Pageable pageable);

    Page<Ticket> findAllByVisibleToAllAndStateIsInAndLocationBuildingIsInAndTitleContainingIgnoreCase(boolean visibleToAll,
                                                                                                      List<TicketState> states,
                                                                                                      List<Building> buildings,
                                                                                                      String search,
                                                                                                      Pageable pageable);

    Page<Ticket> findAllByStateAndLocationBuildingIsInAndTitleContainingIgnoreCase(TicketState state,
                                                                                   List<Building> buildingList,
                                                                                   String search,
                                                                                   Pageable pageable);

}
