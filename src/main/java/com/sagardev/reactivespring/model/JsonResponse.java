package com.sagardev.reactivespring.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class JsonResponse {

    private String status;
    private String message;

}
