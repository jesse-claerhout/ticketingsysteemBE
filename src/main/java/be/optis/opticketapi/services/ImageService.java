package be.optis.opticketapi.services;

import be.optis.opticketapi.dtos.CreateTicketDTO;
import be.optis.opticketapi.dtos.ImageDTO;
import be.optis.opticketapi.models.Image;
import be.optis.opticketapi.models.ticket.Ticket;
import be.optis.opticketapi.repositories.ImageRepository;
import be.optis.opticketapi.repositories.TicketRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

@Service
@AllArgsConstructor
public class ImageService {

    private final ImageRepository imageRepository;
    private final TicketRepository ticketRepository;

    private final String bucketURL = "https://opticket-ticketingsysteem-images.s3.eu-west-1.amazonaws.com/";

    public void createImages(ImageDTO imageDTO) {

        var ticket = ticketRepository.findById(imageDTO.getTicketId()).orElseThrow(NoSuchElementException::new);
        List<Image> images = new ArrayList<>();

        for (var fileName : imageDTO.getFileNames()) {

            var image = Image.builder()
                    .fileName(fileName)
                    .imageURL(bucketURL + fileName)
                    .ticket(ticket).build();

            images.add(image);
        }

        if (!images.isEmpty())
            imageRepository.saveAll(images);
    }

    public List<String> getImageURLsByTicketId(int ticketId) {
        var images = imageRepository.findAll();

        return images.stream().filter(image -> image.getTicket().getTicketId() == ticketId)
                .map(Image::getImageURL).toList();
    }
}
