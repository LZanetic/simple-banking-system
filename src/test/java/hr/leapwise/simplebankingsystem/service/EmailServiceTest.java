package hr.leapwise.simplebankingsystem.service;

import hr.leapwise.simplebankingsystem.model.dto.EmailDTO;
import hr.leapwise.simplebankingsystem.service.impl.EmailService;

import org.junit.Test;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;

public class EmailServiceTest {


    // Email is sent when status is 'success' and action is 'receive'
    @Test
    public void test_email_sent_on_success_and_receive() {
        JavaMailSender mailSender = mock(JavaMailSender.class);
        EmailService emailService = new EmailService();
        ReflectionTestUtils.setField(emailService, "mailSender", mailSender);
        SimpleMailMessage capturedMessage = new SimpleMailMessage();
        doAnswer(invocation -> {
            SimpleMailMessage message = invocation.getArgument(0);
            capturedMessage.setTo(message.getTo());
            capturedMessage.setSubject(message.getSubject());
            capturedMessage.setText(message.getText());
            return null;
        }).when(mailSender).send(any(SimpleMailMessage.class));

        EmailDTO emailDTO = EmailDTO.builder()
                .to("user@example.com")
                .transactionId(123L)
                .balance(new BigDecimal("100.00"))
                .oldBalance(new BigDecimal("50.00"))
                .newBalance(new BigDecimal("150.00"))
                .status("success")
                .action("receive")
                .build();

        emailService.sendTransactionEmail(emailDTO);

        assertNotNull(capturedMessage.getTo());
        assertEquals("Transaction Confirmation", capturedMessage.getSubject());
        assertTrue(capturedMessage.getText().contains("has been added to your account"));
    }

    // EmailDTO with null values for any of the fields
    @Test
    public void test_emaildto_with_null_values() {
        JavaMailSender mailSender = mock(JavaMailSender.class);
        EmailService emailService = new EmailService();
        ReflectionTestUtils.setField(emailService, "mailSender", mailSender);

        EmailDTO emailDTO = EmailDTO.builder().build();

        assertThrows(NullPointerException.class, () -> {
            emailService.sendTransactionEmail(emailDTO);
        });
    }

}