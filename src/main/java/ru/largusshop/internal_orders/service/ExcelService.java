package ru.largusshop.internal_orders.service;

import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.springframework.stereotype.Service;
import ru.largusshop.internal_orders.model.Demand;
import ru.largusshop.internal_orders.model.Position;

import java.util.Iterator;
import java.util.List;

@Service
public class ExcelService {
    public HSSFWorkbook getExcelFromDemands(List<Demand> demands) {
        HSSFWorkbook workbook = new HSSFWorkbook();
        for (Demand demand : demands) {
            HSSFSheet sheet = workbook.createSheet(demand.getName());
            int rowNum = 0;
            fillHeader(workbook, sheet, rowNum);
            for (Position position : demand.getPositions().getRows()) {
                createRow(sheet, ++rowNum, position);
            }
            Row row = sheet.getRow(0);
            Iterator<Cell> cellIterator = row.cellIterator();
            while (cellIterator.hasNext()) {
                Cell cell = cellIterator.next();
                int columnIndex = cell.getColumnIndex();
                sheet.autoSizeColumn(columnIndex);
            }
        }
        return workbook;
    }

    private void fillHeader(HSSFWorkbook workbook, HSSFSheet sheet, int rowNum) {
        Row row = sheet.createRow(rowNum);
        row.createCell(0).setCellValue("Код");
        row.createCell(1).setCellValue("Наименование");
        row.createCell(2).setCellValue("Сумма себестоимости");
        row.createCell(3).setCellValue("Сумма по закупочной цене");
        row.createCell(4).setCellValue("Количество");
        row.createCell(5).setCellValue("Скидка за шт");
        row.setRowStyle(getBoldStyle(workbook));
    }

    private void createRow(HSSFSheet sheet, int rowNum, Position position) {
        Row row = sheet.createRow(rowNum);
        int displayedRowNum = rowNum + 1;
        row.createCell(0).setCellValue(position.getAssortment().getCode());
        row.createCell(1).setCellValue(position.getAssortment().getName());
        row.createCell(2).setCellValue(position.getCost());
        row.createCell(3).setCellValue(position.getAssortment().getBuyPrice().getValue() * position.getQuantity());
        row.createCell(4).setCellValue(position.getQuantity());
        row.createCell(5).setCellFormula("(D" + displayedRowNum + "-C" + displayedRowNum + ")/D" + displayedRowNum);
    }

    private HSSFCellStyle getBoldStyle(HSSFWorkbook workbook) {
        HSSFFont font = workbook.createFont();
        font.setBold(true);
        HSSFCellStyle style = workbook.createCellStyle();
        style.setFont(font);
        return style;
    }
}
