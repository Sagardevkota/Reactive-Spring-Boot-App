package com.sagardev.reactivespring.config;

import com.sagardev.reactivespring.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UserDetailsRepositoryReactiveAuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;


import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.server.SecurityWebFilterChain;


@Configuration
@EnableWebFluxSecurity
@EnableReactiveMethodSecurity
public class AppSecurityConfig  {


    @Autowired
    private MyUserDetailsService myUserDetailsService;

    @Autowired
    private JwtUtil jwtUtil;



    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {

//        http.addFilterBefore(new JwtRequestFilter(jwtUtil,myUserDetailsService),SecurityWebFiltersOrder.AUTHORIZATION);
//
//        return http
//                .csrf()
//                .disable()
//                .authenticationManager(reactiveAuthenticationManager(myUserDetailsService,getBcryptEncoder()))
//                .authorizeExchange()
//                .pathMatchers("/login","/register")
//                .permitAll()
//                .and()
//                .build();

        // Disable login form
        http.httpBasic().disable()
                .formLogin().disable()
                .csrf().disable()
                .logout().disable();
        // Add a filter with Authorization on header   http.addFilterAt(webFilter(),SecurityWebFiltersOrder.AUTHORIZATION)

        http.addFilterBefore(new JwtRequestFilter(jwtUtil,myUserDetailsService),SecurityWebFiltersOrder.AUTHORIZATION);

        return http.build();


    }



    @Bean
    public BCryptPasswordEncoder getBcryptEncoder(){
        return new BCryptPasswordEncoder();
    }


}
