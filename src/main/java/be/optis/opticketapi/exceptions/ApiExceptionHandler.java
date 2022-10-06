package be.optis.opticketapi.exceptions;

import com.amazonaws.SdkClientException;
import org.modelmapper.spi.ErrorMessage;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.AuthorizationServiceException;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.server.ResponseStatusException;

import javax.naming.AuthenticationException;
import java.util.Date;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@RestControllerAdvice
public class ApiExceptionHandler {

    private ResponseEntity<Object> buildResponseEntity(ApiError apiError) {
        return new ResponseEntity<>(apiError, apiError.getStatus());
    }


    @ExceptionHandler
    public ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException exception) {
        var errorMessages = exception.getAllErrors().stream()
                .map(ObjectError::getDefaultMessage)
                .collect(Collectors.toList());
        return new ResponseEntity<>(errorMessages, null, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    public ResponseEntity<Object> handleDataIntegrityViolation(DataIntegrityViolationException exception) {
        if (exception.getMostSpecificCause().getMessage().contains("Key (email)"))
            return new ResponseEntity<>(new EmailError("Er bestaat al een account met dit emailadres"), null,
                    HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(null, null, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    public ResponseEntity<Object> handleAuthenticationException(AuthenticationException exception) {
        var apiError = new ApiError(HttpStatus.BAD_REQUEST);
        apiError.setMessage(exception.getMessage());
        return buildResponseEntity(apiError);
    }

    @ExceptionHandler
    public ResponseEntity<Object> handleWrongCredentials(ResponseStatusException exception) {
        var apiError = new ApiError(HttpStatus.BAD_REQUEST);
        apiError.setMessage("wrong credentials");
        return buildResponseEntity(apiError);
    }

    @ExceptionHandler
    public ResponseEntity<Object> handleIllegalArgumentException(IllegalArgumentException exception) {
        var apiError = new ApiError(HttpStatus.BAD_REQUEST);
        apiError.setMessage(exception.getMessage());
        return buildResponseEntity(apiError);
    }

    @ExceptionHandler
    public ResponseEntity<Object> handleAccessDenied(AccessDeniedException exception) {
        var apiError = new ApiError(HttpStatus.UNAUTHORIZED);
        apiError.setMessage(exception.getMessage());
        return buildResponseEntity(apiError);
    }

    @ExceptionHandler(value = TokenRefreshException.class)
    public ResponseEntity<Object> handleTokenRefreshException(TokenRefreshException ex, WebRequest request) {
        var apiError = new ApiError(HttpStatus.FORBIDDEN);
        apiError.setMessage(ex.getMessage());
        return buildResponseEntity(apiError);
    }

    @ExceptionHandler
    public ResponseEntity<Object> handleNoSuchElementException(NoSuchElementException exception) {
        var apiError = new ApiError(HttpStatus.NOT_FOUND);
        apiError.setMessage(exception.getMessage());
        return buildResponseEntity(apiError);
    }

    @ExceptionHandler
    public ResponseEntity<Object> handleSdkClientException(SdkClientException awsS3Exception) {
        var apiError = new ApiError(HttpStatus.BAD_REQUEST);
        apiError.setMessage(awsS3Exception.getMessage());
        return buildResponseEntity(apiError);
    }
}