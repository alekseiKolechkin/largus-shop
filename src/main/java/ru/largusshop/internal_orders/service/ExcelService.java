package ru.largusshop.internal_orders.service;

import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.springframework.stereotype.Service;
import ru.largusshop.internal_orders.model.CustomerOrder;
import ru.largusshop.internal_orders.model.Demand;
import ru.largusshop.internal_orders.model.Position;

import java.util.Iterator;
import java.util.List;

import static java.util.Objects.isNull;

@Service
public class ExcelService {
    public HSSFWorkbook getExcelFromDemands(List<Demand> demands) {
        HSSFWorkbook workbook = new HSSFWorkbook();
        for (Demand demand : demands) {
            HSSFSheet sheet = workbook.createSheet(demand.getName());
            int rowNum = 0;
            fillHeaderDemand(workbook, sheet, rowNum);
            for (Position position : demand.getPositions().getRows()) {
                createRowDemand(sheet, ++rowNum, position);
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

    private void fillHeaderDemand(HSSFWorkbook workbook, HSSFSheet sheet, int rowNum) {
        Row row = sheet.createRow(rowNum);
        row.createCell(0).setCellValue("Код");
        row.createCell(1).setCellValue("Наименование");
        row.createCell(2).setCellValue("Сумма себестоимости");
        row.createCell(3).setCellValue("Сумма по закупочной цене");
        row.createCell(4).setCellValue("Количество");
        row.createCell(5).setCellValue("Скидка за шт");
        row.setRowStyle(getBoldStyle(workbook));
    }

    private void fillHeaderCustomerOrder(HSSFWorkbook workbook, HSSFSheet sheet, int rowNum) {
        Row row = sheet.createRow(rowNum);
        row.createCell(0).setCellValue("Наименование");
        row.createCell(1).setCellValue("Количество");
        row.setRowStyle(getBoldStyle(workbook));
    }

    private void createRowDemand(HSSFSheet sheet, int rowNum, Position position) {
        Row row = sheet.createRow(rowNum);
        int displayedRowNum = rowNum + 1;
        row.createCell(0).setCellValue(position.getAssortment().getCode());
        row.createCell(1).setCellValue(position.getAssortment().getName());
        row.createCell(2).setCellValue(position.getCost());
        row.createCell(3).setCellValue((isNull(position.getAssortment().getBuyPrice()) ? position.getAssortment().getProduct().getBuyPrice().getValue() : position.getAssortment().getBuyPrice().getValue()) * position.getQuantity());
        row.createCell(4).setCellValue(position.getQuantity());
        row.createCell(5).setCellFormula("(D" + displayedRowNum + "-C" + displayedRowNum + ")/E" + displayedRowNum);
    }

    private void createRowCustomerOrder(HSSFSheet sheet, int rowNum, Position position) {
        Row row = sheet.createRow(rowNum);
        row.createCell(0).setCellValue(position.getAssortment().getName());
        row.createCell(1).setCellValue(position.getQuantity());
    }

    private HSSFCellStyle getBoldStyle(HSSFWorkbook workbook) {
        HSSFFont font = workbook.createFont();
        font.setBold(true);
        HSSFCellStyle style = workbook.createCellStyle();
        style.setFont(font);
        return style;
    }

    public HSSFWorkbook getExcelFromCustomerOrder(CustomerOrder customerOrder, Integer percent){
        HSSFWorkbook workbook = new HSSFWorkbook();
        HSSFSheet sheet = workbook.createSheet(customerOrder.getName());
        int rowNum = 0;
        fillHeaderCustomerOrder(workbook, sheet, rowNum);
        for (Position position : customerOrder.getPositions().getRows()) {
            createRowCustomerOrder(sheet, ++rowNum, position);
        }
        Row lastRow = sheet.createRow(++rowNum);
        lastRow.createCell(0).setCellValue("Процент суммы успешной отгрузки:");
        lastRow.createCell(1).setCellValue(percent);
        Row row = sheet.getRow(0);
        Iterator<Cell> cellIterator = row.cellIterator();
        while (cellIterator.hasNext()) {
            Cell cell = cellIterator.next();
            int columnIndex = cell.getColumnIndex();
            sheet.autoSizeColumn(columnIndex);
        }
        return workbook;
    }
//
//    public void processExcel() throws IOException {
//        Workbook workbook = new HSSFWorkbook(new FileInputStream("C:\\Users\\Aleksei_Kolechkin\\Downloads\\company-admin_sig-2018-09-06-12-14-24.xls"));
//        Sheet sheet = workbook.getSheetAt(0);
//        Map<String, String> map = new HashMap<>();
//        for (Row row : sheet) {
//            map.put(row.getCell(0).getStringCellValue(), row.getCell(1).getStringCellValue());
//        }
//        System.out.println("S");
//
//    }
}
