package com.YipYapTimeAPI.YipYapTimeAPI.exception;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.OffsetDateTime;

@AllArgsConstructor
@Data
public class ErrorDetail {
    private String error;

    private String detail;

    private OffsetDateTime timestamp;
}
