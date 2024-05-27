package hr.leapwise.simplebankingsystem.event;

import hr.leapwise.simplebankingsystem.service.impl.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class TransactionEventListener {

    @Autowired
    private EmailService emailService;

    @EventListener
    public void handleTransactionProcessedEvent(TransactionProcessedEvent event) {
        emailService.sendTransactionEmail(event.getEmailDTO());
    }
}
