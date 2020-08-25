package com.example.splitly.service;

import com.example.splitly.model.entity.User;
import com.example.splitly.model.entity.UserClient;
import com.example.splitly.model.response.FriendRequestResponse;
import com.example.splitly.model.response.PageResponse;
import rx.Single;

import java.util.List;

public interface FriendService {

    Single<List<UserClient>> getFriends(User authUser);

    Single<List<FriendRequestResponse>> getFriendRequests(User authUser);

    Single<Boolean> request(User authUser, String toUserId);

    Single<Boolean> accept(User authUser, String fromUserId);

    Single<Boolean> decline(User authUser, String fromUserId);

}
