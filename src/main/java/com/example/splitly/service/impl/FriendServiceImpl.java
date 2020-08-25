package com.example.splitly.service.impl;

import com.example.splitly.exception.BaseErrorException;
import com.example.splitly.helper.ConverterHelper;
import com.example.splitly.helper.LogHelper;
import com.example.splitly.helper.PageResponseHelper;
import com.example.splitly.model.FriendRequestStatus;
import com.example.splitly.model.Message;
import com.example.splitly.model.entity.FriendRequest;
import com.example.splitly.model.entity.User;
import com.example.splitly.model.entity.UserClient;
import com.example.splitly.model.response.FriendRequestResponse;
import com.example.splitly.model.response.PageResponse;
import com.example.splitly.repository.FriendRequestRepository;
import com.example.splitly.repository.UserClientRepository;
import com.example.splitly.repository.UserRepository;
import com.example.splitly.service.FriendService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import rx.Single;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class FriendServiceImpl implements FriendService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserClientRepository userClientRepository;

    @Autowired
    private FriendRequestRepository friendRequestRepository;

    @Override
    public Single<List<UserClient>> getFriends(User authUser) {
        return Single.<List<UserClient>>create(singleSubscriber -> {
            List<UserClient> userClients = userClientRepository.findByIdIn(authUser.getFriends());
            singleSubscriber.onSuccess(userClients);
        }).doOnError(t -> {
            LogHelper.error(Message.FRIEND_SERVICE_ERROR, "getFriends", authUser.getId(), t);
        });
    }

    @Override
    public Single<List<FriendRequestResponse>> getFriendRequests(User authUser) {
        return Single.<List<FriendRequestResponse>>create(singleSubscriber -> {
            List<FriendRequest> friendRequests = friendRequestRepository
                .findAllByToUserIdOrFromUserId(authUser.getId(), authUser.getId());
            List<FriendRequest> pendingFriendRequests = friendRequests.stream()
                .filter(friendRequest -> friendRequest.getStatus() == FriendRequestStatus.PENDING)
                .collect(Collectors.toList());

            Map<String, UserClient> mapOfUserClient = new HashMap<>();
            Set<String> userIds = pendingFriendRequests.stream().map(FriendRequest::getFromUserId)
                .collect(Collectors.toSet());
            userIds.addAll(pendingFriendRequests.stream().map(FriendRequest::getToUserId)
                .collect(Collectors.toSet()));
            List<UserClient> userClients = userClientRepository.findByIdIn(userIds);
            userClients.forEach(userClient -> mapOfUserClient.put(userClient.getId(), userClient));

            List<FriendRequestResponse> requestResponses =
                pendingFriendRequests.stream().map(friendRequest -> {
                    UserClient fromUser = mapOfUserClient.get(friendRequest.getFromUserId());
                    UserClient toUser = mapOfUserClient.get(friendRequest.getToUserId());
                    return ConverterHelper
                        .convertFromFriendRequestToFriendRequestResponse(friendRequest, fromUser,
                            toUser);
                }).collect(Collectors.toList());
            singleSubscriber.onSuccess(requestResponses);
        }).doOnError(t -> {
            LogHelper.error(Message.FRIEND_SERVICE_ERROR, "getFriendRequests", authUser.getId(), t);
        });
    }

    @Override
    public Single<Boolean> request(User authUser, String toUserId) {
        return Single.<Boolean>create(singleSubscriber -> {
            validateSameUser(authUser, toUserId);
            validateFriendAlready(authUser, toUserId);
            validateFriendRequestExists(authUser.getId(), toUserId);
            validateUserExists(toUserId);

            FriendRequest friendRequest = FriendRequest.builder()
                .fromUserId(authUser.getId())
                .toUserId(toUserId)
                .build();

            friendRequestRepository.save(friendRequest);
            singleSubscriber.onSuccess(true);
        }).doOnError(t -> {
            LogHelper.error(Message.FRIEND_SERVICE_ERROR, "request", toUserId, t);
        });
    }

    @Override
    public Single<Boolean> accept(User authUser, String fromUserId) {
        return Single.<Boolean>create(singleSubscriber -> {
            FriendRequest friendRequest =
                validateFriendRequestNotExists(fromUserId, authUser.getId());
            User fromUser = validateUserExists(fromUserId);

            authUser.getFriends().add(fromUserId);
            fromUser.getFriends().add(authUser.getId());
            userRepository.saveAll(Arrays.asList(authUser, fromUser));

            friendRequest.setStatus(FriendRequestStatus.ACCEPTED);
            friendRequestRepository.save(friendRequest);

            singleSubscriber.onSuccess(true);
        }).doOnError(t -> {
            LogHelper.error(Message.FRIEND_SERVICE_ERROR, "accept", fromUserId, t);
        });
    }

    @Override
    public Single<Boolean> decline(User authUser, String fromUserId) {
        return Single.<Boolean>create(singleSubscriber -> {
            FriendRequest friendRequest =
                validateFriendRequestNotExists(fromUserId, authUser.getId());
            validateUserExists(fromUserId);

            friendRequest.setStatus(FriendRequestStatus.DECLINED);
            friendRequestRepository.save(friendRequest);

            singleSubscriber.onSuccess(true);
        }).doOnError(t -> {
            LogHelper.error(Message.FRIEND_SERVICE_ERROR, "decline", fromUserId, t);
        });
    }

    private void validateSameUser(User authUser, String toId) {
        if (authUser.getId().equals(toId)) {
            throw new BaseErrorException(HttpStatus.BAD_REQUEST,
                Message.FRIEND_CANNOT_ADD_TO_YOURSELF);
        }
    }

    private void validateFriendAlready(User authUser, String toId) {
        authUser.getFriends().stream().filter(friendUserId -> friendUserId.equals(toId))
            .findAny().ifPresent(userFriend -> {
            throw new BaseErrorException(HttpStatus.BAD_REQUEST, Message.FRIEND_ALREADY_A_FRIEND);
        });
    }

    private void validateFriendRequestExists(String fromId, String toId) {
        friendRequestRepository
            .findByFromUserIdAndToUserIdAndStatus(fromId, toId, FriendRequestStatus.PENDING)
            .ifPresent(o -> {
                throw new BaseErrorException(HttpStatus.BAD_REQUEST,
                    Message.FRIEND_REQUEST_ALREADY_EXISTS);
            });
    }

    private FriendRequest validateFriendRequestNotExists(String fromId, String toId) {
        return friendRequestRepository
            .findByFromUserIdAndToUserIdAndStatus(fromId, toId, FriendRequestStatus.PENDING).orElseThrow(
                () -> new BaseErrorException(HttpStatus.BAD_REQUEST,
                    Message.FRIEND_REQUEST_NOT_FOUND));
    }

    private User validateUserExists(String userId) {
        return userRepository.findById(userId).orElseThrow(
            () -> new BaseErrorException(HttpStatus.BAD_REQUEST,
                String.format(Message.USER_NOT_FOUND, userId)));
    }

}
