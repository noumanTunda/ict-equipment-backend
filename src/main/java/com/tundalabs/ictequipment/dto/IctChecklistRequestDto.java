package com.tundalabs.ictequipment.dto;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IctChecklistRequestDto {
    @Size(max = 100, message = "OS installed must not exceed 100 characters")
    private String osInstalled;

    @Size(max = 100, message = "App system installed must not exceed 100 characters")
    private String appSystemInstalled;

    @Size(max = 100, message = "Anti-virus installed must not exceed 100 characters")
    private String antiVirusInstalled;

    @Size(max = 100, message = "PDF reader installed must not exceed 100 characters")
    private String pdfReaderInstalled;

    private Boolean isJoinedToDomain;

    private Boolean isInstalledVpn;

    private Boolean isInstalledPrinter;

    @Size(max = 500, message = "Additional notes must not exceed 500 characters")
    private String additionalNotes;
}
