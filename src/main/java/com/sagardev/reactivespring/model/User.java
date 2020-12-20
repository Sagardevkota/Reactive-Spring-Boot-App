package com.sagardev.reactivespring.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.MongoId;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;


@Document(collection = "users")
@Data
public class User {

    @Transient
    public static final String db_sequence = "user_sequence";

    @MongoId
    @Id
    private String id;

    @NotBlank(message = "UserName cant be empty")
    @Min(value = 5,message = "userName should be minimum of 5 characters")
    private String userName;

    @NotBlank(message = "Password cant be empty")
    @Min(value = 5,message = "Password shoudl be at least 5 characters")

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;
    private int age;
    private String address;
    private String role;
    private boolean active;


}
