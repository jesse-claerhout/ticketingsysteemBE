package be.optis.opticketapi.controllers;

import be.optis.opticketapi.dtos.TicketDetailDTO;
import be.optis.opticketapi.models.ticket.location.Building;
import be.optis.opticketapi.models.ticket.location.TicketLocation;
import be.optis.opticketapi.payload.response.JwtResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/api/locations")
@AllArgsConstructor
@Tag(name = "Location Controller")
public class LocationController {

    @GetMapping("/buildings")
    @Operation(summary = "Returns a list of buildings from the Cronos Group")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Returned a list of all Cronos buildings.",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = TicketLocation.class))}),
            @ApiResponse(responseCode = "400", description = "Failed to get buildings.", content = @Content),
            @ApiResponse(responseCode = "401", description = "Not authorized to use get request.", content = @Content)
    })
    public List<String> getBuildings() {
        return Arrays.stream(Building.values()).map(b -> b.getAddress()).toList();
    }
}
