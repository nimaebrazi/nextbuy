package com.nextbuy.passport.support.fixtures;

import com.nextbuy.passport.dto.LoginRequestDto;
import com.nextbuy.passport.support.utils.Fakers;
import org.instancio.Instancio;
import org.instancio.Select;

import static com.nextbuy.passport.support.utils.Fakers.faker;

public final class LoginRequests {
    private LoginRequests() {
    }

    public static LoginRequestDto random() {
        return Instancio.of(LoginRequestDto.class)
                .supply(Select.field(LoginRequestDto::email), () -> faker().internet().emailAddress())
                .supply(Select.field(LoginRequestDto::password), Fakers::randomPassword)
                .create();
    }

    public static LoginRequestDto withCredentials(String email, String password) {
        return new LoginRequestDto(email, password);
    }
}
