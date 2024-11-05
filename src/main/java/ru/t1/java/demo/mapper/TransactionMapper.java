package ru.t1.java.demo.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MapperConfig;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;
import ru.t1.java.demo.entity.AccountEntity;
import ru.t1.java.demo.entity.TransactionEntity;
import ru.t1.java.demo.model.TransactionRequest;
import ru.t1.java.demo.model.TransactionResponse;

@Mapper(unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface TransactionMapper {

    TransactionMapper TRANSACTION_MAPPER = Mappers.getMapper(TransactionMapper.class);


    @Mapping(target = "accountId", source = "entity.account.id")
    @Mapping(target = "amount", source = "amount")
    @Mapping(target = "time", source = "time")
    @Mapping(target = "id", source = "id")
    TransactionResponse toResponse(TransactionEntity entity);


    @Mapping(target = "account", source = "account")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "amount", source = "request.amount")
    @Mapping(target = "time", ignore = true)
    TransactionEntity toEntity(TransactionRequest request, AccountEntity account);


}
