package hr.leapwise.simplebankingsystem.exception;

public class MailSendException extends RuntimeException {

    public MailSendException(String message) {
        super(message);
    }
}
