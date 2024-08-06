package dino.junction.common.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;

import java.util.List;

/**
 * @param errors Errors가 없다면 응답이 내려가지 않게 처리
 */
@Builder
@Schema(description = "공통 응답")
public record CommonResponse<T>(
        @Schema(description = "status code")  int status,
        @Schema(description = "응답 코드")  String code,
        @Schema(description = "응답 메시지")  String message,
        @Schema(description = "응답 데이터")  T data,
        @JsonInclude(JsonInclude.Include.NON_EMPTY) List<ValidationError> errors) {
    public static CommonResponse<Object> CommonResponseSuccess(Object data) {
        return CommonResponse.builder().status(200).code("SUCCESS").message("OK").data(data).build();
    }

    public static CommonResponse<Object> CommonResponseSuccessWithMessage(Object data) {
        return CommonResponse.builder().status(200).code("SUCCESS").message("OK").data(data).build();
    }

    public static CommonResponse<Object> CommonResponseSuccessMessage() {
        return CommonResponse.builder().status(200).code("SUCCESS").message("OK").data("SUCCESS").build();
    }

//    public static CustomException CommonResponseUnauthorized(String message) {
//        return CustomException.
//        return CommonResponse.builder().status(401).code("Unauthorized").message(message).build();
//    }

    public static CommonResponse<Object> CommonResponseBadRequest(String message) {
        return CommonResponse.builder().status(401).code("Bad request").message(message).build();
    }

    public static ResponseEntity<Object> ResponseEntitySuccess(Object data) {
        return ResponseEntity.status(200).body(CommonResponseSuccess(data));
    }

    public static ResponseEntity<Object> ResponseEntitySuccessMessage() {
        return ResponseEntity.status(200).body(CommonResponseSuccess("SUCCESS"));
    }

    public static ResponseEntity<Object> ResponseEntityBadRequest(String message) {
        return ResponseEntity.status(400).body(CommonResponseBadRequest(message));
    }

//    public static ResponseEntity<Object> ResponseEntityUnauthorized(String message) {
//        return ResponseEntity.status(401).body(CommonResponseUnauthorized(message));
//    }
    @Getter
    @Builder
    @RequiredArgsConstructor
    public static class ValidationError {
        // @Valid 로 에러가 들어왔을 때, 어느 필드에서 에러가 발생했는 지에 대한 응답 처리
        private final String field;
        private final String message;

        public static ValidationError of(final FieldError fieldError) {
            return ValidationError.builder()
                    .field(fieldError.getField())
                    .message(fieldError.getDefaultMessage())
                    .build();
        }
    }
}
