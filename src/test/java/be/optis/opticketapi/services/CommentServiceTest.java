package be.optis.opticketapi.services;

import be.optis.opticketapi.dtos.CommentDTO;
import be.optis.opticketapi.dtos.CreateCommentDTO;
import be.optis.opticketapi.models.Account;
import be.optis.opticketapi.models.Comment;
import be.optis.opticketapi.models.Role;
import be.optis.opticketapi.models.ticket.Ticket;
import be.optis.opticketapi.models.ticket.TicketPriority;
import be.optis.opticketapi.models.ticket.TicketState;
import be.optis.opticketapi.models.ticket.location.Building;
import be.optis.opticketapi.models.ticket.location.TicketLocation;
import be.optis.opticketapi.repositories.CommentRepository;
import be.optis.opticketapi.repositories.TicketRepository;
import be.optis.opticketapi.security.AuthUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.security.access.AccessDeniedException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CommentServiceTest {

    private CommentService commentService;
    private Ticket ticket1;
    private Account account1;
    private Account account2;
    private Comment comment1;
    private Comment comment2;
    private CommentDTO commentDTO1;
    private CommentDTO commentDTO2;
    private CreateCommentDTO createCommentDTO1;
    private CreateCommentDTO createCommentDTO2;
    private CreateCommentDTO createCommentDTONoContent;
    private List<Comment> commentList = new ArrayList<>();
    private List<CommentDTO> commentDTOList = new ArrayList<>();

    @Mock
    private TicketRepository ticketRepository;

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private AuthUtil authUtil;

    @Mock
    private ModelMapper modelMapper;

    @BeforeEach
    void setUp() {
        commentService = new CommentService(ticketRepository, commentRepository, authUtil, modelMapper);

        ticket1 = Ticket.builder()
                .ticketId(1)
                .title("Test Ticket 1")
                .description("Test Ticket description")
                .priority(TicketPriority.P5)
                .state(TicketState.OPEN)
                .location(new TicketLocation(Building.GASTON_GEENSLAAN_11B4, "Keuken"))
                .visibleToAll(true)
                .build();

        account1 = Account.builder()
                .email("claerje@cronos.be")
                .firstName("jesse")
                .lastName("claerhout")
                .password("Helloworld1")
                .role(Role.USER)
                .build();

        account2 = Account.builder()
                .email("spitali@cronos.be")
                .firstName("liam")
                .lastName("spitaels")
                .password("Helloworld1")
                .role(Role.USER)
                .build();

        comment1 = Comment.builder()
                .commentId(1)
                .content("de beschrijving is incorrect, de knop is kapot")
                .ticket(ticket1)
                .account(account1)
                .build();

        commentDTO1 = CommentDTO.builder()
                .commentId(1)
                .content("de beschrijving is incorrect, de knop is kapot")
                .date(LocalDateTime.now())
                .ticketTicketId(1)
                .ticketTitle("test ticket 1")
                .accountFirstName("jesse")
                .accountLastName("claerhout")
                .created(true)
                .build();

        comment2 = Comment.builder()
                .commentId(2)
                .content("Er zit ook heel veel kalk in de watercontainer.")
                .ticket(ticket1)
                .account(account2)
                .build();

        commentDTO2 = CommentDTO.builder()
                .commentId(2)
                .content("de beschrijving is niet helemaal juist, de knop is kapot.")
                .date(LocalDateTime.now())
                .ticketTicketId(1)
                .ticketTitle("test ticket 1")
                .accountFirstName("liam")
                .accountLastName("spitaels")
                .created(false)
                .build();

        commentList.add(comment1);
        commentList.add(comment2);
        commentDTOList.add(commentDTO1);
        commentDTOList.add(commentDTO2);

        createCommentDTO1 = CreateCommentDTO.builder()
                .content("de beschrijving is incorrect, de knop is kapot")
                .build();

        createCommentDTO2 = CreateCommentDTO.builder()
                .content("de beschrijving is niet helemaal juist, de knop is kapot")
                .build();

        createCommentDTONoContent = CreateCommentDTO.builder()
                .content("")
                .build();
    }

    @Test
    void postComment_ticketHasStateClosed() {
        ticket1.setState(TicketState.CLOSED);
        when(ticketRepository.findById(1)).thenReturn(Optional.ofNullable(ticket1));
        assertThrows(IllegalArgumentException.class, () -> commentService.postComment(1, createCommentDTO1));

    }

    @Test
    void postComment_commentHasNoTicket() {
        assertThrows(NoSuchElementException.class, () -> commentService.postComment(2, createCommentDTO1));
    }

    @Test
    void postComment_commentHasNoContent() {
        when(ticketRepository.findById(1)).thenReturn(Optional.ofNullable(ticket1));
        when(authUtil.getLoggedInAccount()).thenReturn(account1);
        assertThrows(IllegalArgumentException.class, () -> commentService.postComment(1, createCommentDTONoContent));
    }

    @Test
    void postComment_commentSuccesfullyPosted() {
        when(ticketRepository.findById(ticket1.getTicketId())).thenReturn(Optional.ofNullable(ticket1));
        when(commentRepository.findById(1)).thenReturn(Optional.ofNullable(comment1));
        when(authUtil.getLoggedInAccount()).thenReturn(account1);
        commentService.postComment(ticket1.getTicketId(), createCommentDTO1);
        assertEquals(createCommentDTO1.getContent(), commentRepository.findById(1).get().getContent());
    }

    @Test
    void getComments_returnsListOfComments() {
        when(commentRepository.findAll()).thenReturn(commentList);
        when(modelMapper.map(comment1, CommentDTO.class)).thenReturn(commentDTO1);
        when(modelMapper.map(comment2, CommentDTO.class)).thenReturn(commentDTO2);
        assertEquals(commentDTOList, commentService.getAllCommentsByTicketId(ticket1.getTicketId()));
    }

    @Test
    void getComments_CurrentUserHasCreatedComment() {
        when(ticketRepository.findById(ticket1.getTicketId())).thenReturn(Optional.ofNullable(ticket1));
        when(authUtil.getLoggedInAccount()).thenReturn(account1);
        commentService.postComment(ticket1.getTicketId(), createCommentDTO1);
        when(commentRepository.findAll()).thenReturn(commentList);
        when(modelMapper.map(comment1, CommentDTO.class)).thenReturn(commentDTO1);
        when(modelMapper.map(comment2, CommentDTO.class)).thenReturn(commentDTO2);
        assertFalse(commentService.getAllCommentsByTicketId(ticket1.getTicketId()).get(1).isCreated());
    }

    @Test
    void deleteComment_ThrowsAccessDeniedExceptionWhenNotAuthorized() {
        when(commentRepository.findById(comment1.getCommentId())).thenReturn(Optional.ofNullable(comment1));
        when(authUtil.getLoggedInAccount()).thenReturn(account2);
        assertThrows(AccessDeniedException.class, () -> commentService.deleteComment(comment1.getCommentId()));
    }

    @Test
    void deleteComment_ThrowsNoSuchElementExceptionWhenCommentDoesntExist() {
        assertThrows(NoSuchElementException.class, () -> commentService.deleteComment(comment1.getCommentId()));
    }

    @Test
    void editComment_ThrowsNoSuchElementException() {
        assertThrows(NoSuchElementException.class, () -> commentService.editComment(comment1.getCommentId(), createCommentDTO2));
    }

    @Test
    void editComment_CommentHasNoContent() {
        assertThrows(IllegalArgumentException.class, () -> commentService.editComment(comment1.getCommentId(), createCommentDTONoContent));
    }

    @Test
    void editComment_CommentSuccesfullyEdited() {
        when(commentRepository.findById(comment1.getCommentId())).thenReturn(Optional.ofNullable(comment1));
        commentService.editComment(comment1.getCommentId(), createCommentDTO2);
        assertEquals(createCommentDTO2.getContent(), comment1.getContent());
    }
}
