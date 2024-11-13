package ru.t1.java.demo.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;
import ru.t1.java.demo.entity.AccountEntity;
import ru.t1.java.demo.entity.ClientEntity;
import ru.t1.java.demo.model.AccountRequest;
import ru.t1.java.demo.model.AccountResponse;

@Mapper(unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface AccountMapper {
    AccountMapper ACCOUNT_MAPPER = Mappers.getMapper(AccountMapper.class);

    @Mapping(target = "clientId", source = "entity.client.id")
    @Mapping(target = "id", source = "id")
    @Mapping(target = "accountId", source = "accountId")
    @Mapping(target = "accountType", source = "accountType")
    @Mapping(target = "status", source = "status")
    @Mapping(target = "balance", source = "balance")
    @Mapping(target = "frozenAmount", source = "frozenAmount")
    AccountResponse toResponse(AccountEntity entity);

    @Mapping(target = "client", source = "client")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "accountId", ignore = true)
    @Mapping(target = "accountType", source = "request.accountType")
    @Mapping(target = "status", source = "request.status")
    @Mapping(target = "balance", source = "request.balance")
    @Mapping(target = "frozenAmount", source = "request.frozenAmount")
    @Mapping(target = "transactions", ignore = true)
    AccountEntity toEntity(AccountRequest request, ClientEntity client);


}
