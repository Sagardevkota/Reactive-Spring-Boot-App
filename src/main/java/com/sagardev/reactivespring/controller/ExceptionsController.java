package com.sagardev.reactivespring.controller;



import com.sagardev.reactivespring.exception.NotFoundException;
import com.sagardev.reactivespring.exception.ValidationException;
import com.sagardev.reactivespring.model.JsonResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.NoSuchElementException;


@RestControllerAdvice
public class ExceptionsController {


    @ExceptionHandler(value = ValidationException.class)
    public ResponseEntity<?> validateRequest(ValidationException exception) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new JsonResponse("400 BAD REQUEST",exception.getMessage()));
    }


    @ExceptionHandler(value = NoSuchElementException.class)
    public ResponseEntity<?> handleException(NoSuchElementException exception) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new JsonResponse("404","Not found"));
    }

    @ExceptionHandler(value = MethodArgumentTypeMismatchException.class)
    public ResponseEntity<?> handleException(MethodArgumentTypeMismatchException exception) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new JsonResponse("400 BAD REQUEST",exception.getMessage()));
    }

    @ExceptionHandler(value = NotFoundException.class)
    public ResponseEntity<?> handleException(NotFoundException exception) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new JsonResponse("404 NOT FOUND",exception.getMessage()));
    }

    @ExceptionHandler(value = BadCredentialsException.class)
    public ResponseEntity<?> handleException(BadCredentialsException exception) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new JsonResponse("403 BAD CREDENTIALS",exception.getMessage()));
    }


    //global exception handler
    @ExceptionHandler(value = Exception.class)
    public ResponseEntity<?> handleException(Exception exception) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new JsonResponse(String.valueOf(HttpStatus.UNAUTHORIZED),exception.getMessage()));
    }

    @ExceptionHandler(value = NullPointerException.class)
    public ResponseEntity<?> handleException(NullPointerException exception) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new JsonResponse(String.valueOf(HttpStatus.INTERNAL_SERVER_ERROR),exception.getMessage()));
    }

}
