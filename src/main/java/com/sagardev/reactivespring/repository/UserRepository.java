package com.sagardev.reactivespring.repository;

import com.sagardev.reactivespring.model.User;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;


@Repository
public interface UserRepository extends ReactiveMongoRepository<User,Integer> {

    Mono<User> findByUserName(String userName);

}
