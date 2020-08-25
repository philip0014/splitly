package com.example.splitly.repository;

import com.example.splitly.model.FriendRequestStatus;
import com.example.splitly.model.entity.FriendRequest;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface FriendRequestRepository extends MongoRepository<FriendRequest, String> {

    Optional<FriendRequest> findByFromUserIdAndToUserIdAndStatus(String fromUserId, String toUserId,
        FriendRequestStatus status);

    List<FriendRequest> findAllByToUserIdOrFromUserId(String toUserId, String fromUserId);

}
