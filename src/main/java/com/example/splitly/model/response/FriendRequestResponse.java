package com.example.splitly.model.response;

import com.example.splitly.model.entity.UserClient;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FriendRequestResponse {

    private String id;
    private UserClient from;
    private UserClient to;

}
