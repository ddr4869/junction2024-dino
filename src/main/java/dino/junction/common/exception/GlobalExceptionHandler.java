package dino.junction.common.exception;

import com.google.api.client.http.HttpStatusCodes;
import dino.junction.common.logger.aop.LogTrace;
import dino.junction.common.model.CustomException;
import dino.junction.common.model.ErrorResponse;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@RestControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler{

    public final LogTrace logTrace;


    @ExceptionHandler(CustomException.class)
    public ResponseEntity<ErrorResponse> handleCunstomException(final CustomException e) {
        e.printStackTrace();
        return ErrorResponse.from(e);
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {

        List<String> errorMessages = ex.getBindingResult().getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .toList();
        String combinedErrorMessage = String.join(", ", errorMessages);

        CustomException customException = new CustomException(HttpStatusCodes.STATUS_CODE_BAD_REQUEST, combinedErrorMessage, "Validation filter error");
        return ResponseEntity.status(customException.getStatus()).body(new ErrorResponse(customException.getStatus(), customException.getMessage(), customException.getTraceId()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneralException(final Exception e) {
        return ResponseEntity.status(HttpStatusCodes.STATUS_CODE_SERVER_ERROR).body(new ErrorResponse(HttpStatusCodes.STATUS_CODE_SERVER_ERROR, e.getMessage(), logTrace.getTraceId()));
    }
}
