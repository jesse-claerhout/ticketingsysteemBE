package be.optis.opticketapi.controllers;

import be.optis.opticketapi.dtos.CommentDTO;
import be.optis.opticketapi.dtos.CreateCommentDTO;
import be.optis.opticketapi.models.Comment;
import be.optis.opticketapi.services.CommentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@Tag(name = "Comment Controller")
@RestController
@RequestMapping("/api/comments")
@AllArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @PostMapping("/{id}")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Creates a comment with content, ticket and account attached;")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Created a comment and returned the commentId",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = Comment.class))}),
            @ApiResponse(responseCode = "400", description = "Failed to post comment.", content = @Content),
            @ApiResponse(responseCode = "401", description = "Not authorized to use comment request.", content = @Content),
            @ApiResponse(responseCode = "404", description = "Ticket with given id not found.", content = @Content)
    })
    public void postComment(@PathVariable int id, @Valid @RequestBody CreateCommentDTO createCommentDTO) {
        commentService.postComment(id, createCommentDTO);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Gets every comment from the specified ticket")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Returns a list of commentDTO objects.",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = CommentDTO.class))}),
            @ApiResponse(responseCode = "400", description = "Failed to get comment request.", content = @Content),
            @ApiResponse(responseCode = "401", description = "Not authorized to use get request.", content = @Content),
            @ApiResponse(responseCode = "404", description = "No comments found for the specified ticket", content = @Content)
    })
    public List<CommentDTO> getCommentsByTicketId(@PathVariable int id) {
        return commentService.getAllCommentsByTicketId(id);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Deletes a comment with given id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "deleted comment with given id.",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = CommentDTO.class))}),
            @ApiResponse(responseCode = "400", description = "Failed to delete comment request.", content = @Content),
            @ApiResponse(responseCode = "401", description = "Not authorized to delete request.", content = @Content),
            @ApiResponse(responseCode = "404", description = "No comments found for the specified ticket", content = @Content)
    })
    public void deleteComment(@PathVariable int id) {
        commentService.deleteComment(id);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Edits the content of a comment with given id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "succesfully edited the comment with given id.",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = CommentDTO.class))}),
            @ApiResponse(responseCode = "400", description = "Failed to edit comment.", content = @Content),
            @ApiResponse(responseCode = "401", description = "Not authorized to edit comment request.", content = @Content),
            @ApiResponse(responseCode = "404", description = "No comments found for the specified ticket", content = @Content)
    })
    public void editComment(@PathVariable int id, @Valid @RequestBody CreateCommentDTO createCommentDTO) {
        commentService.editComment(id, createCommentDTO);
    }
}
