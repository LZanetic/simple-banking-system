package hr.leapwise.simplebankingsystem.model.dto;

import hr.leapwise.simplebankingsystem.model.entity.Account;
import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Data
public class CustomerDTO {
    private Long customerId;
    private String name;
    private String address;
    private String email;
    private String phoneNumber;
}
