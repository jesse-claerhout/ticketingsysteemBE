package be.optis.opticketapi.services;

import be.optis.opticketapi.dtos.*;
import be.optis.opticketapi.models.Account;
import be.optis.opticketapi.models.Role;
import be.optis.opticketapi.models.notification.NewTicketNotification;
import be.optis.opticketapi.models.ticket.Ticket;
import be.optis.opticketapi.models.ticket.TicketState;
import be.optis.opticketapi.models.ticket.history.HandymanCommentEntry;
import be.optis.opticketapi.models.ticket.history.NewTicketEntry;
import be.optis.opticketapi.models.ticket.history.StateChangeEntry;
import be.optis.opticketapi.models.ticket.location.Building;
import be.optis.opticketapi.models.ticket.location.TicketLocation;
import be.optis.opticketapi.repositories.AccountRepository;
import be.optis.opticketapi.repositories.TicketRepository;
import be.optis.opticketapi.security.AuthUtil;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.IntStream;
import java.util.stream.Stream;

@Service
@AllArgsConstructor
public class TicketService {

    private final TicketRepository ticketRepository;
    private final AccountRepository accountRepository;
    private final ModelMapper modelMapper;
    private final AuthUtil authUtil;
    private final NotificationService notificationService;
    private final MailService mailService;

    private final int PAGE_SIZE = 48;

    public List<TicketDTO> getMyTickets(int pageNo, List<String> states, List<String> buildings, String sort, String search) {
        var account = authUtil.getLoggedInAccount();

        var _sort = sort.split(",");
        var pageable = PageRequest.of(pageNo, PAGE_SIZE, Sort.by(new Order(getSortDirection(_sort[1]), _sort[0])));

        var allTickets = ticketRepository
                .findAllByStateIsInAndLocationBuildingIsInAndTitleContainingIgnoreCase(stringsToStates(states), stringsToBuildings(buildings), search, pageable)
                .getContent()
                .stream().toList();

        var tickets = Stream.concat(
                        account.getCreatedTickets().stream(),
                        account.getFollowedTickets().stream())
                .distinct()
                .toList();

        if (account.getRole() == Role.HANDYMAN) {
            tickets = Stream.of(
                            account.getCreatedTickets().stream(),
                            account.getSubscribedTickets().stream())
                    .flatMap(t -> t)
                    .distinct()
                    .toList();
        }

        List<Ticket> myTickets = tickets;

        return allTickets.stream()
                .filter(myTickets::contains)
                .map(t -> mapTicketDTO(t, account))
                .toList();
    }

    public List<TicketDTO> getAllTickets(int pageNo, List<String> states, List<String> buildings, String sort, String search) {
        var account = authUtil.getLoggedInAccount();

        var _sort = sort.split(",");
        var pageable = PageRequest.of(pageNo, PAGE_SIZE, Sort.by(new Order(getSortDirection(_sort[1]), _sort[0])));

        return ticketRepository
                .findAllByVisibleToAllAndStateIsInAndLocationBuildingIsInAndTitleContainingIgnoreCase(true, stringsToStates(states), stringsToBuildings(buildings), search, pageable)
                .getContent()
                .stream()
                .map(t -> mapTicketDTO(t, account))
                .toList();
    }

    private Sort.Direction getSortDirection(String direction) {
        if (Objects.equals(direction, "desc")) {
            return Sort.Direction.DESC;
        } else {
            return Sort.Direction.ASC;
        }
    }

    @Transactional
    public int createTicket(CreateTicketDTO dto) {
        var account = authUtil.getLoggedInAccount();

        var location = new TicketLocation(Building.fromFullAddress(dto.getLocation()), dto.getSpecificLocation());
        var ticket = Ticket.builder()
                .title(dto.getTitle())
                .description(dto.getDescription())
                .state(TicketState.OPEN)
                .priority(dto.getPriority())
                .location(location)
                .creator(account)
                .visibleToAll(dto.getVisibleToAll()).build();

        ticket.getHistory().add(new NewTicketEntry());

        var savedTicket = ticketRepository.saveAndFlush(ticket);
        account.getCreatedTickets().add(ticket);
        accountRepository.save(account);

        // Notification / Email
        var handymanAccounts = accountRepository.findByRole(Role.HANDYMAN);
        handymanAccounts.forEach((a) -> a.getInbox().add(new NewTicketNotification(savedTicket)));
        mailService.sendNewTicketEmail(savedTicket, handymanAccounts);
        accountRepository.saveAll(handymanAccounts);

        return savedTicket.getTicketId();
    }

