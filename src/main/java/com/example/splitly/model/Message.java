package com.example.splitly.model;

public interface Message {

    // Info
    String GOOGLE_SIGN_IN = "Google sign in with User ID: {}";
    String JWT_AUTHENTICATION_FAILED = "Jwt authentication failed";
    String EXPIRED_OR_INVALID_TOKEN = "Invalid or expired token";
    String USER_NOT_FOUND = "User: %s not found";
    String BILL_CANNOT_ADD_TO_YOURSELF = "Cannot add bill to yourself";
    String BILL_PARTICIPANT_CANNOT_BE_EMPTY = "Bill participant cannot be empty";
    String BILL_NOT_FOUND = "Bill: %s not found";
    String FRIEND_CANNOT_ADD_TO_YOURSELF = "Cannot add friend to yourself";
    String FRIEND_ALREADY_A_FRIEND = "Already a friend";
    String FRIEND_REQUEST_ALREADY_EXISTS = "Friend request already exists";
    String FRIEND_REQUEST_NOT_FOUND = "Friend request not found";

    // Error
    String AUTH_SERVICE_ERROR = "AuthService#{} error with request: {}";
    String BILL_SERVICE_ERROR = "BillService#{} error with request: {}";
    String FRIEND_SERVICE_ERROR = "FriendService#{} error with request: {}";
    // Exception
    String INVALID_ID_TOKEN = "Invalid ID token";

    // Validation
    String EMAIL_ALREADY_REGISTERED = "Email already registered";
    String PASSWORD_IS_TOO_SHORT = "Password is too short, must be at least 8 characters";
    String PASSWORD_NOT_CONFIRMED = "Password not confirmed";

}
