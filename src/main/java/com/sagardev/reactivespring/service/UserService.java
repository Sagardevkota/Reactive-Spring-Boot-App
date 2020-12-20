package com.sagardev.reactivespring.service;

import com.sagardev.reactivespring.exception.NotFoundException;
import com.sagardev.reactivespring.model.User;
import com.sagardev.reactivespring.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;



@Service
public class UserService {

    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository){
        this.userRepository = userRepository;
    }

    public Mono<User> getUserById(int userId) {
        return userRepository.findById(userId).switchIfEmpty(Mono.error(new NotFoundException("User Not Found")));
    }

    public Flux<User> getUsers() {
        return userRepository.findAll().switchIfEmpty(Mono.error(new NotFoundException("Users Not found")));
    }


    public void save(User user) {
        userRepository.save(user).subscribe();
    }

    public Mono<String> getUserAddress(String userName) {
        return userRepository.findByUserName(userName)
                .map(User::getAddress);

    }


    public Mono<User> getUserByUserName(String userName) {
        return userRepository.findByUserName(userName);
    }
}
