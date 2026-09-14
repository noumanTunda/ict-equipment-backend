package com.tundalabs.ictequipment.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "DTO for updating a keyphrase for transaction signing")
public class UpdateKeyphraseRequestDto {
    
    @Schema(description = "Current keyphrase", example = "oldSecretKey123")
    @NotBlank(message = "Current keyphrase is required")
    @Size(min = 6, message = "Current keyphrase must be at least 6 characters")
    private String currentKeyphrase;
    
    @Schema(description = "New keyphrase", example = "newSecretKey456")
    @NotBlank(message = "New keyphrase is required")
    @Size(min = 6, message = "New keyphrase must be at least 6 characters")
    private String newKeyphrase;
}
