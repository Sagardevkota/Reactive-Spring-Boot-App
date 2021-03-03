package com.sagardev.reactivespring.model;

import lombok.*;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;
import java.util.List;

@AllArgsConstructor
@Getter
@Setter
@Builder
@Data
public class JwtResponse {
    private  String jwt;
    private String status;
    private String message;
    private Collection<? extends GrantedAuthority> role;


    public JwtResponse(String status, String message) {
        this.status = status;
        this.message = message;
    }


}
