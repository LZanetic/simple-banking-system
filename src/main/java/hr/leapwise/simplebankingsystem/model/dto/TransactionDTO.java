package hr.leapwise.simplebankingsystem.model.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TransactionDTO {

    @NotNull(message = "Sender account cannot be null")
    private Long senderAccountId;

    @NotNull(message = "Receiver account cannot be null")
    private Long receiverAccountId;

    @NotNull(message = "Amount cannot be null")
    @DecimalMin(value = "0.0", inclusive = false, message = "Amount cannot be negative or 0.0")
    private BigDecimal amount;

    @NotNull(message = "Currency cannot be null")
    @NotBlank(message = "Currency cannot be blank")
    private String currencyId;

    private String message;

    private LocalDateTime timestamp;
}
