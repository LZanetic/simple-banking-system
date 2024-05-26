package hr.leapwise.simplebankingsystem.mapper;

import hr.leapwise.simplebankingsystem.model.dto.TransactionDTO;
import hr.leapwise.simplebankingsystem.model.entity.Transaction;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface TransactionMapper {

    TransactionMapper INSTANCE = Mappers.getMapper(TransactionMapper.class);

    Transaction mapToTransactionEntity(TransactionDTO transactionDTO);

    TransactionDTO mapToTransactionDTO(Transaction transaction);
}
