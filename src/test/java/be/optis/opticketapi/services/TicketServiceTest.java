package be.optis.opticketapi.services;

import be.optis.opticketapi.dtos.CreateTicketDTO;
import be.optis.opticketapi.dtos.EditTicketDTO;
import be.optis.opticketapi.dtos.TicketDTO;
import be.optis.opticketapi.models.Account;
import be.optis.opticketapi.models.Role;
import be.optis.opticketapi.models.ticket.Ticket;
import be.optis.opticketapi.models.ticket.TicketPriority;
import be.optis.opticketapi.models.ticket.TicketState;
import be.optis.opticketapi.models.ticket.history.NewTicketEntry;
import be.optis.opticketapi.models.ticket.history.StateChangeEntry;
import be.optis.opticketapi.models.ticket.location.Building;
import be.optis.opticketapi.models.ticket.location.TicketLocation;
import be.optis.opticketapi.repositories.AccountRepository;
import be.optis.opticketapi.repositories.TicketRepository;
import be.optis.opticketapi.security.AuthUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.security.access.AccessDeniedException;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TicketServiceTest {

    private TicketService service;
    private EditTicketDTO editDto;
    private CreateTicketDTO createDto;
    private Ticket ticket1;
    private Ticket ticket2;
    private TicketDTO ticketDTO;
    private List<Ticket> tickets;
    private Account handyman;
    private Account user;

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private TicketRepository ticketRepository;

    @Mock
    private ModelMapper modelMapper;

    @Mock
    private AuthUtil authUtil;

    @Mock
    private NotificationService notificationService;

    @Mock
    private MailService mailService;

    @BeforeEach
    void setUp() {
        service = new TicketService(ticketRepository, accountRepository, modelMapper, authUtil, notificationService, mailService);
        editDto = new EditTicketDTO(
                "Hello world!",
                "Description",
                TicketPriority.P3,
                Building.BELLEVUE_5.getAddress(),
                "Ingang",
                true,
                TicketState.BUSY.getReadable(),
                null
        );

        createDto = new CreateTicketDTO(
                "Hello world!",
                "Description",
                TicketPriority.P3,
                Building.BELLEVUE_5.getAddress(),
                "Ingang",
                true
        );

        ticket1 = Ticket.builder()
                .title("Test Ticket 1")
                .description("Test Ticket description")
                .priority(TicketPriority.P5)
                .state(TicketState.OPEN)
                .location(new TicketLocation(Building.GASTON_GEENSLAAN_11B4, "Keuken"))
                .visibleToAll(true)
                .build();

        ticket2 = Ticket.builder()
                .title("Test Ticket 2")
                .description("Test Ticket description")
                .priority(TicketPriority.P5)
                .state(TicketState.WAITING_FOR_VALIDATION)
                .location(new TicketLocation(Building.VELDKANT_33B, "Keuken"))
                .visibleToAll(true)
                .build();

        ticketDTO = new TicketDTO();
        ticketDTO.setState(TicketState.OPEN.getReadable());

        tickets = Arrays.asList(ticket1, ticket2);

        handyman = Account.builder()
                .email("claerje@cronos.be")
                .firstName("jesse")
                .lastName("claerhout")
                .password("Helloworld1")
                .role(Role.HANDYMAN)
                .build();

        user = Account.builder()
                .email("spitali@cronos.be")
                .firstName("liam")
                .lastName("spitaels")
                .password("Helloworld1")
                .role(Role.USER)
                .build();
    }

    @Test
    void editTicket() {
        when(ticketRepository.findById(1)).thenReturn(Optional.ofNullable(ticket1));
        service.editTicketHandyman(1, editDto);
        assertEquals(ticketRepository.findById(1).orElse(new Ticket()).getTitle(), editDto.getTitle());
    }

    @Test
    void editTicket_deliveryDateInPast() {
        when(ticketRepository.findById(1)).thenReturn(Optional.ofNullable(ticket1));
        editDto.setDeliveryDate(LocalDate.of(1900, 1, 1));
        assertThrows(IllegalArgumentException.class, () -> service.editTicketHandyman(1, editDto));
    }

    @Test
    void editTicket_ticketDoesNotExist() {
        assertThrows(NoSuchElementException.class, () -> service.editTicketHandyman(2, editDto));
    }

    @Test
    void editTicket_stateChange_addsNotification() {
        when(ticketRepository.findById(1)).thenReturn(Optional.ofNullable(ticket1));
        ticket1.getFollowers().add(user);
        service.editTicketHandyman(1, editDto);
        Mockito.verify(notificationService).sendStateChangeNotification(ticket1, ticket1.getState());
    }

    @Test
    void editTicket_stateChange_addsHistoryEntry() {
        when(ticketRepository.findById(1)).thenReturn(Optional.ofNullable(ticket1));
        service.editTicketHandyman(1, editDto);
        assertTrue(ticket1.getHistory().stream().anyMatch(h -> h instanceof StateChangeEntry));
    }

    @Test
    void editTicket_noStateChange_doesNotAddHistoryEntry() {
        when(ticketRepository.findById(1)).thenReturn(Optional.ofNullable(ticket1));
        editDto.setState(ticket1.getState().getReadable());
        service.editTicketHandyman(1, editDto);
        assertFalse(ticket1.getHistory().stream().anyMatch(h -> h instanceof StateChangeEntry));
    }

    @Test
    void openTickets_ticketHasStatusOpen() {
        Page<Ticket> pagedRepositoryResponse = new PageImpl<>(List.of(ticket1));
        when(ticketRepository.findAllByStateAndLocationBuildingIsInAndTitleContainingIgnoreCase(any(), any(), any(), any())).thenReturn(pagedRepositoryResponse);
        when(authUtil.getLoggedInAccount()).thenReturn(handyman);
        when(modelMapper.map(ticket1, TicketDTO.class)).thenReturn(ticketDTO);
        assertEquals(service.getOpenTickets(0, List.of(ticket1.getLocation().getBuilding().getAddress()), "priority,asc", "").get(0).getState(), TicketState.OPEN.getReadable());
    }

    @Test
    void openTickets_requestByUserThrowsAccessDeniedException() {
        handyman.setRole(Role.USER);
        when(authUtil.getLoggedInAccount()).thenReturn(handyman);
        assertThrows(AccessDeniedException.class, () -> service.getOpenTickets(0, List.of(Building.VELDKANT_33B.getAddress()), "priority,asc", ""));
    }

    @Test
    void handymanSubscribe_stateChangedToAppointedToHandyman() {
        when(ticketRepository.findById(1)).thenReturn(Optional.ofNullable(ticket1));
        when(authUtil.getLoggedInAccount()).thenReturn(handyman);
        service.handymanSubscribe(1);
        assertEquals(ticketRepository.findById(1).get().getState(), TicketState.APPOINTED_TO_HANDYMAN);
    }

    @Test
    void handymanSubscribe_initialStateOfTicketMustBeOpen() {
        when(ticketRepository.findById(1)).thenReturn(Optional.ofNullable(ticket1));
        when(authUtil.getLoggedInAccount()).thenReturn(handyman);
        ticket1.setState(TicketState.WAITING_FOR_MATERIALS);
        assertThrows(IllegalArgumentException.class, () -> service.handymanSubscribe(1));
    }

    @Test
    void handymanSubscribe_requestByUserThrowsAccessDeniedException() {
        when(ticketRepository.findById(1)).thenReturn(Optional.ofNullable(ticket1));
        handyman.setRole(Role.USER);
        when(authUtil.getLoggedInAccount()).thenReturn(handyman);
        assertThrows(AccessDeniedException.class, () -> service.handymanSubscribe(1));
    }

    @Test
    void handymanSubscribe_ticketDoesNotExist() {
        when(authUtil.getLoggedInAccount()).thenReturn(handyman);
        assertThrows(NoSuchElementException.class, () -> service.handymanSubscribe(2));
    }

    @Test
    void handymanSubscribe_ticketIsAddedToSubscribedTicketsList() {
        when(ticketRepository.findById(1)).thenReturn(Optional.ofNullable(ticket1));
        when(authUtil.getLoggedInAccount()).thenReturn(handyman);
        service.handymanSubscribe(1);
        assertTrue(handyman.getSubscribedTickets().contains(ticket1));
    }

    @Test
    void handymanSubscribe_ticketHasHandymanAfterSubscribe() {
        when(ticketRepository.findById(1)).thenReturn(Optional.ofNullable(ticket1));
        when(authUtil.getLoggedInAccount()).thenReturn(handyman);
        service.handymanSubscribe(1);
        assertEquals(ticket1.getAppointed(), true);
    }

    @Test
    void allTickets_filter() {
        Page<Ticket> pagedResponse = new PageImpl<>(List.of(ticket1));
        var ticketDTO = new TicketDTO();
        when(authUtil.getLoggedInAccount()).thenReturn(handyman);
        when(ticketRepository.findAllByVisibleToAllAndStateIsInAndLocationBuildingIsInAndTitleContainingIgnoreCase(anyBoolean(), any(), any(), any(), any())).thenReturn(pagedResponse);
        when(modelMapper.map(ticket1, TicketDTO.class)).thenReturn(ticketDTO);
        assertTrue(service.getAllTickets(0, List.of(ticket1.getState().getReadable()), List.of(), "ticketId,desc", "").contains(ticketDTO));
    }

    @Test
    void openTickets_filter() {
        Page<Ticket> pagedResponse = new PageImpl<>(List.of(ticket1));
        var ticketDTO = new TicketDTO();
        when(authUtil.getLoggedInAccount()).thenReturn(handyman);
        when(ticketRepository.findAllByStateAndLocationBuildingIsInAndTitleContainingIgnoreCase(any(), any(), any(), any())).thenReturn(pagedResponse);
        when(modelMapper.map(ticket1, TicketDTO.class)).thenReturn(ticketDTO);
        assertTrue(service.getOpenTickets(0, List.of(ticket1.getLocation().getBuilding().getAddress()), "ticketId,asc", "").contains(ticketDTO));
    }
}