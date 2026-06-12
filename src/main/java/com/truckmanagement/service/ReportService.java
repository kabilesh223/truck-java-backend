package com.truckmanagement.service;

import com.truckmanagement.model.Trip;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.*;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ReportService {

    private static final String[] HEADERS = {
        "ID","Date","Truck No","Driver Name","Loading Point","Delivery Point",
        "Weight(T)","Freight","Toll","Commission","Fuel L","Fuel Amt",
        "Expenses","Advance","Bill Amt","Total Trip","Balance"
    };

    public byte[] generate(List<Trip> trips, String type, String company) throws Exception {
        XSSFWorkbook wb = new XSSFWorkbook();

        if ("per_truck".equals(type)) {
            Map<String, List<Trip>> groups = trips.stream()
                    .collect(Collectors.groupingBy(t -> t.getTruckNo() != null ? t.getTruckNo() : "Unknown",
                            LinkedHashMap::new, Collectors.toList()));
            for (Map.Entry<String, List<Trip>> e : groups.entrySet())
                writeSheet(wb, e.getValue(), "Truck: " + e.getKey(), company, e.getKey().substring(0, Math.min(e.getKey().length(), 31)));

        } else if ("per_driver".equals(type)) {
            Map<String, List<Trip>> groups = trips.stream()
                    .collect(Collectors.groupingBy(t -> t.getDriverName() != null ? t.getDriverName() : "Unknown",
                            LinkedHashMap::new, Collectors.toList()));
            for (Map.Entry<String, List<Trip>> e : groups.entrySet())
                writeSheet(wb, e.getValue(), "Driver: " + e.getKey(), company, e.getKey().substring(0, Math.min(e.getKey().length(), 31)));

        } else if ("monthly".equals(type)) {
            Map<String, List<Trip>> groups = new LinkedHashMap<>();
            DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd-MM-yyyy");
            for (Trip t : trips) {
                String month;
                try { month = java.time.LocalDate.parse(t.getDate(), fmt).format(DateTimeFormatter.ofPattern("MMM yyyy")); }
                catch (Exception e) { month = "Unknown"; }
                groups.computeIfAbsent(month, k -> new ArrayList<>()).add(t);
            }
            for (Map.Entry<String, List<Trip>> e : groups.entrySet())
                writeSheet(wb, e.getValue(), "Month: " + e.getKey(), company, e.getKey().substring(0, Math.min(e.getKey().length(), 31)));

        } else {
            writeSheet(wb, trips, "TRIP SUMMARY REPORT", company, "Summary");
        }

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        wb.write(out);
        wb.close();
        return out.toByteArray();
    }

    private void writeSheet(XSSFWorkbook wb, List<Trip> trips, String subtitle, String company, String sheetName) {
        XSSFSheet ws = wb.getSheet(sheetName) != null ? wb.getSheet(sheetName) : wb.createSheet(sheetName);

        // Styles
        XSSFCellStyle titleStyle = wb.createCellStyle();
        XSSFFont titleFont = wb.createFont();
        titleFont.setBold(true); titleFont.setFontHeightInPoints((short)14);
        titleFont.setColor(new XSSFColor(new byte[]{(byte)0x1F,(byte)0x4E,(byte)0x79}, null));
        titleStyle.setFont(titleFont);
        titleStyle.setAlignment(HorizontalAlignment.CENTER);

        XSSFCellStyle headerStyle = wb.createCellStyle();
        headerStyle.setFillForegroundColor(new XSSFColor(new byte[]{(byte)0x1F,(byte)0x4E,(byte)0x79}, null));
        headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        XSSFFont headerFont = wb.createFont();
        headerFont.setBold(true); headerFont.setColor(IndexedColors.WHITE.getIndex());
        headerStyle.setFont(headerFont);
        headerStyle.setAlignment(HorizontalAlignment.CENTER);
        headerStyle.setWrapText(true);
        setBorder(headerStyle);

        XSSFCellStyle altStyle = wb.createCellStyle();
        altStyle.setFillForegroundColor(new XSSFColor(new byte[]{(byte)0xEB,(byte)0xF0,(byte)0xFA}, null));
        altStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        altStyle.setAlignment(HorizontalAlignment.CENTER);
        setBorder(altStyle);

        XSSFCellStyle normalStyle = wb.createCellStyle();
        normalStyle.setAlignment(HorizontalAlignment.CENTER);
        setBorder(normalStyle);

        XSSFCellStyle totalStyle = wb.createCellStyle();
        totalStyle.setFillForegroundColor(new XSSFColor(new byte[]{(byte)0xFF,(byte)0xD7,(byte)0x00}, null));
        totalStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        XSSFFont boldFont = wb.createFont(); boldFont.setBold(true);
        totalStyle.setFont(boldFont);
        totalStyle.setAlignment(HorizontalAlignment.CENTER);
        setBorder(totalStyle);

        // Title
        Row titleRow = ws.createRow(0);
        Cell titleCell = titleRow.createCell(0);
        titleCell.setCellValue(company + " - " + subtitle);
        titleCell.setCellStyle(titleStyle);
        ws.addMergedRegion(new CellRangeAddress(0, 0, 0, HEADERS.length - 1));
        titleRow.setHeightInPoints(28);

        // Generated date
        Row dateRow = ws.createRow(1);
        Cell dateCell = dateRow.createCell(0);
        dateCell.setCellValue("Generated: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm")));
        ws.addMergedRegion(new CellRangeAddress(1, 1, 0, HEADERS.length - 1));

        // Headers
        Row headerRow = ws.createRow(3);
        headerRow.setHeightInPoints(32);
        for (int i = 0; i < HEADERS.length; i++) {
            Cell c = headerRow.createCell(i);
            c.setCellValue(HEADERS[i]);
            c.setCellStyle(headerStyle);
        }

        // Data
        double[] totals = new double[10]; // freight,toll,comm,fuelL,fuelA,exp,adv,bill,total,bal
        for (int i = 0; i < trips.size(); i++) {
            Trip t = trips.get(i);
            Row row = ws.createRow(i + 4);
            XSSFCellStyle style = (i % 2 == 0) ? normalStyle : altStyle;
            Object[] vals = {
                t.getId(), t.getDate(), t.getTruckNo(), t.getDriverName(),
                t.getLoadingPoint(), t.getDeliveryPoint(), t.getWeight(),
                t.getFreight(), t.getToll(), t.getCommission(),
                t.getFuelLiters(), t.getFuelAmount(), t.getExpenses(),
                t.getAdvance(), t.getBillAmount(), t.getTotalTripAmount(), t.getBalanceAmount()
            };
            for (int j = 0; j < vals.length; j++) {
                Cell c = row.createCell(j);
                if (vals[j] instanceof Number) c.setCellValue(((Number)vals[j]).doubleValue());
                else if (vals[j] != null) c.setCellValue(vals[j].toString());
                c.setCellStyle(style);
            }
            double[] tripVals = {safe(t.getFreight()),safe(t.getToll()),safe(t.getCommission()),
                safe(t.getFuelLiters()),safe(t.getFuelAmount()),safe(t.getExpenses()),
                safe(t.getAdvance()),safe(t.getBillAmount()),safe(t.getTotalTripAmount()),safe(t.getBalanceAmount())};
            for (int j = 0; j < totals.length; j++) totals[j] += tripVals[j];
        }

        // Totals row
        Row totalRow = ws.createRow(trips.size() + 4);
        Cell tc = totalRow.createCell(0); tc.setCellValue("TOTAL"); tc.setCellStyle(totalStyle);
        int[] totalCols = {7,8,9,10,11,12,13,14,15,16};
        for (int i = 0; i < totalCols.length; i++) {
            Cell c = totalRow.createCell(totalCols[i]);
            c.setCellValue(Math.round(totals[i]*100.0)/100.0);
            c.setCellStyle(totalStyle);
        }

        // Column widths
        int[] widths = {6,12,12,16,18,18,10,12,10,12,10,12,12,12,12,14,14};
        for (int i = 0; i < widths.length; i++)
            ws.setColumnWidth(i, widths[i] * 256);
    }

    private void setBorder(XSSFCellStyle style) {
        style.setBorderBottom(BorderStyle.THIN); style.setBorderTop(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);   style.setBorderRight(BorderStyle.THIN);
        style.setBottomBorderColor(IndexedColors.GREY_25_PERCENT.getIndex());
        style.setTopBorderColor(IndexedColors.GREY_25_PERCENT.getIndex());
        style.setLeftBorderColor(IndexedColors.GREY_25_PERCENT.getIndex());
        style.setRightBorderColor(IndexedColors.GREY_25_PERCENT.getIndex());
    }

    private double safe(Double v) { return v == null ? 0 : v; }
}
