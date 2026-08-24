package com.tundalabs.ictequipment.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<Type> {
    private int statusCode;
    private String message;
    private LocalDateTime timestamp;
    private Type payload;

    public static <Type> ApiResponse<Type> success(int statusCode, String message, Type payload) {
        return ApiResponse.<Type>builder()
                .statusCode(statusCode)
                .message(message)
                .timestamp(LocalDateTime.now())
                .payload(payload)
                .build();
    }

    public static <Type> ApiResponse<Type> success(int statusCode, String message) {
        return ApiResponse.<Type>builder()
                .statusCode(statusCode)
                .message(message)
                .timestamp(LocalDateTime.now())
                .build();
    }

    public static <Type> ApiResponse<Type> error(int statusCode, String message) {
        return ApiResponse.<Type>builder()
                .statusCode(statusCode)
                .message(message)
                .timestamp(LocalDateTime.now())
                .build();
    }
}
