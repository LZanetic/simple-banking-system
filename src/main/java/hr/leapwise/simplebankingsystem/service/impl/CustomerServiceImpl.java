package hr.leapwise.simplebankingsystem.service.impl;

import hr.leapwise.simplebankingsystem.dao.CustomerRepository;
import hr.leapwise.simplebankingsystem.exception.ResourceNotFoundException;
import hr.leapwise.simplebankingsystem.mapper.CustomerMapper;
import hr.leapwise.simplebankingsystem.model.dto.CustomerDTO;
import hr.leapwise.simplebankingsystem.model.entity.Customer;
import hr.leapwise.simplebankingsystem.service.CustomerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Slf4j
public class CustomerServiceImpl implements CustomerService {

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private CustomerMapper customerMapper;

    @Override
    public CustomerDTO getCustomerById(Long customerId) {
        Optional<Customer> customer = customerRepository.findById(customerId);
        if (customer.isEmpty()) throw new ResourceNotFoundException("Customer not found");

        return customer.map(value -> customerMapper.mapToCustomerDTO(value)).orElse(null);
    }
}
