package com.sagardev.reactivespring.config;

import com.sagardev.reactivespring.model.User;
import com.sagardev.reactivespring.repository.UserRepository;
import com.sagardev.reactivespring.util.JwtUtil;
import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Slf4j
@Service
public class MyAuthenticationManager implements ReactiveAuthenticationManager {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private MyUserDetailsService userDetailsService;

    @Override
    public Mono<Authentication> authenticate(Authentication authentication) {
        String authToken = authentication.getCredentials().toString();
        Mono<MyUserDetails> userDetailsMono = userDetailsService
                .findByUserName(jwtUtil.getUsernameFromToken(authToken));

       return userDetailsMono.flatMap(userDetails -> {
            if (jwtUtil.validateToken(authToken, userDetails).equals("ok"))
            {
                Claims claims = jwtUtil.getAllClaimsFromToken(authToken);

                log.info(claims.toString());

                return Mono.just(new UsernamePasswordAuthenticationToken(claims.getSubject(),
                        authentication.getCredentials(),userDetails.getAuthorities()));
            }

            return Mono.empty();

        });










    }
}
