package hr.leapwise.simplebankingsystem.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CustomerDTO {
    private Long customerId;
    private String name;
    private String address;
    private String email;
    private String phoneNumber;
}
