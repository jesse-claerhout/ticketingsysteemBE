package be.optis.opticketapi.controllers;

import be.optis.opticketapi.dtos.TicketDTO;
import be.optis.opticketapi.services.AwsS3Service;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Tag(name = "Aws S3 Controller")
@RestController
@RequestMapping("/api/aws-s3")
@AllArgsConstructor
public class AwsS3Controller {

    private final AwsS3Service awsS3Service;

    @PostMapping()
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Uploads multipart files (images) to S3")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Succesfully uploaded image(s)",
                    content = @Content),
            @ApiResponse(responseCode = "400", description = "Something went wrong uploading to the S3 bucket", content = @Content),
            @ApiResponse(responseCode = "401", description = "Not authorized to use get request.", content = @Content)
    })
    public void uploadFiles(@RequestParam("files") List<MultipartFile> files) {
        awsS3Service.uploadFiles(files);
    }
}
