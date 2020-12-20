package com.sagardev.reactivespring.config;

import com.sagardev.reactivespring.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class MyAuthenticationManager implements ReactiveAuthenticationManager {

    private Logger logger = LoggerFactory.getLogger(this.getClass().getName());

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder encoder;

    @Override
    public Mono<Authentication> authenticate(Authentication authentication) {
        String username = authentication.getPrincipal() + "";
        String password = authentication.getCredentials() + "";


      return userRepository.findByUserName(username)
               .flatMap(user -> {

                   logger.info(user.toString());

                   if (!encoder.matches(password, user.getPassword())) {
                       return Mono.error(new BadCredentialsException("Incorrect Password"));
                   }
                   if (!user.isActive()) {
                       return Mono.error(new DisabledException("User is disabled"));
                   }
                   List<String> userRights = Collections.singletonList(user.getRole());

                   return Mono.just(new UsernamePasswordAuthenticationToken(username, null, userRights.stream()
                           .map(SimpleGrantedAuthority::new)
                           .collect(Collectors.toList())));
               });



    }
}
