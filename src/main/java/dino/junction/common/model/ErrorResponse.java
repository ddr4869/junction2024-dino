package dino.junction.common.model;

import com.google.api.gax.rpc.StatusCode;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.ResponseEntity;

import java.sql.Time;
import java.util.Date;
import java.util.Map;

@Getter
public class ErrorResponse {
    private final int status;
    private final String message;
    private final String traceId;
    private final Date timestamp;

    public ErrorResponse(int status, String message, String traceId) {
        this.status = status;
        this.message = message;
        this.traceId = traceId;
        this.timestamp = new Date();
    }

    public static ResponseEntity<ErrorResponse> from(final CustomException customException) {
        return ResponseEntity.status(customException.getStatus()).body(new ErrorResponse(customException.getStatus(), customException.getMessage(), customException.getTraceId()));
    }
}
