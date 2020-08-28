package com.example.splitly.web;

import com.example.splitly.helper.ResponseHelper;
import com.example.splitly.model.ApiPath;
import com.example.splitly.model.entity.User;
import com.example.splitly.model.entity.UserClient;
import com.example.splitly.model.response.Response;
import com.example.splitly.service.SearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import rx.Single;

import java.util.List;

@RestController(value = "SearchRestController")
@RequestMapping(ApiPath.SEARCH)
public class SearchController {

    @Autowired
    private SearchService searchService;

    @GetMapping("/users")
    public Single<Response<List<UserClient>>> findUsersByKeyword(
        @AuthenticationPrincipal User authUser, @RequestParam String keyword,
        @RequestParam(defaultValue = "5") int limit) {
        return searchService.findUsersByKeyword(authUser, keyword, limit).map(ResponseHelper::ok);
    }

    @GetMapping("/new-friends")
    public Single<Response<List<UserClient>>> findNewFriendsByKeyword(
        @AuthenticationPrincipal User authUser, @RequestParam String keyword,
        @RequestParam(defaultValue = "5") int limit) {
        return searchService.findNewFriendsByKeyword(authUser, keyword, limit)
            .map(ResponseHelper::ok);
    }

}
