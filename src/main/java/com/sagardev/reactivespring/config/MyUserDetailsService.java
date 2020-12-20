package com.sagardev.reactivespring.config;

import com.sagardev.reactivespring.repository.UserRepository;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;


@Component
public class MyUserDetailsService implements ReactiveUserDetailsService {

    private final UserRepository userRepository;

    public MyUserDetailsService(UserRepository userRepository){
        this.userRepository = userRepository;
    }

    @Override
    public Mono<UserDetails> findByUsername(String userName) {
        return userRepository.findByUserName(userName).map(MyUserDetails::new);
    }
}
