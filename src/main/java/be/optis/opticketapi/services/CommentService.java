package be.optis.opticketapi.services;

import be.optis.opticketapi.dtos.CommentDTO;
import be.optis.opticketapi.dtos.CreateCommentDTO;
import be.optis.opticketapi.dtos.TicketDTO;
import be.optis.opticketapi.models.Account;
import be.optis.opticketapi.models.Comment;
import be.optis.opticketapi.models.ticket.Ticket;
import be.optis.opticketapi.models.ticket.TicketState;
import be.optis.opticketapi.repositories.CommentRepository;
import be.optis.opticketapi.repositories.TicketRepository;
import be.optis.opticketapi.security.AuthUtil;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.NoSuchElementException;

@Service
@AllArgsConstructor
public class CommentService {

    private final TicketRepository ticketRepository;
    private final CommentRepository commentRepository;
    private final AuthUtil authUtil;
    private final ModelMapper modelMapper;

    public void postComment(int ticketId, CreateCommentDTO dto) {
        var ticket = ticketRepository.findById(ticketId).orElseThrow(NoSuchElementException::new);
        var account = authUtil.getLoggedInAccount();

        if (ticket.getState() == TicketState.CLOSED) {
            throw new IllegalArgumentException("Er mag geen reactie geplaatst worden als het ticket de status closed heeft");
        }

        if (dto.getContent().isBlank()) {
            throw new IllegalArgumentException("Inhoud mag niet leeg zijn");
        }

        var comment = Comment.builder()
                .content(dto.getContent())
                .account(account)
                .ticket(ticket)
                .build();

        commentRepository.save(comment);
    }

    public List<CommentDTO> getAllCommentsByTicketId(int ticketId) {
        var comments = commentRepository.findAll().stream().filter(comment -> comment.getTicket().getTicketId() == ticketId).toList();
        if (comments.isEmpty()) {
            return new ArrayList<>();
        }
        return comments.stream().map(this::mapCommentDTO).sorted(Comparator.comparingInt(CommentDTO::getCommentId)).toList();
    }

    private CommentDTO mapCommentDTO(Comment comment) {
        var account = authUtil.getLoggedInAccount();

        var commentDTO = modelMapper.map(comment, CommentDTO.class);
        commentDTO.setCreated(comment.getAccount() == account);

        return commentDTO;
    }

    @Transactional
    public void deleteComment(int commentId) {
        var comment = commentRepository.findById(commentId).orElseThrow(NoSuchElementException::new);
        var account = authUtil.getLoggedInAccount();

        if (comment.getAccount() != account) {
            throw new AccessDeniedException("Comment behoort tot een andere gebruiker. Je mag deze niet verwijderen.");
        }

        commentRepository.deleteById(commentId);
    }

    public void editComment(int commentId, CreateCommentDTO createCommentDTO) {
        if (createCommentDTO.getContent().isBlank()) {
            throw new IllegalArgumentException("Inhoud mag niet leeg zijn");
        }
        var comment = commentRepository.findById(commentId).orElseThrow(NoSuchElementException::new);
        comment.setContent(createCommentDTO.getContent());
        commentRepository.save(comment);
    }
}
