package hr.leapwise.simplebankingsystem.service.impl;

import hr.leapwise.simplebankingsystem.model.dto.EmailDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    /**
     * Sends an email notification for a transaction confirmation.
     *
     * @param emailDTO the email data transfer object containing the recipient's email address, transaction details,
     *                 old and new balance, and status of the transaction.
     */
    public void sendTransactionEmail(EmailDTO emailDTO) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(emailDTO.getTo());
        message.setSubject("Transaction Confirmation");

        String statusMessage = emailDTO.getStatus().equalsIgnoreCase("success") ?
                "successfully" : "unsuccessfully";
        String action = emailDTO.getAction().equalsIgnoreCase("receive") ?
                "added to" : "taken from";

        String messageText = emailDTO.getStatus().equalsIgnoreCase("success") ?
                String.format("Hello!\n\n" +
                                "The transaction with ID: %d has been processed %s, " +
                                "and the balance: %.2f has been %s your account.\n\n" +
                                "Old balance: %.2f\n" +
                                "New balance: %.2f\n\n" +
                                "Regards,\n" +
                                "Your XYZ bank",
                        emailDTO.getTransactionId(), statusMessage, emailDTO.getBalance(), action,
                        emailDTO.getOldBalance(), emailDTO.getNewBalance()) :
                String.format("Hello!\n\n" +
                                "The transaction with ID: %d has been processed %s, " +
                                "and the balance: %.2f has not been %s your account.\n\n" +
                                "Old balance: %.2f\n" +
                                "New balance: %.2f\n\n" +
                                "Regards,\n" +
                                "Your XYZ bank",
                        emailDTO.getTransactionId(), statusMessage, emailDTO.getBalance(), action,
                        emailDTO.getOldBalance(), emailDTO.getNewBalance());
        log.info("Sending email: {}", messageText);

        message.setText(messageText);
        mailSender.send(message);
    }
}
