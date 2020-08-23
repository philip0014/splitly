package com.example.splitly.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = UserClient.COLLECTION_NAME)
public class UserClient {

    public static final String COLLECTION_NAME = "user_client";

    @Id
    private String id;
    private String username;
    private String profileUrl;

}
