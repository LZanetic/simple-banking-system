package hr.leapwise.simplebankingsystem.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class CustomerTransactionsDTO {

    private CustomerDTO customer;

    private List<TransactionDTO> transactions;
}
