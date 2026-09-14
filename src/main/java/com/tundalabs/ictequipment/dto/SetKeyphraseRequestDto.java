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
@Schema(description = "DTO for setting a keyphrase for transaction signing")
public class SetKeyphraseRequestDto {
    
    @Schema(description = "Keyphrase for signing transactions", example = "mySecretKey123")
    @NotBlank(message = "Keyphrase is required")
    @Size(min = 6, message = "Keyphrase must be at least 6 characters")
    private String keyphrase;
}