    public TicketDetailDTO getTicketDetail(int ticketId) {
        var account = authUtil.getLoggedInAccount();

        var ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new IllegalArgumentException("Ticket bestaat niet"));

        if (!ticket.getVisibleToAll() && !ticket.getCreator().equals(account) && account.getRole() != Role.HANDYMAN)
            throw new AccessDeniedException("Geen toegang");

        var ticketDetailDTO = modelMapper.map(ticket, TicketDetailDTO.class);
        ticketDetailDTO.setCreated(checkIfCreator(account, ticket));
        ticketDetailDTO.setFollows(checkIfFollows(account, ticket));
        ticketDetailDTO.setState(ticket.getState().getReadable());
        return ticketDetailDTO;
    }

    @Transactional
    public void followTicket(int ticketId) {
        var account = authUtil.getLoggedInAccount();

        var ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new IllegalArgumentException("Ticket bestaat niet"));

        account.getFollowedTickets().add(ticket);
        ticket.getFollowers().add(account);

        ticketRepository.save(ticket);
        accountRepository.save(account);
    }

    @Transactional
    public void unfollowTicket(int ticketId) {
        var account = authUtil.getLoggedInAccount();

        var ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new IllegalArgumentException("Ticket bestaat niet"));

        account.getFollowedTickets().remove(ticket);
        ticket.getFollowers().remove(account);

        ticketRepository.save(ticket);
        accountRepository.save(account);
    }

    @Transactional
    public void editTicketHandyman(int id, EditTicketDTO dto) {
        var ticket = ticketRepository
                .findById(id)
                .orElseThrow(() -> new NoSuchElementException("no ticket found with this id"));

        if (dto.getDeliveryDate() != null && dto.getDeliveryDate().isBefore(LocalDate.now()))
            throw new IllegalArgumentException("delivery date cannot be in te past");

        var oldState = ticket.getState();

        ticket.setTitle(dto.getTitle());
        ticket.setDescription(dto.getDescription());
        ticket.setPriority(dto.getPriority());
        ticket.setLocation(new TicketLocation(Building.fromFullAddress(dto.getLocation()), dto.getSpecificLocation()));
        ticket.setVisibleToAll(dto.getVisibleToAll());
        ticket.setState(TicketState.fromReadable(dto.getState()));
        ticket.setDeliveryDate(dto.getDeliveryDate());

        if (oldState != ticket.getState()) {
            // Notification/Mailing
            // State might change at a later point but notif should stay the same which is why we add state seperately
            notificationService.sendStateChangeNotification(ticket, ticket.getState());
            mailService.sendStateChangeEmail(ticket, oldState);

            // History
            ticket.getHistory().add(new StateChangeEntry(ticket.getState()));
        }

        ticketRepository.save(ticket);
    }

    @Transactional
    public void editTicketUser(int id, EditTicketUserDTO dto) {
        var ticket = ticketRepository.findById(id).orElseThrow(NoSuchElementException::new);

        ticket.setTitle(dto.getTitle());
        ticket.setDescription(dto.getDescription());
        ticket.setPriority(dto.getPriority());
        ticket.setLocation(new TicketLocation(Building.fromFullAddress(dto.getLocation()), dto.getSpecificLocation()));
        ticket.setVisibleToAll(dto.getVisibleToAll());

        ticketRepository.save(ticket);
    }

    public List<TicketDTO> getOpenTickets(int pageNo, List<String> buildings, String sort, String search) {
        var account = authUtil.getLoggedInAccount();

        if (account.getRole() == Role.USER) {
            throw new AccessDeniedException("Only a handyman can make this request");
        }

        var _sort = sort.split(",");
        var pageable = PageRequest.of(pageNo, PAGE_SIZE, Sort.by(getSortDirection(_sort[1]), _sort[0]));

        return ticketRepository.findAllByStateAndLocationBuildingIsInAndTitleContainingIgnoreCase(TicketState.OPEN, stringsToBuildings(buildings), search, pageable).getContent()
                .stream()
                .map(t -> mapTicketDTO(t, account))
                .toList();
    }

    @Transactional
    public void handymanSubscribe(int ticketId) {
        var account = authUtil.getLoggedInAccount();
        var ticket = ticketRepository.findById(ticketId).orElseThrow(NoSuchElementException::new);

        var oldState = ticket.getState();

        if (account.getRole() == Role.USER) {
            throw new AccessDeniedException("Only a handyman can make this request");
        }

        if (oldState != TicketState.OPEN) {
            throw new IllegalArgumentException("To subscribe a ticket must be of state OPEN");
        }

        ticket.setState(TicketState.APPOINTED_TO_HANDYMAN);
        account.getSubscribedTickets().add(ticket);
        ticket.setAppointed(true);

        notificationService.sendStateChangeNotification(ticket, ticket.getState());
        mailService.sendStateChangeEmail(ticket, oldState);

        accountRepository.save(account);
        ticketRepository.saveAndFlush(ticket);

        ticket.getHistory().add(new StateChangeEntry(ticket.getState()));
    }

    private Boolean checkIfCreator(Account account, Ticket ticket) {
        return account.getCreatedTickets().contains(ticket);
    }

    private Boolean checkIfFollows(Account account, Ticket ticket) {
        return account.getFollowedTickets().contains(ticket);
    }

    private TicketDTO mapTicketDTO(Ticket ticket, Account account) {
        var ticketDTO = modelMapper.map(ticket, TicketDTO.class);
        ticketDTO.setThumbnailURL(String.format("https://picsum.photos/id/%d/400/300", ticket.getTicketId() + 10));
        ticketDTO.setCreated(checkIfCreator(account, ticket));
        ticketDTO.setFollows(checkIfFollows(account, ticket));
        ticketDTO.setState(ticket.getState().getReadable());

        return ticketDTO;
    }

    private List<TicketState> stringsToStates(List<String> strings) {
        var states = strings.stream().map(TicketState::fromReadable).toList();
        states = new ArrayList<>(states);

        if (states.isEmpty()) states = new ArrayList<>(Arrays.asList(TicketState.values()));

        states.remove(TicketState.DELETED);

        return states;
    }

    private List<Building> stringsToBuildings(List<String> strings) {
        var buildings = strings.stream().map(Building::fromFullAddress).toList();
        if (buildings.isEmpty()) buildings = Arrays.asList(Building.values());
        return buildings;
    }

    @Transactional
    public void deleteTicket(int id) {
        var account = authUtil.getLoggedInAccount();
        var ticket = ticketRepository.findById(id).orElseThrow(NoSuchElementException::new);

        account.getFollowedTickets().remove(ticket);
        account.getCreatedTickets().remove(ticket);
        account.getSubscribedTickets().remove(ticket);

        ticketRepository.deleteById(id);
    }

    public List<HistoryEntryDTO> getHistory(int id) {
        var ticket = ticketRepository.findById(id).orElseThrow(NoSuchElementException::new);
        var history = ticket.getHistory().stream()
                .map(h -> {
                    if (h instanceof NewTicketEntry)
                        return new HistoryEntryDTO(HistoryEntryDTO.HistoryEntryType.CREATED, h.getDateTime(), null, null);
                    else if (h instanceof HandymanCommentEntry)
                        return new HistoryEntryDTO(HistoryEntryDTO.HistoryEntryType.HANDYMAN_COMMENT, h.getDateTime(), null, null);
                    else if (h instanceof StateChangeEntry)
                        return new HistoryEntryDTO(HistoryEntryDTO.HistoryEntryType.STATE_CHANGE, h.getDateTime(), null, ((StateChangeEntry) h).getNewState().getReadable());
                    return null;
                })
                .toList();

        return IntStream.rangeClosed(0, history.size() - 1).mapToObj(i -> history.get(history.size() - 1 - i)).toList();
    }
}
