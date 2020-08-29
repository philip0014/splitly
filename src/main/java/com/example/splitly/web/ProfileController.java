package com.example.splitly.web;

import com.example.splitly.helper.ResponseHelper;
import com.example.splitly.model.ApiPath;
import com.example.splitly.model.entity.User;
import com.example.splitly.model.request.ProfileRequest;
import com.example.splitly.model.response.Response;
import com.example.splitly.model.response.ProfileResponse;
import com.example.splitly.service.ProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import rx.Single;

import javax.validation.Valid;

@RestController(value = "ProfileRestController")
@RequestMapping(ApiPath.PROFILE)
public class ProfileController {

    @Autowired
    private ProfileService profileService;

    @GetMapping
    public Single<Response<ProfileResponse>> getProfile(@AuthenticationPrincipal User authUser) {
        return profileService.getProfile(authUser).map(ResponseHelper::ok);
    }

    @PutMapping
    public Single<Response<ProfileResponse>> updateProfile(@AuthenticationPrincipal User authUser,
        @Valid @ModelAttribute @RequestBody ProfileRequest request) {
        return profileService.updateProfile(authUser, request).map(ResponseHelper::ok);
    }

}
