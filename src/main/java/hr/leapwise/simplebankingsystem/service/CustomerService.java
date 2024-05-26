package hr.leapwise.simplebankingsystem.service;

import hr.leapwise.simplebankingsystem.model.dto.CustomerDTO;

public interface CustomerService {
    CustomerDTO getCustomerById(Long customerId);
}
