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
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.security.Principal;
import java.util.Collections;
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

    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("/users")
    Flux<User> getUserById(){

        return userService.getUsers();
    }

    private List<String> getRolesofLoggedInUser(Principal principal){
      return getLoggedInUser(principal)
              .single()
                .map(user -> Collections.singletonList(user.getRole())).block();
    }

    private Mono<User> getLoggedInUser(Principal principal){

        return userService.getUserByUserName(principal.getName());
    }


    //API for admin
    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("/users/address/{userName}")
    Mono<String> getUserAddress(@PathVariable String userName,Principal principal){
        if (!principal.getName().equals(userName))
            return Mono.error(new ValidationException("UNAUTHORIZED USER"));
        return userService.getUserAddress(userName);

    }

    @RequestMapping(value = "login",method = RequestMethod.POST)
    public Mono<JwtResponse> createAuthenticationToken(@RequestBody User user) {

        Mono<UserDetails> userDetailsMono = userDetailsService.findByUsername(user.getUserName());

        Mono<Authentication> authenticationMono = authenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken(user.getUserName(), user.getPassword()));

//
//       return authenticationMono.zipWith(userDetailsMono)
//                .flatMap(objects -> {
//                    objects.mapT1(authentication -> {
//                        if (authentication.isAuthenticated()){
//                            objects.mapT2(userDetails -> {
//
//                                JwtResponse jwtResponse = JwtResponse.builder()
//                                        .jwt(jwtUtil.generateToken(userDetails))
//                                        .status("200 OK")
//                                        .message("Login Successful")
//                                        .role(userDetails.getAuthorities().toString())
//                                        .build();
//                                logger.info(jwtResponse.toString());
//                                return Mono.just(jwtResponse);
//                            });
//                        }
//                        return Mono.just(new JwtResponse("403","Error"));
//                    }
//
//                    );
//
//                    return Mono.just(new JwtResponse("403","Error"));
//                });


       return Mono.zip(authenticationMono, userDetailsMono).flatMap(data->{

           Authentication authentication = data.getT1();

            if (authentication.isAuthenticated())
            {
                UserDetails userDetails = data.getT2();
                JwtResponse jwtResponse = JwtResponse.builder()
                        .jwt(jwtUtil.generateToken(userDetails))
                        .status("200 Ok")
                        .role(authentication.getAuthorities().toString())
                        .message("Login Successful")
                        .build();

                return Mono.just(jwtResponse);

            }
            else
                return Mono.just(new JwtResponse("403 Error","Login unsuccessful"));


        });





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

}
