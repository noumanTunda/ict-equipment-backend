package com.tundalabs.ictequipment.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IctChecklistResponseDto {
    private Long id;
    private String osInstalled;
    private String appSystemInstalled;
    private String antiVirusInstalled;
    private String pdfReaderInstalled;
    private Boolean isJoinedToDomain;
    private Boolean isInstalledVpn;
    private Boolean isInstalledPrinter;
    private String additionalNotes;
}
