package com.tundalabs.ictequipment.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(description = "Generic API response wrapper")
public class ApiResponse<Type> {
    @Schema(description = "HTTP status code", example = "200")
    private int statusCode;
    
    @Schema(description = "Response message", example = "Operation successful")
    private String message;
    
    @Schema(description = "Response timestamp")
    private LocalDateTime timestamp;
    
    @Schema(description = "Response payload/data")
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
