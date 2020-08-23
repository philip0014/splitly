package com.example.splitly.service.impl;

import com.example.splitly.exception.BaseErrorException;
import com.example.splitly.helper.ConverterHelper;
import com.example.splitly.helper.LogHelper;
import com.example.splitly.model.Message;
import com.example.splitly.model.entity.User;
import com.example.splitly.model.entity.UserClient;
import com.example.splitly.model.request.GoogleSignInRequest;
import com.example.splitly.model.request.RegisterRequest;
import com.example.splitly.model.request.SignInRequest;
import com.example.splitly.model.response.SignInResponse;
import com.example.splitly.repository.UserClientRepository;
import com.example.splitly.repository.UserRepository;
import com.example.splitly.security.jwt.JwtTokenProvider;
import com.example.splitly.service.AuthService;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import rx.Single;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class AuthServiceImpl implements AuthService {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserClientRepository userClientRepository;

    @Autowired
    private GoogleIdTokenVerifier verifier;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public Single<SignInResponse> register(RegisterRequest request) {
        return Single.<SignInResponse>create(singleSubscriber -> {
            if (userRepository.findByEmail(request.getEmail()).isPresent()) {
                throw new BaseErrorException(HttpStatus.BAD_REQUEST,
                    Message.EMAIL_ALREADY_REGISTERED);
            }

            if (request.getPassword().length < 6) {
                throw new BaseErrorException(HttpStatus.BAD_REQUEST,
                    Message.PASSWORD_IS_TOO_SHORT);
            }

            User user = createUser(request);
            userRepository.save(user);

            UserClient userClient = ConverterHelper.convertFromUserToUserClient(user);
            userClientRepository.save(userClient);

            String token = jwtTokenProvider.createToken(user.getEmail(),
                user.getRoles().stream().map(Enum::toString).collect(Collectors.toList()));

            SignInResponse response = SignInResponse.builder()
                .user(ConverterHelper.convertFromUserToProfileResponse(user))
                .accessToken(token)
                .build();
            singleSubscriber.onSuccess(response);
        }).doOnError(t -> {
            LogHelper.error(Message.AUTH_SERVICE_ERROR, "register", request.getEmail(), t);
        });
    }

    @Override
    public Single<SignInResponse> signIn(SignInRequest request) {
        return Single.<SignInResponse>create(singleSubscriber -> {
            authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(),
                    String.valueOf(request.getPassword())));

            User user = userRepository.findByEmail(request.getEmail()).orElseThrow(
                () -> new BaseErrorException(HttpStatus.UNAUTHORIZED,
                    String.format(Message.USER_NOT_FOUND, request.getEmail())));

            String token = jwtTokenProvider.createToken(user.getEmail(),
                user.getRoles().stream().map(Enum::toString).collect(Collectors.toList()));

            SignInResponse response = SignInResponse.builder()
                .user(ConverterHelper.convertFromUserToProfileResponse(user))
                .accessToken(token)
                .build();

            singleSubscriber.onSuccess(response);
        }).doOnError(t -> {
            LogHelper.error(Message.AUTH_SERVICE_ERROR, "signIn", request.getEmail(), t);
        });
    }

    @Override
    public Single<SignInResponse> googleSignIn(GoogleSignInRequest request) {
        return Single.<SignInResponse>create(singleSubscriber -> {
            try {
                GoogleIdToken idToken = verifier.verify(request.getUserIdToken());
                GoogleIdToken.Payload payload;

                if (Objects.nonNull(idToken)) {
                    payload = idToken.getPayload();

                    String userId = payload.getSubject();
                    LogHelper.info(Message.GOOGLE_SIGN_IN, userId);
                } else {
                    throw new BaseErrorException(HttpStatus.UNAUTHORIZED, Message.INVALID_ID_TOKEN);
                }

                User user = getUser(payload);
                String token = jwtTokenProvider.createToken(user.getEmail(),
                    user.getRoles().stream().map(Enum::toString).collect(Collectors.toList()));

                SignInResponse response = SignInResponse.builder()
                    .user(ConverterHelper.convertFromUserToProfileResponse(user))
                    .accessToken(token)
                    .build();

                singleSubscriber.onSuccess(response);
            } catch (GeneralSecurityException | IOException e) {
                e.printStackTrace();
                throw new BaseErrorException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
            }
        }).doOnError(t -> {
            LogHelper.error(Message.AUTH_SERVICE_ERROR, "googleSignIn", request, t);
        });
    }

    private User getUser(GoogleIdToken.Payload payload) {
        return userRepository.findByEmail(payload.getEmail())
            .orElseGet(() -> {
                User user = createUser(payload);
                userRepository.save(user);

                UserClient userClient = ConverterHelper.convertFromUserToUserClient(user);
                userClientRepository.save(userClient);
                return user;
            });
    }

    private User createUser(RegisterRequest request) {
        return User.builder()
            .email(request.getEmail())
            .username(request.getUsername())
            .password(passwordEncoder.encode(String.valueOf(request.getPassword())))
            .locale(request.getLocale())
            .build();
    }

    private User createUser(GoogleIdToken.Payload payload) {
        return User.builder()
            .email(payload.getEmail())
            .username((String) payload.get("name"))
            .locale((String) payload.get("locale"))
            .build();
    }
}
