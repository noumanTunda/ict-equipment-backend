package com.tundalabs.ictequipment.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request DTO for rejecting equipment request")
public class RejectEquipmentRequestDto {

    @NotBlank(message = "Rejection reason is required")
    @Schema(description = "Reason for rejecting the request", example = "Equipment not available")
    private String rejectionReason;
}
