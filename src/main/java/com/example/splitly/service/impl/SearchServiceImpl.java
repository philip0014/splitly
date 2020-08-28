package com.example.splitly.service.impl;

import com.example.splitly.helper.LogHelper;
import com.example.splitly.model.Message;
import com.example.splitly.model.entity.User;
import com.example.splitly.model.entity.UserClient;
import com.example.splitly.repository.UserClientRepository;
import com.example.splitly.repository.UserRepository;
import com.example.splitly.service.SearchService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import rx.Single;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.example.splitly.helper.ConverterHelper.convertFromUserToUserClient;

@Slf4j
@Service
public class SearchServiceImpl implements SearchService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserClientRepository userClientRepository;

    private final String USERNAME_REGEX_FORMAT = ".*%s.*";

    @Override
    public Single<List<UserClient>> findUsersByKeyword(User authUser, String keyword, int limit) {
        return Single.<List<UserClient>>create(singleSubscriber -> {
            singleSubscriber.onSuccess(findUsers(keyword, limit, new ArrayList<>()).stream()
                .filter(userClient -> !authUser.getId().equals(userClient.getId()))
                .collect(Collectors.toList()));
        }).doOnError(t -> {
            LogHelper.error(Message.PROFILE_SERVICE_ERROR, "getUsersByKeyword", keyword, t);
        });
    }

    @Override
    public Single<List<UserClient>> findNewFriendsByKeyword(User authUser, String keyword,
        int limit) {
        return Single.<List<UserClient>>create(singleSubscriber -> {
            singleSubscriber.onSuccess(findUsers(keyword, limit, authUser.getFriends()).stream()
                .filter(userClient -> !authUser.getId().equals(userClient.getId()))
                .collect(Collectors.toList()));
        }).doOnError(t -> {
            LogHelper.error(Message.PROFILE_SERVICE_ERROR, "findNewFriendsByKeyword", keyword, t);
        });
    }

    private List<UserClient> findUsers(String keyword, int limit, List<String> idFilter) {
        Set<UserClient> userClients = new HashSet<>();
        userRepository.findByEmail(keyword.toLowerCase())
            .ifPresent(user -> userClients.add(convertFromUserToUserClient(user)));
        userClients.addAll(userClientRepository.findByUsernameRegexAndIdNotIn(
            String.format(USERNAME_REGEX_FORMAT, keyword.toLowerCase()),
            idFilter, PageRequest.of(0, limit)));
        return new ArrayList<>(userClients);
    }

}
