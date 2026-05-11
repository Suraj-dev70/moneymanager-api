package com.moneymanagerproject.service;

import com.moneymanagerproject.dto.ExpenseDto;
import com.moneymanagerproject.entity.ExpenseEntity;
import com.moneymanagerproject.entity.ProfileEntity;
import com.moneymanagerproject.repository.ExpenseRepository;
import com.moneymanagerproject.repository.ProfileRepository;
import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {
    private final ExpenseRepository expenseRepository;
    private final EmailService emailService;
    private final ProfileRepository profileRepository;
    private final ExpenseService expenseService;
    @Value("${money.manager.frontend.url}")
    private String frontendUrl;
    //@Scheduled(cron = "0 * * * * *",zone = "IST")
    @Scheduled(cron = "0 0 22 * * *",zone = "IST")
    public void sendDailyReminder(){
     log.info("Job started: sendDailyIncomeAndExpenseRemainder()");
        List<ProfileEntity> profiles =  profileRepository.findAll();
        for(ProfileEntity profile:profiles){
            String body = "Hi, " + profile.getFullName() + "<br><br>"
                    + "This is a friendly reminder to add your income or expenses in Money Manager.<br><br>"
                    + "<a href='" + frontendUrl + "' "
                    + "style='display:inline-block;padding:10px 20px;background:#4CAF50;color:#fff;text-decoration:none;border-radius:5px;'>"
                    + "Open Money Manager</a>"
                    + "<br><br>Best regards,<br>Money Manager Team";
            emailService.sendSimpleMail(profile.getEmail(),"Daily Reminder:Add Income and Expenses in Money Manager",body);
        }
        log.info("Job completed: sendDailyIncomeAndExpenseRemainder()");
    }

    @Scheduled(cron="0 * * * * *",zone="IST")
    //@Scheduled(cron="0 0 23 * * *",zone="IST")
    public void sendDailyExpenses(){
        log.info("Job started: sendDailyExpenses()");
        List<ProfileEntity> profiles =  profileRepository.findAll();
        for(ProfileEntity profile:profiles){
          List<ExpenseDto> todayExpenses=  expenseService.getExpenseForUserOnDate(profile.getId(), LocalDate.now());
          if(!todayExpenses.isEmpty()){
              StringBuilder table = new StringBuilder();

              table.append("<table style='border-collapse:collapse;width:100%;font-family:Arial,sans-serif;'>");

              table.append("<tr style='background-color:#f2f2f2;'>")
                      .append("<th style='border:1px solid #ddd;padding:8px;'>Sr.No</th>")
                      .append("<th style='border:1px solid #ddd;padding:8px;'>Name</th>")
                      .append("<th style='border:1px solid #ddd;padding:8px;'>Amount</th>")
                      .append("<th style='border:1px solid #ddd;padding:8px;'>Category</th>")
                      .append("<th style='border:1px solid #ddd;padding:8px;'>Date</th>")
                      .append("</tr>");

              int i = 1;

              for (ExpenseDto expenseDto : todayExpenses) {

                  table.append("<tr>");

                  table.append("<td style='border:1px solid #ddd;padding:8px;text-align:center;'>")
                          .append(i++)
                          .append("</td>");

                  table.append("<td style='border:1px solid #ddd;padding:8px;'>")
                          .append(expenseDto.getName())
                          .append("</td>");

                  table.append("<td style='border:1px solid #ddd;padding:8px;'>₹")
                          .append(expenseDto.getAmount())
                          .append("</td>");

                  table.append("<td style='border:1px solid #ddd;padding:8px;'>")
                          .append(expenseDto.getCategoryName() != null
                                  ? expenseDto.getCategoryName()
                                  : "N/A")
                          .append("</td>");

                  table.append("<td style='border:1px solid #ddd;padding:8px;'>")
                          .append(expenseDto.getDate())
                          .append("</td>");

                  table.append("</tr>");
              }

              table.append("</table>");
              String body="Hi, " + profile.getFullName() + "<br><br>Here is summary of your expenses for today:<br><br>"+table
                      +"<br/><br/>Best regards,<br>Money Manager Team";
              emailService.sendSimpleMail(profile.getEmail(),"Summary of today expenses",body);
          }
          log.info("Job completed: sendDailyExpenses()");
        }
    }
}
