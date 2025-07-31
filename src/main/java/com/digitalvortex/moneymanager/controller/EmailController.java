package com.digitalvortex.moneymanager.controller;

import com.digitalvortex.moneymanager.model.Profile;
import com.digitalvortex.moneymanager.service.*;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

@RestController
@RequestMapping("/email")
@RequiredArgsConstructor
public class EmailController {
    private final IncomeService incomeService;

    private final ExcelService excelService;

    private final ExpenseService expenseService;

    private ProfileService profileService;

    private EmailService emailService;

    @GetMapping("income-excel")
    public ResponseEntity<Void> emailIncomeExcel() throws IOException, MessagingException {
        Profile profile = profileService.getCurrentProfile();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        excelService.writeIncomesToExcel(baos, incomeService.incomesForCurrentUser());
        emailService.sendEmailWithAttachment(profile.getEmail(),"Your Income data report for this month", "please find attached your income report", baos.toByteArray(), "income.xlsx");

        return ResponseEntity.ok(null);
    }


    @GetMapping("expense-excel")
    public ResponseEntity<Void> emailExpenseExcel() throws IOException, MessagingException {
        Profile profile = profileService.getCurrentProfile();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        excelService.writeExpensesToExcel(baos, expenseService.expensesForCurrentUser());
        emailService.sendEmailWithAttachment(profile.getEmail(),"Your expense data report for this month", "please find attached your expenses report", baos.toByteArray(), "income.xlsx");

        return ResponseEntity.ok(null);
    }
}
