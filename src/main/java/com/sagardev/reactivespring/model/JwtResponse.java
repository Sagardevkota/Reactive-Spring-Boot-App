package com.sagardev.reactivespring.model;

import lombok.*;

@AllArgsConstructor
@Getter
@Setter
@Builder
@Data
public class JwtResponse {
    private  String jwt;
    private String status;
    private String message;
    private String role;


    public JwtResponse(String status, String message) {
        this.status = status;
        this.message = message;
    }

}
