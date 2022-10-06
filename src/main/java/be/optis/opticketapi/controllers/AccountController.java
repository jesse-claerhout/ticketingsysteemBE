package be.optis.opticketapi.controllers;

import be.optis.opticketapi.dtos.NotificationDTO;
import be.optis.opticketapi.services.AccountService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Account Controller")
@RestController
@RequestMapping("/api/account")
@AllArgsConstructor
public class AccountController {

    private AccountService accountService;

    @GetMapping("/inbox")
    @Operation(summary = "Gets the inbox of the logged in user.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Returns list of notifications in user's inbox. All given notifications will have 'seen' as true next time this request is done.")
    })
    public List<NotificationDTO> getInbox() {
        return accountService.getInbox();
    }

    @GetMapping("/inbox/unseen-count")
    @Operation(summary = "Gets the amount of unseen notifications in the user's inbox.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Returns the amount of unseen notifications in the user's inbox.")
    })
    public int newNotifications() {
        return accountService.unseenNotificationsCount();
    }

    @DeleteMapping("/inbox/{id}")
    @Operation(summary = "Deletes the notification with the given id.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Deleted notification.")
    })
    public void deleteNotification(@PathVariable Integer id) {
        accountService.deleteNotification(id);
    }

    @DeleteMapping("/inbox")
    @Operation(summary = "Deletes every notification in the user's inbox.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Deleted notifications in user's inbox.")
    })
    public void clearInbox() {
        accountService.clearInbox();
    }
}
