package com.YipYapTimeAPI.YipYapTimeAPI.exception;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@AllArgsConstructor
@Data
public class ErrorDetail {

    private String error;

    private String detail;

    private LocalDateTime timestamp;
}
