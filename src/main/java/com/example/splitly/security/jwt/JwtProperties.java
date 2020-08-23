package com.example.splitly.security.jwt;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "jwt")
public class JwtProperties {

    @Value("${security.secret.key}")
    private String secretKey;

    @Value("${security.token.validity}")
    private String validityInMs;

}
