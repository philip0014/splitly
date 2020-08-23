package com.example.splitly.security.jwt;

import com.example.splitly.model.Message;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
        AuthenticationException authException) throws IOException {
        log.info(Message.JWT_AUTHENTICATION_FAILED, authException);
        response.sendError(HttpServletResponse.SC_UNAUTHORIZED, Message.JWT_AUTHENTICATION_FAILED);
    }

}
