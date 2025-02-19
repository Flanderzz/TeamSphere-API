package co.teamsphere.api.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ErrorResponse {
    private ErrorDetails error;

    @Getter
    @Setter
    @AllArgsConstructor
    public static class ErrorDetails {
        private int status;
        private String message;
        private String details;
        private String endpoint;
        private String method;
        private String timestamp;
        private String requestId;
    }
}
