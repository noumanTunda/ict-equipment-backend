package com.tundalabs.ictequipment.service.impl;

import com.lowagie.text.*;
import com.lowagie.text.pdf.*;
import java.awt.Color;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class PdfHeaderFooterEvent extends PdfPageEventHelper {

    private final Font headerFont;
    private final Font footerFont;
    private final Color primaryColor;
    private final Color borderGray;
    private final String printTimestamp;

    public PdfHeaderFooterEvent() {
        this.primaryColor = new Color(19, 148, 219);
        this.borderGray = new Color(226, 232, 240);
        this.headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 7, new Color(100, 116, 139));
        this.footerFont = FontFactory.getFont(FontFactory.HELVETICA, 7, new Color(100, 116, 139));
        this.printTimestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }

    @Override
    public void onEndPage(PdfWriter writer, Document document) {
        PdfContentByte cb = writer.getDirectContent();
        float left = document.leftMargin();
        float right = document.getPageSize().getWidth() - document.rightMargin();
        float top = document.getPageSize().getHeight() - document.topMargin();
        float bottom = document.bottomMargin();

        // --- HEADER CONTENT & SEPARATOR ---
        // Top Header Text (Centered)
        ColumnText.showTextAligned(
                cb,
                Element.ALIGN_CENTER,
                new Phrase("ICT EQUIPMENT MANAGEMENT SYSTEM", headerFont),
                (left + right) / 2,
                top + 12,
                0
        );

        // Header Double Line: Heavy line on top (1.5pt), light line below (0.5pt)
        cb.saveState();
        cb.setColorStroke(primaryColor);

        // Heavy Line (Top)
        cb.setLineWidth(1.5f);
        cb.moveTo(left, top + 6);
        cb.lineTo(right, top + 6);
        cb.stroke();

        // Light Line (Bottom)
        cb.setColorStroke(borderGray);
        cb.setLineWidth(0.5f);
        cb.moveTo(left, top + 3);
        cb.lineTo(right, top + 3);
        cb.stroke();
        cb.restoreState();

        // --- FOOTER CONTENT & SEPARATOR ---
        // Footer Double Line: Light line on top (0.5pt), heavy line below (1.5pt)
        cb.saveState();
        cb.setColorStroke(borderGray);

        // Light Line (Top)
        cb.setLineWidth(0.5f);
        cb.moveTo(left, bottom - 3);
        cb.lineTo(right, bottom - 3);
        cb.stroke();

        // Heavy Line (Bottom)
        cb.setColorStroke(primaryColor);
        cb.setLineWidth(1.5f);
        cb.moveTo(left, bottom - 6);
        cb.lineTo(right, bottom - 6);
        cb.stroke();
        cb.restoreState();

        // Centered Footer Text (Print Timestamp + Page Number)
        String footerText = String.format("Printed on: %s  |  Page %d", printTimestamp, writer.getPageNumber());
        ColumnText.showTextAligned(
                cb,
                Element.ALIGN_CENTER,
                new Phrase(footerText, footerFont),
                (left + right) / 2,
                bottom - 16,
                0
        );
    }
}