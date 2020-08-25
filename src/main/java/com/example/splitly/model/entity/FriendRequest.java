package com.example.splitly.model.entity;

import com.example.splitly.model.FriendRequestStatus;
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
@Document(collection = FriendRequest.COLLECTION_NAME)
public class FriendRequest {

    public static final String COLLECTION_NAME = "friend_request";

    @Id
    private String id;
    private String fromUserId;
    private String toUserId;

    @Builder.Default
    private FriendRequestStatus status = FriendRequestStatus.PENDING;

}
