package com.example.splitly.helper;

import com.example.splitly.model.entity.FriendRequest;
import com.example.splitly.model.entity.UserClient;
import com.example.splitly.model.entity.Bill;
import com.example.splitly.model.entity.User;
import com.example.splitly.model.response.BillResponse;
import com.example.splitly.model.response.FriendRequestResponse;
import com.example.splitly.model.response.ProfileResponse;

public class ConverterHelper {

    public static ProfileResponse convertFromUserToProfileResponse(User user) {
        return ProfileResponse.builder()
            .id(user.getId())
            .username(user.getUsername())
            .email(user.getEmail())
            .profileUrl(user.getProfileUrl())
            .locale(user.getLocale())
            .build();
    }

    public static UserClient convertFromUserToUserClient(User user) {
        return UserClient.builder()
            .id(user.getId())
            .username(user.getUsername())
            .profileUrl(user.getProfileUrl())
            .build();
    }

    public static BillResponse convertFromBillToBillResponse(Bill bill, UserClient receiver,
        UserClient giver) {
        return BillResponse.builder()
            .id(bill.getId())
            .description(bill.getDescription())
            .receiver(receiver)
            .giver(giver)
            .currency(bill.getCurrency())
            .nominalNeeded(bill.getNominalNeeded())
            .nominalPaid(bill.getNominalPaid())
            .createdAt(bill.getCreatedAt())
            .status(bill.getStatus())
            .build();
    }

    public static FriendRequestResponse convertFromFriendRequestToFriendRequestResponse(
        FriendRequest friendRequest, UserClient from, UserClient to) {
        return FriendRequestResponse.builder()
            .id(friendRequest.getId())
            .from(from)
            .to(to)
            .build();
    }

}
