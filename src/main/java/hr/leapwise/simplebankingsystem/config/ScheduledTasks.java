package hr.leapwise.simplebankingsystem.config;

import hr.leapwise.simplebankingsystem.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@EnableScheduling
public class ScheduledTasks {

    @Autowired
    private AccountService accountService;

    @Scheduled(cron = "${turnover.cron.expression}")
    public void calculateTurnover() {
        accountService.updateTurnover();
    }
}
