package com.sagardev.reactivespring.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.MongoId;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;


@Document(collection = "users")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @MongoId
    @Id
    private String id;

    @NotBlank(message = "UserName cant be empty")
    @Min(value = 5,message = "userName should be minimum of 5 characters")
    private String userName;

    @NotBlank(message = "Password cant be empty")
    @Min(value = 5,message = "Password should be at least 5 characters")

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;
    private int age;
    private String address;
    private String role;
    private boolean active;

}
