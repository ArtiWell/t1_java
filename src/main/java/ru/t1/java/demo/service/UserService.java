package ru.t1.java.demo.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.t1.java.demo.config.security.UserCredentials;
import ru.t1.java.demo.config.security.UserDetailsImpl;
import ru.t1.java.demo.entity.UserEntity;
import ru.t1.java.demo.exception.ConflictException;
import ru.t1.java.demo.model.reqest.UserRequest;
import ru.t1.java.demo.model.response.UserResponse;
import ru.t1.java.demo.dao.persistence.UserRepository;

import static ru.t1.java.demo.mapper.UserMapper.USER_MAPPER;

@Service
@AllArgsConstructor
@Slf4j
public class UserService implements UserDetailsService {

    private final UserCredentials userCredentials;
    private final UserRepository userRepository;
    private final PasswordEncoder encoder;

    @Override
    public UserDetails loadUserByUsername(String login) throws UsernameNotFoundException {
        UserEntity user = userRepository.findByLogin(login).orElseGet(UserEntity::new);
        return new UserDetailsImpl(user);
    }


    public UserResponse registerUser(UserRequest request) {
        if (userRepository.findByLogin(request.login()).isPresent()) {
            throw new ConflictException();
        }
        UserEntity user = USER_MAPPER.toEntity(request,encoder);
        userRepository.save(user);

        return USER_MAPPER.toResponse(user);
    }
}
