package com.example.splitly.service;

import com.example.splitly.model.request.GoogleSignInRequest;
import com.example.splitly.model.request.RegisterRequest;
import com.example.splitly.model.request.SignInRequest;
import com.example.splitly.model.response.SignInResponse;
import rx.Single;

public interface AuthService {

    Single<SignInResponse> register(RegisterRequest request);

    Single<SignInResponse> signIn(SignInRequest request);

    Single<SignInResponse> googleSignIn(GoogleSignInRequest request);

}
