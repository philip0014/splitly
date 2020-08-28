package com.example.splitly.service;

import com.example.splitly.model.entity.User;
import com.example.splitly.model.entity.UserClient;
import rx.Single;

import java.util.List;

public interface SearchService {

    Single<List<UserClient>> findUsersByKeyword(User authUser, String keyword, int limit);

    Single<List<UserClient>> findNewFriendsByKeyword(User authUser, String keyword, int limit);

}
