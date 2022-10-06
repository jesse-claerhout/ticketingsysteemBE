package be.optis.opticketapi.controllers;

import be.optis.opticketapi.dtos.*;
import be.optis.opticketapi.models.ticket.Ticket;
import be.optis.opticketapi.models.ticket.TicketPriority;
import be.optis.opticketapi.models.ticket.TicketState;
import be.optis.opticketapi.services.TicketService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.Arrays;
import java.util.List;

@Tag(name = "Ticket Controller")
@RestController
@RequestMapping("/api/tickets")
@AllArgsConstructor
public class TicketController {

    private final TicketService ticketService;

    @GetMapping("/my-tickets")
    @Operation(summary = "Gets tickets created or followed by the user.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "returns a list of TicketDTO objects.",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = TicketDTO.class))}),
            @ApiResponse(responseCode = "400", description = "Failed to get my-tickets request.", content = @Content),
            @ApiResponse(responseCode = "401", description = "Not authorized to use get request.", content = @Content)
    })
    public List<TicketDTO> getMyTickets(@RequestParam(defaultValue = "0") Integer pageNo,
                                        @RequestParam(defaultValue = "") List<String> states,
                                        @RequestParam(defaultValue = "") List<String> buildings,
                                        @RequestParam(defaultValue = "ticketId,asc") String sort,
                                        @RequestParam(defaultValue = "") String search) {
        return ticketService.getMyTickets(pageNo, states, buildings, sort, search);
    }

    @GetMapping()
    @Operation(summary = "Gets every public ticket from every user with given page number.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Returns a list of TicketDTO objects.",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = TicketDTO.class))}),
            @ApiResponse(responseCode = "400", description = "Failed to get tickets request.", content = @Content),
            @ApiResponse(responseCode = "401", description = "Not authorized to use get request.", content = @Content)
    })
    public List<TicketDTO> getAllTickets(@RequestParam(defaultValue = "0") Integer pageNo,
                                         @RequestParam(defaultValue = "") List<String> states,
                                         @RequestParam(defaultValue = "") List<String> buildings,
                                         @RequestParam(defaultValue = "ticketId,asc") String sort,
                                         @RequestParam(defaultValue = "") String search) {
        return ticketService.getAllTickets(pageNo, states, buildings, sort, search);
    }

    @GetMapping("/priorities")
    @Operation(summary = "Gets the available priorities to show on a ticket.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Returns a list of the available priorities.",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = TicketPriority.class))}),
            @ApiResponse(responseCode = "400", description = "Failed to get priorities request.", content = @Content),
            @ApiResponse(responseCode = "401", description = "Not authorized to use get request.", content = @Content)
    })
    public List<TicketPriority> getPriorities() {
        return Arrays.stream(TicketPriority.values()).toList();
    }

    @GetMapping("/states")
    @Operation(summary = "Gets the available states a ticket can have.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Returns a list of the available ticket states.",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = TicketPriority.class))}),
            @ApiResponse(responseCode = "400", description = "Failed to get states request.", content = @Content),
            @ApiResponse(responseCode = "401", description = "Not authorized to use get request.", content = @Content)
    })
    public List<String> getStates() {
        return Arrays.stream(TicketState.values()).map(TicketState::getReadable).toList();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Creates a ticket with given title, description, date, priority, location and if it's public or not;")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Created a ticket and returned the ticketId",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = Ticket.class))}),
            @ApiResponse(responseCode = "400", description = "Failed to post ticket request.", content = @Content),
            @ApiResponse(responseCode = "401", description = "Not authorized to use post request.", content = @Content)
    })
    public int createTicket(@Valid @RequestBody CreateTicketDTO body) {
        return ticketService.createTicket(body);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Returns the information of a ticket with given id.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Returned the information of a ticket with the given Id.",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = TicketDetailDTO.class))}),
            @ApiResponse(responseCode = "400", description = "Failed to get ticketdetail, given Id does not exist.", content = @Content),
            @ApiResponse(responseCode = "401", description = "Not authorized to use get request.", content = @Content)
    })
    public TicketDetailDTO getTicketDetail(@PathVariable int id) {
        return ticketService.getTicketDetail(id);
    }

    @PostMapping("/{id}/follow")
    @Operation(summary = "Adds a public ticket to the users followed tickets.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ticket with given id is added to the users followed tickets",
                    content = @Content),
            @ApiResponse(responseCode = "400", description = "Failed to post request, given ticket Id does not exist.", content = @Content),
            @ApiResponse(responseCode = "401", description = "Not authorized to use post request.", content = @Content)
    })
    public void followTicket(@PathVariable int id) {
        ticketService.followTicket(id);
    }

    @PostMapping("/{id}/unfollow")
    @Operation(summary = "Removes a ticket from the users followed tickets.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ticket with given id is removed from the users followed tickets",
                    content = @Content),
            @ApiResponse(responseCode = "400", description = "Failed to post request, given ticket Id does not exist.", content = @Content),
            @ApiResponse(responseCode = "401", description = "Not authorized to use post request.", content = @Content)
    })
    public void unfollowTicket(@PathVariable int id) {
        ticketService.unfollowTicket(id);
    }

    @PutMapping("/{id}/edit-handyman")
    @Operation(summary = "Update the ticket with the given id.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ticket with given id has been edited.", content = @Content),
            @ApiResponse(responseCode = "400", description = "Request failed, invalid body.", content = @Content),
            @ApiResponse(responseCode = "401", description = "Not authorized to use put request.", content = @Content),
            @ApiResponse(responseCode = "404", description = "Ticket with given id was not found", content = @Content)
    })
    public void editTicket(@PathVariable int id, @Valid @RequestBody EditTicketDTO body) {
        ticketService.editTicketHandyman(id, body);
    }

    @PutMapping("/{id}/edit-user")
    @Operation(summary = "Update the ticket with the given id.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ticket with given id has been edited.", content = @Content),
            @ApiResponse(responseCode = "400", description = "Request failed, invalid body.", content = @Content),
            @ApiResponse(responseCode = "401", description = "Not authorized to use put request.", content = @Content),
            @ApiResponse(responseCode = "404", description = "Ticket with given id was not found", content = @Content)
    })
    public void editTicket(@PathVariable int id, @Valid @RequestBody EditTicketUserDTO body) {
        ticketService.editTicketUser(id, body);
    }

    @GetMapping("/open-tickets")
    @Operation(summary = "Gets every open ticket from every user with the given page number.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Returns a list of TicketDTO objects that have the status OPEN.",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = TicketDTO.class))}),
            @ApiResponse(responseCode = "400", description = "Failed to get publicTickets request.", content = @Content),
            @ApiResponse(responseCode = "401", description = "Not authorized to use get request.", content = @Content)
    })
    public List<TicketDTO> getOpenTickets(@RequestParam(defaultValue = "0") Integer pageNo,
                                          @RequestParam(defaultValue = "") List<String> buildings,
                                          @RequestParam(defaultValue = "priority,asc") String sort,
                                          @RequestParam(defaultValue = "") String search) {
        return ticketService.getOpenTickets(pageNo, buildings, sort, search);
    }

    @PostMapping("/{id}/subscribe")
    @Operation(summary = "Handyman gets subscribed to the open ticket with the given id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "State of the ticket has changed from OPEN to APPOINTED_TO_HANDYMAN",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = TicketDTO.class))}),
            @ApiResponse(responseCode = "400", description = "Failed to request.", content = @Content),
            @ApiResponse(responseCode = "401", description = "Not authorized to use get request.", content = @Content),
            @ApiResponse(responseCode = "404", description = "Ticket with given id was not found", content = @Content)
    })
    public void handymanSubscribe(@PathVariable int id) {
        ticketService.handymanSubscribe(id);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Ticket with the given id gets deleted")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ticket with the given Id is deleted",
                    content = @Content),
            @ApiResponse(responseCode = "400", description = "Failed to request.", content = @Content),
            @ApiResponse(responseCode = "401", description = "Not authorized to use get request.", content = @Content),
            @ApiResponse(responseCode = "404", description = "Ticket with given id was not found", content = @Content)
    })
    public void deleteTicket(@PathVariable int id) {
        ticketService.deleteTicket(id);
    }

    @GetMapping("/{id}/history")
    @Operation(summary = "Get the history of the given ticket")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Returns history of ticket with given id", content = @Content),
            @ApiResponse(responseCode = "401", description = "Not authorized to use get request.", content = @Content),
            @ApiResponse(responseCode = "404", description = "Ticket with given id was not found", content = @Content)
    })
    public List<HistoryEntryDTO> getHistory(@PathVariable int id) {
        return ticketService.getHistory(id);
    }
}