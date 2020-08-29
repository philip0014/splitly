package com.example.splitly.service;

import com.example.splitly.model.entity.User;
import com.example.splitly.model.request.ProfileRequest;
import com.example.splitly.model.response.ProfileResponse;
import rx.Single;

public interface ProfileService {

    Single<ProfileResponse> getProfile(User authUser);

    Single<ProfileResponse> updateProfile(User authUser, ProfileRequest request);

}
