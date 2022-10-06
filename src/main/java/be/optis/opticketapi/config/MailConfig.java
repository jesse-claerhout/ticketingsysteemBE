package be.optis.opticketapi.config;

import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailServiceAsync;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailServiceAsyncClientBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MailConfig {

    @Value("${aws_access_id}")
    private String accessKeyId;

    @Value("${aws_access_secret}")
    private String accessKeySecret;

    @Value("${aws_region}")
    private String region;

    @Bean
    public AmazonSimpleEmailServiceAsync asyncSESClient() {
        return AmazonSimpleEmailServiceAsyncClientBuilder
                .standard()
                .withRegion(region)
                .withCredentials(getAwsCredentialProvider())
                .build();
    }

    private AWSCredentialsProvider getAwsCredentialProvider() {
        var awsCredentials = new BasicAWSCredentials(accessKeyId, accessKeySecret);
        return new AWSStaticCredentialsProvider(awsCredentials);
    }
}