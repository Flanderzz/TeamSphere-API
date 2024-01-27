package com.YipYapTimeAPI.YipYapTimeAPI.exception;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ErrorDetail {

    private String error;
    private String detail;
    private LocalDateTime timestamp;

}
