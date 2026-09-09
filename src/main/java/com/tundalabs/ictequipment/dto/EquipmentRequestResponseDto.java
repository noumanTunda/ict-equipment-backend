package com.tundalabs.ictequipment.dto;

import com.tundalabs.ictequipment.entity.EquipmentRequest;
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
@Schema(description = "Response DTO for equipment request")
public class EquipmentRequestResponseDto {

    @Schema(description = "Request ID", example = "1")
    private Long id;

    @Schema(description = "Unique request code", example = "REQ-20240126120000-ABC12345")
    private String requestCode;

    @Schema(description = "Staff ID", example = "1")
    private Long staffId;

    @Schema(description = "Staff name", example = "John Doe")
    private String staffName;

    @Schema(description = "Request type", example = "EXCHANGE")
    private EquipmentRequest.RequestType requestType;

    @Schema(description = "Reason for request", example = "Need to replace old laptop with new one")
    private String reason;

    @Schema(description = "Request status", example = "PENDING")
    private EquipmentRequest.RequestStatus status;

    @Schema(description = "Rejection reason (if rejected)")
    private String rejectionReason;

    @Schema(description = "ID of officer who approved the request")
    private Long approvedBy;

    @Schema(description = "Name of officer who approved the request")
    private String approvedByName;

    @Schema(description = "Preferred equipment type (for ISSUE or EXCHANGE requests)", example = "LAPTOP")
    private String preferredEquipmentType;

    @Schema(description = "Approval timestamp")
    private LocalDateTime approvedAt;

    @Schema(description = "Request creation timestamp")
    private LocalDateTime createdAt;

    @Schema(description = "Request last update timestamp")
    private LocalDateTime updatedAt;

    @Schema(description = "Associated transaction ID (if approved)")
    private Long transactionId;
}
