package hr.leapwise.simplebankingsystem.service;
import hr.leapwise.simplebankingsystem.dao.CustomerRepository;
import hr.leapwise.simplebankingsystem.mapper.CustomerMapper;
import hr.leapwise.simplebankingsystem.model.dto.CustomerDTO;
import hr.leapwise.simplebankingsystem.model.entity.Customer;
import hr.leapwise.simplebankingsystem.service.impl.CustomerServiceImpl;

import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static org.junit.Assert.*;

public class CustomerServiceTest {

    @Test
    public void test_customer_retrieval_success() {
        // Arrange
        Long customerId = 1L;
        Customer customer = new Customer(customerId, "John Doe", "1234 Elm Street", "john@example.com", "1234567890", null);
        CustomerDTO expectedDTO = new CustomerDTO();
        expectedDTO.setCustomerId(customerId);
        expectedDTO.setName("John Doe");
        expectedDTO.setAddress("1234 Elm Street");
        expectedDTO.setEmail("john@example.com");
        expectedDTO.setPhoneNumber("1234567890");

        CustomerRepository mockRepository = Mockito.mock(CustomerRepository.class);
        CustomerMapper mockMapper = Mockito.mock(CustomerMapper.class);
        Mockito.when(mockRepository.findById(customerId)).thenReturn(Optional.of(customer));
        Mockito.when(mockMapper.mapToCustomerDTO(customer)).thenReturn(expectedDTO);

        CustomerServiceImpl service = new CustomerServiceImpl();
        ReflectionTestUtils.setField(service, "customerRepository", mockRepository);
        ReflectionTestUtils.setField(service, "customerMapper", mockMapper);

        // Act
        CustomerDTO result = service.getCustomerById(customerId);

        // Assert
        assertNotNull(result);
        assertEquals(expectedDTO, result);
    }

    // Customer ID provided is null
    @Test
    public void test_customer_id_null() {
        // Arrange
        Long customerId = null;
        CustomerRepository mockRepository = Mockito.mock(CustomerRepository.class);
        Mockito.when(mockRepository.findById(customerId)).thenThrow(new IllegalArgumentException("Customer ID cannot be null"));

        CustomerServiceImpl service = new CustomerServiceImpl();
        ReflectionTestUtils.setField(service, "customerRepository", mockRepository);

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> service.getCustomerById(customerId));
    }

}
