package hr.leapwise.simplebankingsystem.model.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long transactionId;
    private double amount;
    private String currencyId;
    private String message;
    private LocalDateTime timestamp;

    @JoinColumn(name = "sender_account_id")
    private Long senderAccountId;

    @JoinColumn(name = "receiver_account_id")
    private Long receiverAccountId;
}

