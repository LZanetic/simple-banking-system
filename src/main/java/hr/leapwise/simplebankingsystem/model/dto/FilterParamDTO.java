package hr.leapwise.simplebankingsystem.model.dto;

import lombok.Data;
import java.time.LocalDateTime;
import java.math.BigDecimal;

@Data
public class FilterParamDTO {

    String currencyId;
    String message;
    Long senderAccountId;
    Long receiverAccountId;
    LocalDateTime dateBefore;
    LocalDateTime dateAfter;
    BigDecimal amountGreaterThan;
    BigDecimal amountLessThan;
}

