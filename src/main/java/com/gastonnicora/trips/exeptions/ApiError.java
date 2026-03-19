package com.gastonnicora.trips.exeptions;

import java.time.LocalDateTime;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ApiError {

    private int status;
    private String message;
    private LocalDateTime timestamp;
    private Map<String, String> errors;

}