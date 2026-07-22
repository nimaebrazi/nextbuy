package com.nextbuy.passport.service;

import com.nextbuy.passport.domain.User;
import com.nextbuy.passport.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class UserDetailService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Email Not Found"));

        Set<GrantedAuthority> authorities =
                user.getRoles().stream()
                        .flatMap(role -> {

                            // set roles
                            Set<SimpleGrantedAuthority> auths = new HashSet<>();
                            auths.add(new SimpleGrantedAuthority(role.getName()));

                            // permissions
                            role.getPermissions().forEach(p ->
                                    auths.add(new SimpleGrantedAuthority(p.getName()))
                            );

                            return auths.stream();

                        }).collect(Collectors.toSet());


        return new org.springframework.security.core.userdetails.User(
                user.getEmail(),
                user.getPassword(),
                user.isEnabled(),
                true, true, true,
                authorities
        );
    }
}
