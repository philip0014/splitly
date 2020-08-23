package com.example.splitly.repository;

import com.example.splitly.model.entity.UserClient;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.Collection;
import java.util.List;

public interface UserClientRepository extends MongoRepository<UserClient, String> {

    List<UserClient> findByIdIn(Collection<String> id);

    Page<UserClient> findByIdIn(Collection<String> id, Pageable pageable);

    @Query(value = "{'username': {$regex : ?0, $options: 'i'}, 'id': {$nin: ?1}}")
    List<UserClient> findByUsernameRegexAndIdNotIn(String username, Collection<String> id,
        Pageable pageable);

}
