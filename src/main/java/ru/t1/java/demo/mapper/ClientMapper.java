package ru.t1.java.demo.mapper;

import org.springframework.stereotype.Component;
import ru.t1.java.demo.model.ClientDto;
import ru.t1.java.demo.entity.ClientEntity;

@Component
public class ClientMapper {

    public static ClientEntity toEntity(ClientDto dto) {
        if (dto.getMiddleName() == null) {
//            throw new NullPointerException();
        }
        return ClientEntity.builder()
                .firstName(dto.getFirstName())
                .lastName(dto.getLastName())
                .middleName(dto.getMiddleName())
                .build();
    }

    public static ClientDto toDto(ClientEntity entity) {
        return ClientDto.builder()
                .id(entity.getId())
                .firstName(entity.getFirstName())
                .lastName(entity.getLastName())
                .middleName(entity.getMiddleName())
                .build();
    }

}
