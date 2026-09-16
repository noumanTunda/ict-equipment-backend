package com.tundalabs.ictequipment.util;

import com.lowagie.text.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class PdfReportTemplate {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    private static final DateTimeFormatter DATE_ONLY_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public static Font getTitleFont() {
        return FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16);
    }

    public static Font getHeaderFont() {
        return FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12);
    }

    public static Font getNormalFont() {
        return FontFactory.getFont(FontFactory.HELVETICA, 10);
    }

    public static Font getSmallFont() {
        return FontFactory.getFont(FontFactory.HELVETICA, 8);
    }

    public static void addReportHeader(Document document, String reportTitle) throws DocumentException {
        document.add(new Paragraph(reportTitle, getTitleFont()));
        document.add(Chunk.NEWLINE);
    }

    public static void addReportMetadata(Document document, String... metadata) throws DocumentException {
        for (String meta : metadata) {
            document.add(new Paragraph(meta, getNormalFont()));
        }
        document.add(Chunk.NEWLINE);
    }

    public static void addReportTimestamp(Document document) throws DocumentException {
        document.add(new Paragraph("Generated: " + LocalDateTime.now().format(DATE_FORMATTER), getSmallFont()));
        document.add(Chunk.NEWLINE);
    }

    public static void addInstitutionalHeader(Document document, String institutionName, String department) throws DocumentException {
        document.add(new Paragraph(institutionName, getTitleFont()));
        document.add(new Paragraph(department, getHeaderFont()));
        document.add(Chunk.NEWLINE);
    }

    public static void addReportFooter(Document document, String footerText, int totalRecords) throws DocumentException {
        document.add(Chunk.NEWLINE);
        document.add(new Paragraph("Total Records: " + totalRecords, getNormalFont()));
        document.add(new Paragraph(footerText, getSmallFont()));
    }

    public static Table createTable(int columns, float[] columnWidths) {
        Table table = new Table(columns);
        table.setWidths(columnWidths);
        return table;
    }

    public static void addTableHeader(Table table, String... headers) {
        for (String header : headers) {
            table.addCell(new Cell(new Phrase(header, getHeaderFont())));
        }
    }

    public static void addTableRow(Table table, String... cells) {
        for (String cell : cells) {
            table.addCell(new Cell(new Phrase(cell != null ? cell : "N/A", getNormalFont())));
        }
    }

    public static void addTableRowWithFont(Table table, Font font, String... cells) {
        for (String cell : cells) {
            table.addCell(new Cell(new Phrase(cell != null ? cell : "N/A", font)));
        }
    }

    public static String formatDate(LocalDateTime dateTime) {
        return dateTime != null ? dateTime.format(DATE_FORMATTER) : "N/A";
    }

    public static String formatDateOnly(LocalDateTime dateTime) {
        return dateTime != null ? dateTime.format(DATE_ONLY_FORMATTER) : "N/A";
    }

    public static void addSectionHeader(Document document, String sectionTitle) throws DocumentException {
        document.add(new Paragraph(sectionTitle, getHeaderFont()));
        document.add(Chunk.NEWLINE);
    }

    public static void addParameterBlock(Document document, String label, String value) throws DocumentException {
        document.add(new Paragraph(label + ": " + (value != null ? value : "N/A"), getNormalFont()));
    }

    public static void addHorizontalLine(Document document) throws DocumentException {
        document.add(new Paragraph(new Chunk(new com.lowagie.text.pdf.draw.LineSeparator())));
    }

    public static void addEmptyRow(Document document) throws DocumentException {
        document.add(Chunk.NEWLINE);
    }
}
