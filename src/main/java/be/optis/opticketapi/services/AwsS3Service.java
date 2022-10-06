package be.optis.opticketapi.services;

import be.optis.opticketapi.models.Image;
import be.optis.opticketapi.repositories.ImageRepository;
import com.amazonaws.HttpMethod;
import com.amazonaws.SdkClientException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.S3Object;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.util.*;

@Service
@AllArgsConstructor
public class AwsS3Service {

    private final AmazonS3 amazonS3;

    private final String bucket = "opticket-ticketingsysteem-images";

    public void uploadFiles(List<MultipartFile> files) {
        for (MultipartFile file : files) {
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentLength(file.getSize());
            metadata.setContentType(file.getContentType());

            try {
                amazonS3.putObject(bucket, file.getOriginalFilename(), file.getInputStream(), metadata);
            } catch (IOException e) {
                throw new SdkClientException("An exception occured while uploading the file to AWS");
            }
        }
    }
}
