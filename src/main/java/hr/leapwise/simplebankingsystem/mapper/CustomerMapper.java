package hr.leapwise.simplebankingsystem.mapper;

import hr.leapwise.simplebankingsystem.model.dto.CustomerDTO;
import hr.leapwise.simplebankingsystem.model.entity.Customer;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface CustomerMapper {
    CustomerMapper INSTANCE = Mappers.getMapper(CustomerMapper.class);

    CustomerDTO mapToCustomerDTO(Customer customer);
}
