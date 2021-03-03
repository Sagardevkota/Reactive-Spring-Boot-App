package com.sagardev.reactivespring.config;

import com.sagardev.reactivespring.repository.UserRepository;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;


@Component
public class MyUserDetailsService  {

    private final UserRepository userRepository;

    public MyUserDetailsService(UserRepository userRepository){
        this.userRepository = userRepository;
    }

    public Mono<MyUserDetails> findByUserName(String userName) {
        return userRepository.findByUserName(userName).map(MyUserDetails::new);
    }


}
