package be.optis.opticketapi.services;

import be.optis.opticketapi.dtos.ImageDTO;
import be.optis.opticketapi.models.Image;
import be.optis.opticketapi.models.ticket.Ticket;
import be.optis.opticketapi.models.ticket.TicketPriority;
import be.optis.opticketapi.models.ticket.TicketState;
import be.optis.opticketapi.models.ticket.location.Building;
import be.optis.opticketapi.models.ticket.location.TicketLocation;
import be.optis.opticketapi.repositories.ImageRepository;
import be.optis.opticketapi.repositories.TicketRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ImageServiceTest {

    private ImageService imageService;
    private ImageDTO imageDTO;
    private List<Image> images;
    private List<String> imageURLs;

    @Mock
    private ImageRepository imageRepository;

    @Mock
    private TicketRepository ticketRepository;

    @BeforeEach
    void setUp() {
        imageService = new ImageService(imageRepository, ticketRepository);

        Ticket ticket1 = Ticket.builder()
                .ticketId(1)
                .title("Test Ticket 1")
                .description("Test Ticket description")
                .priority(TicketPriority.P5)
                .state(TicketState.OPEN)
                .location(new TicketLocation(Building.GASTON_GEENSLAAN_11B4, "Keuken"))
                .visibleToAll(true)
                .build();

        imageDTO = new ImageDTO();
        imageDTO.setTicketId(1);
        String[] fileNames = {"image01"};
        imageDTO.setFileNames(fileNames);

        Image image = Image.builder()
                .imageId(1)
                .fileName("image01")
                .imageURL("image01URL")
                .ticket(ticket1)
                .build();

        images = List.of(image);
        imageURLs = List.of("image01URL");
    }

    @Test
    void getImages_getImageURLsByTicketId() {
        when(imageRepository.findAll()).thenReturn(images);
        assertEquals(imageURLs, imageService.getImageURLsByTicketId(imageDTO.getTicketId()));
    }
}