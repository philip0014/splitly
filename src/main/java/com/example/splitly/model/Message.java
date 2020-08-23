package com.example.splitly.model;

public interface Message {

    // Info
    String GOOGLE_SIGN_IN = "Google sign in with User ID: {}";
    String JWT_AUTHENTICATION_FAILED = "Jwt authentication failed";
    String EXPIRED_OR_INVALID_TOKEN = "Invalid or expired token";
    String USER_NOT_FOUND = "User: %s not found";
    // Error
    String AUTH_SERVICE_ERROR = "AuthService#{} error with request: {}";
    // Exception
    String INVALID_ID_TOKEN = "Invalid ID token";

    // Validation
    String EMAIL_ALREADY_REGISTERED = "Email already registered";
    String PASSWORD_IS_TOO_SHORT = "Password is too short, must be at least 8 characters";
    String PASSWORD_NOT_CONFIRMED = "Password not confirmed";

}
