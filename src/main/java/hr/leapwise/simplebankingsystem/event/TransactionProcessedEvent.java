package hr.leapwise.simplebankingsystem.event;

import hr.leapwise.simplebankingsystem.model.dto.EmailDTO;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class TransactionProcessedEvent extends ApplicationEvent {
    private final EmailDTO emailDTO;

    public TransactionProcessedEvent(Object source, EmailDTO emailDTO) {
        super(source);
        this.emailDTO = emailDTO;
    }
}