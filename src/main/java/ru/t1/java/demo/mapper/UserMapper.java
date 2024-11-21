package ru.t1.java.demo.mapper;

import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import org.springframework.security.crypto.password.PasswordEncoder;
import ru.t1.java.demo.entity.UserEntity;
import ru.t1.java.demo.model.reqest.UserRequest;
import ru.t1.java.demo.model.response.UserResponse;

@Mapper
public interface UserMapper {
    UserMapper USER_MAPPER = Mappers.getMapper(UserMapper.class);


    @Mapping(target = "id", source = "id")
    @Mapping(target = "login", source = "login")
    UserResponse toResponse(UserEntity entity);


    @Mapping(target = "id", ignore = true)
    @Mapping(target = "login", source = "login")
    @Mapping(target = "password", expression = "java(encoder.encode(request.password()))")
    UserEntity toEntity(UserRequest request, @Context PasswordEncoder encoder);


}
