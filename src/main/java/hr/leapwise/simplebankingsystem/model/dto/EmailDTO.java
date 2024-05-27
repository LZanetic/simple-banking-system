package hr.leapwise.simplebankingsystem.model.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class EmailDTO {
    String to;
    Long transactionId;
    BigDecimal balance;
    BigDecimal oldBalance;
    BigDecimal newBalance;
    String status;
    String action;
}
