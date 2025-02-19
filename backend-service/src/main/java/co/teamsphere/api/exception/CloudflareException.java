package co.teamsphere.api.exception;

import lombok.Data;

@Data
public class CloudflareException {
    private int code;
    private String message;
}
