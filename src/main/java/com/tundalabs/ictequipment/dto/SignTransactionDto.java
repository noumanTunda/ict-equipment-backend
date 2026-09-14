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
@Schema(description = "DTO for signing a transaction with keyphrase")
public class SignTransactionDto {
    
    @Schema(description = "Keyphrase for signing (will be hashed and validated)", example = "mySecretKey123")
    @NotBlank(message = "Keyphrase is required")
    private String keyphrase;
}
