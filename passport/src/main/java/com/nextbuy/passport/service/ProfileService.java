package com.nextbuy.passport.service;


import com.nextbuy.passport.dto.UserProfileDto;
import com.nextbuy.passport.exceptions.AuthExceptions;
import com.nextbuy.passport.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProfileService {

    private final UserRepository userRepository;

    public UserProfileDto execute(String email) {
        return userRepository.findByEmail(email)
                .map(UserProfileDto::from)
                .orElseThrow(AuthExceptions::invalidAccessToken);
    }
}
