package com.sagardev.reactivespring.controller;

import com.sagardev.reactivespring.config.MyAuthenticationManager;
import com.sagardev.reactivespring.config.MyUserDetailsService;
import com.sagardev.reactivespring.exception.ValidationException;
import com.sagardev.reactivespring.model.JsonResponse;
import com.sagardev.reactivespring.model.JwtResponse;
import com.sagardev.reactivespring.model.User;
import com.sagardev.reactivespring.service.UserService;
import com.sagardev.reactivespring.util.JwtUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.List;

@RestController
public class UserController {

    private final UserService userService;

    private final Logger logger = LoggerFactory.getLogger(getClass().getName());

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    private MyUserDetailsService userDetailsService;

    @Autowired
    private MyAuthenticationManager authenticationManager;


    @Autowired
    private JwtUtil jwtUtil;


    @Autowired
    public UserController(UserService userService){
        this.userService = userService;
    }

    @GetMapping("users/{userId}")
    Mono<User> getUserById(@PathVariable int userId){
        if (userId<0)
            throw new ValidationException("userId cant be negative");
        return userService.getUserById(userId);
    }

    @PreAuthorize("hasAnyAuthority('ADMIN','MODERATOR')")
    @GetMapping("/users")
    Flux<User> getUserById(){
        return userService.getUsers();
    }

    //API for admin
    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("/users/address")
    Mono<String> getUserAddress(){
        return userService.getUserAddress((Mono<Object>)getCurrentUser());
    }

    @RequestMapping(value = "/login",method = RequestMethod.POST)
    public Mono<ResponseEntity<JwtResponse>> createAuthenticationToken(@RequestBody User user) {

        return userDetailsService.findByUserName(user.getUserName()).map((userDetails) -> {

            if (bCryptPasswordEncoder.matches(user.getPassword(),userDetails.getPassword())) {
                String token = jwtUtil.generateToken(userDetails);
                return ResponseEntity.ok(new JwtResponse(token,"200 Ok","Login successful",
                        userDetails.getAuthorities()));
            } else {
                return ResponseEntity.badRequest().body(new JwtResponse("409 Conflict","Incorrect Password"));
            }
        }).defaultIfEmpty(ResponseEntity.status(HttpStatus.UNAUTHORIZED).build());




    }

    @RequestMapping(value = "/register",method = RequestMethod.POST)
    public Mono<JsonResponse> register(@RequestBody User user)
    {
        return userService.getUserByUserName(user.getUserName())
                .defaultIfEmpty(user)
                .flatMap(user1 -> {
                    user1.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
                    userService.save(user1);
                            logger.info(user1.toString());
                    return Mono.just(new JsonResponse("200 Ok","Registered Successfully"));
                    }
                    );

    }

    @GetMapping("/posts")
    Mono<List<String>> getPosts(){
        return Mono.just(Arrays.asList("apple","ball"));
    }



    @GetMapping("/current-user")
    public Mono<Object> getCurrentUser() {
     return ReactiveSecurityContextHolder.getContext()
             .map(SecurityContext::getAuthentication)
             .map(Authentication::getPrincipal);

    }



}
