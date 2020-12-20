package com.sagardev.reactivespring.config;


import com.sagardev.reactivespring.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import org.springframework.web.util.pattern.PathPattern;
import org.springframework.web.util.pattern.PathPatternParser;
import org.w3c.dom.stylesheets.LinkStyle;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.List;


public class JwtRequestFilter implements WebFilter {

    private static final String HEADER_PREFIX = "Bearer " ;

   private final MyUserDetailsService myUserDetailService;
   private final List<PathPattern> pathPatternList;

   private final JwtUtil jwtUtil;

   public JwtRequestFilter(JwtUtil jwtUtil, MyUserDetailsService myUserDetailService){
       this.jwtUtil = jwtUtil;
       this.myUserDetailService = myUserDetailService;
       PathPattern pathPattern1 = new PathPatternParser().parse("/login");
       PathPattern pathPattern2 = new PathPatternParser().parse("/register");
       pathPatternList = Arrays.asList(pathPattern1,pathPattern2);
   }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
       //if the request path matches the list of patterns return
       if (pathPatternList.stream()
               .anyMatch(pathPattern ->
                       pathPattern.matches(exchange.getRequest().getPath().pathWithinApplication())))
        return chain.filter(exchange);


        String jwt = resolveToken(exchange.getRequest());
        String userName = jwtUtil.getUsernameFromToken(jwt);
        UserDetails userDetails = myUserDetailService.findByUsername(userName).block();
        if (jwtUtil.validateToken(jwt,userDetails).equalsIgnoreCase("ok")){
            UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new
                    UsernamePasswordAuthenticationToken(userDetails,null,userDetails.getAuthorities());
           return chain.filter(exchange)
                    .subscriberContext(ReactiveSecurityContextHolder.withAuthentication(usernamePasswordAuthenticationToken));
        }


        return chain.filter(exchange);

    }



    private String resolveToken(ServerHttpRequest request) {
        String bearerToken = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(HEADER_PREFIX)) {
            return bearerToken.substring(7);
        }
        return null;
    }
}
