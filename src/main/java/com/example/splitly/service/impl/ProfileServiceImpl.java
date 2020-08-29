package com.example.splitly.service.impl;

import com.example.splitly.exception.BaseErrorException;
import com.example.splitly.helper.ConverterHelper;
import com.example.splitly.helper.LogHelper;
import com.example.splitly.model.Message;
import com.example.splitly.model.entity.User;
import com.example.splitly.model.entity.UserClient;
import com.example.splitly.model.request.ProfileRequest;
import com.example.splitly.model.response.ProfileResponse;
import com.example.splitly.repository.UserClientRepository;
import com.example.splitly.repository.UserRepository;
import com.example.splitly.service.ProfileService;
import com.example.splitly.service.StorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import rx.Single;

import java.util.Objects;

@Service
public class ProfileServiceImpl implements ProfileService {

    @Autowired
    private StorageService storageService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserClientRepository userClientRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public Single<ProfileResponse> getProfile(User authUser) {
        return Single.<ProfileResponse>create(singleSubscriber -> {
            ProfileResponse profileResponse =
                ConverterHelper.convertFromUserToProfileResponse(authUser);
            singleSubscriber.onSuccess(profileResponse);
        }).doOnError(t -> {
            LogHelper.error(Message.PROFILE_SERVICE_ERROR, "getProfile", authUser.getId(), t);
        });
    }

    @Override
    public Single<ProfileResponse> updateProfile(User authUser, ProfileRequest request) {
        return Single.<ProfileResponse>create(singleSubscriber -> {
            if (Objects.nonNull(request.getPassword()) && request.getPassword().length != 0) {
                if (request.getPassword().length < 6) {
                    throw new BaseErrorException(HttpStatus.BAD_REQUEST,
                        Message.PASSWORD_IS_TOO_SHORT);
                } else if (Objects.isNull(request.getConfirmPassword()) || !passwordConfirmed(
                    request.getPassword(), request.getConfirmPassword())) {
                    throw new BaseErrorException(HttpStatus.BAD_REQUEST,
                        Message.PASSWORD_NOT_CONFIRMED);
                }
                authUser.setPassword(passwordEncoder.encode(String.valueOf(request.getPassword())));
            }

            if (!StringUtils.isEmpty(request.getLocale())) {
                authUser.setLocale(request.getLocale());
            }
            authUser.setUsername(request.getUsername());

            UserClient userClient = validateUserClientExists(authUser.getId());
            userClient.setUsername(request.getUsername());

            if (Objects.nonNull(request.getImage())) {
                try {
                    String imageUrl =
                        storageService.saveImage(authUser.getId(), request.getImage());
                    authUser.setProfileUrl(imageUrl);
                    userClient.setProfileUrl(imageUrl);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }

            userRepository.save(authUser);
            userClientRepository.save(userClient);

            ProfileResponse profileResponse =
                ConverterHelper.convertFromUserToProfileResponse(authUser);
            singleSubscriber.onSuccess(profileResponse);
        }).doOnError(t -> {
            LogHelper.error(Message.PROFILE_SERVICE_ERROR, "updateProfile", authUser.getId(), t);
        });
    }

    private UserClient validateUserClientExists(String userId) {
        return userClientRepository.findById(userId).orElseThrow(() -> new BaseErrorException(
            HttpStatus.BAD_REQUEST, String.format(Message.USER_NOT_FOUND, userId)));
    }

    private boolean passwordConfirmed(char[] password, char[] confirmPassword) {
        int passwordLength = password.length;
        int cPasswordLength = confirmPassword.length;
        if (passwordLength != cPasswordLength) {
            return false;
        }

        for (int i = 0; i < passwordLength; i++) {
            if (password[i] != confirmPassword[i]) {
                return false;
            }
        }
        return true;
    }

}
