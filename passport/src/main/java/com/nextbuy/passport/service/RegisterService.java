package com.nextbuy.passport.service;


import com.nextbuy.passport.controller.v1.dto.RegisterUserDto;
import com.nextbuy.passport.domain.Role;
import com.nextbuy.passport.domain.User;
import com.nextbuy.passport.dto.AuthTokenResponseDto;
import com.nextbuy.passport.dto.LoginRequestDto;
import com.nextbuy.passport.dto.RefreshTokenContextDto;
import com.nextbuy.passport.exceptions.AuthExceptions;
import com.nextbuy.passport.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class RegisterService {

    private final RoleService roleService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final LoginService loginService;


    @Transactional(isolation = Isolation.SERIALIZABLE)
    public AuthTokenResponseDto execute(RegisterUserDto registerUserDto, RefreshTokenContextDto context) {

        boolean userExists = userRepository.existsByEmail(registerUserDto.email());
        if (userExists) {
            throw AuthExceptions.emailAlreadyExist();
        }

        Role role = roleService.getByName("ROLE_USER");
        if (role == null) {
            throw AuthExceptions.roleNotFound();
        }

        Set<Role> roles = new HashSet<>();
        roles.add(role);

        User user = User.builder()
                .email(registerUserDto.email())
                .password(passwordEncoder.encode(registerUserDto.password()))
                .roles(roles)
                .build();

        user = userRepository.save(user);

       return loginService.execute(new LoginRequestDto(user.getEmail(), registerUserDto.password()), context);
    }
}
