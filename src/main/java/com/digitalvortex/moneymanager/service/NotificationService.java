package com.digitalvortex.moneymanager.service;

import com.digitalvortex.moneymanager.dto.ExpenseDTO;
import com.digitalvortex.moneymanager.model.Profile;
import com.digitalvortex.moneymanager.repository.ProfileRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {
    private final ProfileRepository profileRepository;
    private final EmailService emailService;
    private final ExpenseService expenseService;

    @Value("${money.manager.frontend.url}")
    private String frontEndUrl;

    //@Scheduled(cron = "0 * * * * *", zone = "EAT")
    @Scheduled(cron = "0 0 22 * * *", zone = "EAT")
    public void sendDailyEmailNotifications() {


        // TODO: Add logic to get users and send emails
        // profileRepository.findAll().forEach(profile -> {
        //     emailService.sendHtmlEmail(profile.getEmail(), "Daily Financial Reminder", body);
        // });

        log.info("Daily email notifications sent at 22:00 EAT");

        List<Profile> profiles = profileRepository.findAll();
        for (Profile profile : profiles){
            String body = "<div style=\"font-family: 'Segoe UI', Arial, sans-serif; max-width: 600px; margin: 0 auto; background: #ffffff; border-radius: 12px; overflow: hidden; box-shadow: 0 4px 20px rgba(0,0,0,0.1);\">" +

                    "<!-- Header -->" +
                    "<div style=\"background: linear-gradient(135deg, #4CAF50 0%, #45a049 100%); padding: 30px; text-align: center; color: white;\">" +
                    "<div style=\"font-size: 40px; margin-bottom: 10px;\">💰</div>" +
                    "<h1 style=\"margin: 0; font-size: 24px; font-weight: 600;\">Daily Financial Check-in</h1>" +
                    "<p style=\"margin: 8px 0 0 0; opacity: 0.9; font-size: 16px;\">Keep your finances on track</p>" +
                    "</div>" +

                    "<!-- Content -->" +
                    "<div style=\"padding: 30px;\">" +

                    "<p style=\"font-size: 18px; color: #333; margin: 0 0 20px 0; line-height: 1.5;\">" +
                    "Hi there! 👋" +
                    "</p>" +

                    "<p style=\"font-size: 16px; color: #555; line-height: 1.6; margin: 0 0 25px 0;\">" +
                    "Just a friendly reminder to log your daily income and expenses. Staying consistent with tracking your finances is key to reaching your financial goals!" +
                    "</p>" +

                    "<!-- Stats Cards -->" +
                    "<div style=\"display: flex; gap: 15px; margin: 25px 0; flex-wrap: wrap;\">" +
                    "<div style=\"flex: 1; min-width: 120px; background: linear-gradient(135deg, #e8f5e8 0%, #f0f8f0 100%); padding: 20px; border-radius: 10px; text-align: center; border: 2px solid #4CAF50;\">" +
                    "<div style=\"font-size: 24px; margin-bottom: 5px;\">📈</div>" +
                    "<div style=\"font-size: 14px; color: #4CAF50; font-weight: 600;\">INCOME</div>" +
                    "</div>" +
                    "<div style=\"flex: 1; min-width: 120px; background: linear-gradient(135deg, #fff0f0 0%, #fdf5f5 100%); padding: 20px; border-radius: 10px; text-align: center; border: 2px solid #f44336;\">" +
                    "<div style=\"font-size: 24px; margin-bottom: 5px;\">📉</div>" +
                    "<div style=\"font-size: 14px; color: #f44336; font-weight: 600;\">EXPENSES</div>" +
                    "</div>" +
                    "</div>" +

                    "<!-- Call to Action -->" +
                    "<div style=\"background: linear-gradient(135deg, #f8f9ff 0%, #e8f2ff 100%); padding: 25px; border-radius: 12px; border-left: 5px solid #4CAF50; margin: 25px 0;\">" +
                    "<p style=\"margin: 0 0 15px 0; font-size: 16px; color: #333; font-weight: 500;\">" +
                    "⏰ Take 2 minutes now to:" +
                    "</p>" +
                    "<ul style=\"margin: 0; padding-left: 20px; color: #555; line-height: 1.8;\">" +
                    "<li>Add today's income sources</li>" +
                    "<li>Record your expenses</li>" +
                    "<li>Check your budget progress</li>" +
                    "</ul>" +
                    "</div>" +

                    "<!-- Button -->" +
                    "<div style=\"text-align: center; margin: 30px 0;\">" +
                    "<a href=\"" + frontEndUrl + "\" style=\"display: inline-block; background: linear-gradient(135deg, #4CAF50 0%, #45a049 100%); color: white; text-decoration: none; padding: 15px 35px; border-radius: 25px; font-weight: 600; font-size: 16px; box-shadow: 0 4px 15px rgba(76, 175, 80, 0.3); transition: all 0.3s ease;\">" +
                    "📱 Open Money Manager" +
                    "</a>" +
                    "</div>" +

                    "<!-- Footer Message -->" +
                    "<div style=\"background: #f9f9f9; padding: 20px; border-radius: 8px; text-align: center; margin-top: 25px;\">" +
                    "<p style=\"margin: 0; font-size: 14px; color: #777; line-height: 1.5;\">" +
                    "💡 <strong>Pro tip:</strong> Regular tracking leads to better financial decisions and helps you spot spending patterns!" +
                    "</p>" +
                    "</div>" +

                    "<p style=\"font-size: 14px; color: #888; text-align: center; margin: 20px 0 0 0;\">" +
                    "Stay financially fit! 💪<br>" +
                    "<span style=\"color: #4CAF50; font-weight: 500;\">Your Money Manager Team</span>" +
                    "</p>" +

                    "</div>" +
                    "</div>";

            emailService.sendEmail(profile.getEmail(),"Daily Reminder: Add your income and expenses", body);

        }
    }


    @Scheduled(cron = "0 0 23 * * *", zone = "EAT")
    //@Scheduled(cron = "0 0 22 * * *", zone = "EAT")
    public void sendDailyExpenseSummary(){
        log.info("Daily expense summary job started");
        List<Profile> profiles = profileRepository.findAll();

        for (Profile profile : profiles){
            List<ExpenseDTO> expenses = expenseService.getExpencesForUserForDate(profile.getId(), LocalDate.now());
            log.info("THESE ATE THE EXPESES FOR USER"+profile.getEmail()+" :"+expenses.toString());
            if(!expenses.isEmpty()){
                StringBuilder emailBody = new StringBuilder();

                // Email header
                emailBody.append("<div style=\"font-family: 'Segoe UI', Arial, sans-serif; max-width: 700px; margin: 0 auto; background: #ffffff; border-radius: 12px; overflow: hidden; box-shadow: 0 4px 20px rgba(0,0,0,0.1);\">");

                // Header section
                emailBody.append("<div style=\"background: linear-gradient(135deg, #FF6B6B 0%, #E55353 100%); padding: 25px; text-align: center; color: white;\">");
                emailBody.append("<div style=\"font-size: 36px; margin-bottom: 8px;\">📊</div>");
                emailBody.append("<h1 style=\"margin: 0; font-size: 22px; font-weight: 600;\">Daily Expense Summary</h1>");
                emailBody.append("<p style=\"margin: 5px 0 0 0; opacity: 0.9; font-size: 14px;\">").append(LocalDate.now().format(DateTimeFormatter.ofPattern("MMMM dd, yyyy"))).append("</p>");
                emailBody.append("</div>");

                // Content section
                emailBody.append("<div style=\"padding: 25px;\">");
                emailBody.append("<p style=\"font-size: 16px; color: #333; margin: 0 0 20px 0;\">Hi ").append(profile.getFullName() != null ? profile.getFullName() : "").append(",</p>");
                emailBody.append("<p style=\"font-size: 14px; color: #666; margin: 0 0 25px 0;\">Here's your expense summary for today:</p>");

                // Table with proper styling
                emailBody.append("<table style=\"width: 100%; border-collapse: collapse; margin: 20px 0; background: white; border-radius: 8px; overflow: hidden; box-shadow: 0 2px 10px rgba(0,0,0,0.1);\">");

                // Table header
                emailBody.append("<thead>");
                emailBody.append("<tr style=\"background: linear-gradient(135deg, #4CAF50 0%, #45a049 100%); color: white;\">");
                emailBody.append("<th style=\"padding: 15px 12px; text-align: left; font-weight: 600; font-size: 14px; border: none;\">SNO</th>");
                emailBody.append("<th style=\"padding: 15px 12px; text-align: left; font-weight: 600; font-size: 14px; border: none;\">Name</th>");
                emailBody.append("<th style=\"padding: 15px 12px; text-align: right; font-weight: 600; font-size: 14px; border: none;\">Amount</th>");
                emailBody.append("<th style=\"padding: 15px 12px; text-align: center; font-weight: 600; font-size: 14px; border: none;\">Category</th>");
                emailBody.append("</tr>");
                emailBody.append("</thead>");

                // Table body
                emailBody.append("<tbody>");

                BigDecimal totalAmount = BigDecimal.ZERO;
                int rowIndex = 0;

                for (ExpenseDTO expense : expenses) {
                    String rowColor = rowIndex % 2 == 0 ? "#f8f9fa" : "#ffffff";

                    emailBody.append("<tr style=\"background-color: ").append(rowColor).append("; border-bottom: 1px solid #e9ecef;\">");

                    // Description
                    emailBody.append("<td style=\"padding: 12px; border: none; font-size: 13px; color: #333;\">");
                    emailBody.append(rowIndex++);
                    emailBody.append("</td>");

                    // Category
                    emailBody.append("<td style=\"padding: 12px; border: none; font-size: 13px;\">");
                    emailBody.append("<span style=\"background: #e3f2fd; color: #1976d2; padding: 4px 8px; border-radius: 12px; font-size: 12px; font-weight: 500;\">");
                    emailBody.append(expense.getName() != null ? expense.getName() : "not named");
                    emailBody.append("</span>");
                    emailBody.append("</td>");

                    // Amount
                    emailBody.append("<td style=\"padding: 12px; border: none; font-size: 13px; text-align: right; font-weight: 600; color: #d32f2f;\">");
                    emailBody.append("$").append(String.format("%.2f", expense.getAmount()));
                    emailBody.append("</td>");

                    emailBody.append("<td style=\"padding: 12px; border: none; font-size: 13px;\">");
                    emailBody.append("<span style=\"background: #e3f2fd; color: #1976d2; padding: 4px 8px; border-radius: 12px; font-size: 12px; font-weight: 500;\">");
                    emailBody.append(expense.getCategoryId() != null ? expense.getCategoryName() : "not categorized");
                    emailBody.append("</span>");
                    emailBody.append("</td>");

                    emailBody.append("</tr>");



                  //  totalAmount += expense.getAmount();
                    rowIndex++;
                }

                // Total row
                emailBody.append("<tr style=\"background: linear-gradient(135deg, #f5f5f5 0%, #eeeeee 100%); border-top: 2px solid #4CAF50;\">");
                emailBody.append("<td colspan=\"2\" style=\"padding: 15px 12px; border: none; font-weight: 700; font-size: 14px; color: #333;\">📊 TOTAL EXPENSES</td>");
               // emailBody.append("<td style=\"padding: 15px 12px; border: none; font-weight: 700; font-size: 16px; text-align: right; color: #d32f2f;\">$").append(String.format("%.2f", totalAmount)).append("</td>");
                emailBody.append("<td style=\"padding: 15px 12px; border: none; text-align: center; font-size: 14px; color: #666;\">").append(expenses.size()).append(" items</td>");
                emailBody.append("</tr>");

                emailBody.append("</tbody>");
                emailBody.append("</table>");

                // Footer message
                emailBody.append("<div style=\"background: #f0f8f0; padding: 15px; border-radius: 8px; margin-top: 20px; text-align: center;\">");
                emailBody.append("<p style=\"margin: 0; font-size: 13px; color: #4CAF50; font-weight: 500;\">💡 Keep tracking your expenses to stay within budget!</p>");
                emailBody.append("</div>");

                // App link
                emailBody.append("<div style=\"text-align: center; margin: 25px 0 10px 0;\">");
                emailBody.append("<a href=\"").append(frontEndUrl).append("\" style=\"display: inline-block; background: linear-gradient(135deg, #4CAF50 0%, #45a049 100%); color: white; text-decoration: none; padding: 12px 25px; border-radius: 20px; font-weight: 600; font-size: 14px; box-shadow: 0 3px 10px rgba(76, 175, 80, 0.3);\">");
                emailBody.append("📱 Open Money Manager");
                emailBody.append("</a>");
                emailBody.append("</div>");

                emailBody.append("</div>"); // Close content div
                emailBody.append("</div>"); // Close main container

                // Send email (assuming you have emailService method)
                String subject = "Daily Expense Summary - " + LocalDate.now().format(DateTimeFormatter.ofPattern("MMM dd, yyyy"));
                emailService.sendEmail(profile.getEmail(), subject, emailBody.toString());

                log.info("Expense summary email prepared for user: {}", profile.getEmail());
            } else {
                log.info("No expenses found for user: {} on {}", profile.getEmail(), LocalDate.now());
            }
        }

        log.info("Daily expense summary job completed");
    }
}