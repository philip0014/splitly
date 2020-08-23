package com.example.splitly.web;

import com.example.splitly.helper.ResponseHelper;
import com.example.splitly.model.ApiPath;
import com.example.splitly.model.request.GoogleSignInRequest;
import com.example.splitly.model.request.RegisterRequest;
import com.example.splitly.model.request.SignInRequest;
import com.example.splitly.model.response.Response;
import com.example.splitly.model.response.SignInResponse;
import com.example.splitly.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import rx.Single;

import javax.validation.Valid;

@RestController(value = "AuthRestController")
@RequestMapping(ApiPath.AUTH)
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/register")
    public Single<Response<SignInResponse>> register(@Valid @RequestBody RegisterRequest request) {
        return authService.register(request).map(ResponseHelper::ok);
    }

    @PostMapping("/signIn")
    public Single<Response<SignInResponse>> signIn(@Valid @RequestBody SignInRequest request) {
        return authService.signIn(request).map(ResponseHelper::ok);
    }

    @PostMapping("/google")
    public Single<Response<SignInResponse>> googleSignIn(
        @Valid @RequestBody GoogleSignInRequest request) {
        return authService.googleSignIn(request).map(ResponseHelper::ok);
    }

}
