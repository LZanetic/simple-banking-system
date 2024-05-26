package hr.leapwise.simplebankingsystem.model.dto;

import com.opencsv.bean.CsvBindByName;
import lombok.Data;

@Data
public class TransactionCsvDTO {
    @CsvBindByName
    private Long senderAccountId;

    @CsvBindByName
    private Long receiverAccountId;

    @CsvBindByName
    private double amount;

    @CsvBindByName
    private String currencyId;

    @CsvBindByName
    private String message;

    @CsvBindByName
    private String timestamp;
}
