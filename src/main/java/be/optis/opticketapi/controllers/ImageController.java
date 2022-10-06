package be.optis.opticketapi.controllers;

import be.optis.opticketapi.dtos.CreateTicketDTO;
import be.optis.opticketapi.dtos.ImageDTO;
import be.optis.opticketapi.dtos.TicketDTO;
import be.optis.opticketapi.models.Image;
import be.optis.opticketapi.services.AwsS3Service;
import be.optis.opticketapi.services.ImageService;
import com.amazonaws.HttpMethod;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Tag(name = "Image Controller")
@RestController
@RequestMapping("/api/images")
@AllArgsConstructor
public class ImageController {

    private final ImageService imageService;

    @PostMapping()
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Uploads the fileNames of images to the database with their corresponding ticketId")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Succesfully uploaded image(s)",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ImageDTO.class))}),
            @ApiResponse(responseCode = "400", description = "Something went wrong posting the images to the database", content = @Content),
            @ApiResponse(responseCode = "401", description = "Not authorized to use post request.", content = @Content)
    })
    public void postImages(@Valid @RequestBody ImageDTO imageDTO) {
        imageService.createImages(imageDTO);
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "gets the URLs of images from the database with given ticketId")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Succesfully retrieved image URLs",
                    content = @Content),
            @ApiResponse(responseCode = "400", description = "Something went wrong getting the image URLs from the database", content = @Content),
            @ApiResponse(responseCode = "401", description = "Not authorized to use get request.", content = @Content)
    })
    public List<String> getImageURLs(@PathVariable int id) {
        return imageService.getImageURLsByTicketId(id);
    }
}
