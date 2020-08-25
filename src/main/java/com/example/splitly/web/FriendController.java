package com.example.splitly.web;

import com.example.splitly.helper.ResponseHelper;
import com.example.splitly.model.ApiPath;
import com.example.splitly.model.entity.User;
import com.example.splitly.model.entity.UserClient;
import com.example.splitly.model.request.FriendRequest;
import com.example.splitly.model.response.FriendRequestResponse;
import com.example.splitly.model.response.PageResponse;
import com.example.splitly.model.response.Response;
import com.example.splitly.service.FriendService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import rx.Single;

import javax.validation.Valid;
import java.util.List;

@RestController(value = "FriendRestController")
@RequestMapping(ApiPath.FRIEND)
public class FriendController {

    @Autowired
    private FriendService friendService;

    @GetMapping
    public Single<Response<List<UserClient>>> getFriends(
        @AuthenticationPrincipal User authUser) {
        return friendService.getFriends(authUser).map(ResponseHelper::ok);
    }

    @GetMapping("/request")
    public Single<Response<List<FriendRequestResponse>>> getFriendRequests(
        @AuthenticationPrincipal User authUser) {
        return friendService.getFriendRequests(authUser).map(ResponseHelper::ok);
    }

    @PostMapping("/request")
    public Single<Response<Boolean>> request(@AuthenticationPrincipal User authUser,
        @Valid @RequestBody FriendRequest request) {
        return friendService.request(authUser, request.getId()).map(ResponseHelper::ok);
    }

    @PutMapping("/accept/{userId}")
    public Single<Response<Boolean>> accept(@AuthenticationPrincipal User authUser,
        @PathVariable String userId) {
        return friendService.accept(authUser, userId).map(ResponseHelper::ok);
    }

    @PutMapping("/decline/{userId}")
    public Single<Response<Boolean>> decline(@AuthenticationPrincipal User authUser,
        @PathVariable String userId) {
        return friendService.decline(authUser, userId).map(ResponseHelper::ok);
    }

}
