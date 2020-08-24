package com.example.splitly.service;

import com.example.splitly.model.entity.User;
import com.example.splitly.model.request.BillPayRequest;
import com.example.splitly.model.request.BillRequest;
import com.example.splitly.model.response.BillResponse;
import com.example.splitly.model.response.PageResponse;
import rx.Single;

import java.util.List;

public interface BillService {

    Single<List<BillResponse>> getPendingBills(User authUser);

    Single<PageResponse<BillResponse>> getCompleteBills(User authUser, int page, int size);

    Single<List<BillResponse>> create(User authUser, BillRequest request);

    Single<BillResponse> pay(User authUser, String billId, BillPayRequest request);

    Single<BillResponse> settleUp(User authUser, String billId);

}
