package com.digitalvortex.moneymanager.service;

import com.digitalvortex.moneymanager.dto.ExpenseDTO;
import com.digitalvortex.moneymanager.dto.IncomeDTO;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.stream.IntStream;

@Service
public class ExcelService {

    public void writeIncomesToExcel(OutputStream os, List<IncomeDTO> incomes) throws IOException {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Income Report");
            Row headerRow = sheet.createRow(0);

            // Create headers
            headerRow.createCell(0).setCellValue("S.NO");
            headerRow.createCell(1).setCellValue("Name");
            headerRow.createCell(2).setCellValue("Category");
            headerRow.createCell(3).setCellValue("Amount");
            headerRow.createCell(4).setCellValue("Date");

            // Create data rows
            IntStream.range(0, incomes.size())
                    .forEach(i -> {
                        IncomeDTO income = incomes.get(i);
                        Row row = sheet.createRow(i + 1);
                        row.createCell(0).setCellValue(i + 1);
                        row.createCell(1).setCellValue(income.getName() != null ? income.getName() : "N/A");
                        row.createCell(2).setCellValue(income.getCategoryId() != null ? income.getCategoryName() : "N/A");
                        row.createCell(3).setCellValue(income.getAmount() != null ? income.getAmount().doubleValue() : 0);
                        row.createCell(4).setCellValue(income.getDate() != null ? income.getDate().toString() : "N/A");
                    });

            // Auto-size columns for better readability
            for (int i = 0; i < 5; i++) {
                sheet.autoSizeColumn(i);
            }

            // Write workbook to output stream ONCE, after all data is added
            workbook.write(os);

        } catch (Exception e) {
            throw new RuntimeException("Error writing incomes to Excel", e);
        }
    }

    public void writeExpensesToExcel(OutputStream os, List<ExpenseDTO> expenses) throws IOException {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Expense Report");
            Row headerRow = sheet.createRow(0);

            // Create headers
            headerRow.createCell(0).setCellValue("S.NO");
            headerRow.createCell(1).setCellValue("Name");
            headerRow.createCell(2).setCellValue("Category");
            headerRow.createCell(3).setCellValue("Amount");
            headerRow.createCell(4).setCellValue("Date");

            // Create data rows
            IntStream.range(0, expenses.size())
                    .forEach(i -> {
                        ExpenseDTO expense = expenses.get(i);
                        Row row = sheet.createRow(i + 1);
                        row.createCell(0).setCellValue(i + 1);
                        row.createCell(1).setCellValue(expense.getName() != null ? expense.getName() : "N/A");
                        row.createCell(2).setCellValue(expense.getCategoryId() != null ? expense.getCategoryName() : "N/A");
                        row.createCell(3).setCellValue(expense.getAmount() != null ? expense.getAmount().doubleValue() : 0);
                        row.createCell(4).setCellValue(expense.getDate() != null ? expense.getDate().toString() : "N/A");
                    });

            // Auto-size columns for better readability
            for (int i = 0; i < 5; i++) {
                sheet.autoSizeColumn(i);
            }

            // Write workbook to output stream ONCE, after all data is added
            workbook.write(os);

        } catch (Exception e) {
            throw new RuntimeException("Error writing expenses to Excel", e);
        }
    }
}