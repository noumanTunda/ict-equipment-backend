package com.tundalabs.ictequipment.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Response DTO for equipment transaction details")
public class TransactionResponseDto {
    @Schema(description = "Transaction ID", example = "1")
    private Long id;
    
    @Schema(description = "Unique transaction code", example = "TXN-20240126120000-ABC12345")
    private String transactionCode;
    
    @Schema(description = "Staff member ID", example = "1")
    private Long staffId;
    
    @Schema(description = "Staff member full name", example = "John Doe")
    private String staffName;
    
    @Schema(description = "Issuing officer ID", example = "2")
    private Long issuingOfficerId;
    
    @Schema(description = "Issuing officer full name", example = "Jane Smith")
    private String issuingOfficerName;
    
    @Schema(description = "Transaction status", example = "COMPLETED")
    private String status;
    
    @Schema(description = "Employee signature (base64 encoded)")
    private String employeeSignature;
    
    @Schema(description = "Officer signature (base64 encoded)")
    private String officerSignature;
    
    @Schema(description = "Employee signature timestamp")
    private LocalDateTime employeeSignedAt;
    
    @Schema(description = "Officer signature timestamp")
    private LocalDateTime officerSignedAt;
    
    @Schema(description = "Transaction creation timestamp")
    private LocalDateTime createdAt;
    
    @Schema(description = "Transaction last update timestamp")
    private LocalDateTime updatedAt;
    
    @Schema(description = "List of issued items")
    private List<IssuedItemResponseDto> issuedItems;
    
    @Schema(description = "List of returned items")
    private List<ReturnedItemResponseDto> returnedItems;
    
    @Schema(description = "ICT checklist details")
    private IctChecklistResponseDto checklist;
}
