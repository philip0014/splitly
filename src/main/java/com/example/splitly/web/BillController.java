package com.example.splitly.web;

import com.example.splitly.helper.ResponseHelper;
import com.example.splitly.model.ApiPath;
import com.example.splitly.model.entity.User;
import com.example.splitly.model.request.BillPayRequest;
import com.example.splitly.model.request.BillRequest;
import com.example.splitly.model.response.BillResponse;
import com.example.splitly.model.response.PageResponse;
import com.example.splitly.model.response.Response;
import com.example.splitly.service.BillService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import rx.Single;

import javax.validation.Valid;
import java.util.List;

@RestController(value = "BillRestController")
@RequestMapping(ApiPath.BILL)
public class BillController {

    @Autowired
    private BillService billService;

    @GetMapping("/pending")
    public Single<Response<List<BillResponse>>> getPendingBills(
        @AuthenticationPrincipal User authUser) {
        return billService.getPendingBills(authUser).map(ResponseHelper::ok);
    }

    @GetMapping("/complete")
    public Single<Response<PageResponse<BillResponse>>> getCompleteBills(
        @AuthenticationPrincipal User authUser, @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size) {
        return billService.getCompleteBills(authUser, page, size).map(ResponseHelper::ok);
    }

    @PostMapping("/create")
    public Single<Response<List<BillResponse>>> create(@AuthenticationPrincipal User authUser,
        @Valid @RequestBody BillRequest request) {
        return billService.create(authUser, request).map(ResponseHelper::ok);
    }

    @PutMapping("/pay/{billId}")
    public Single<Response<BillResponse>> pay(@AuthenticationPrincipal User authUser,
        @PathVariable String billId, @Valid @RequestBody BillPayRequest request) {
        return billService.pay(authUser, billId, request).map(ResponseHelper::ok);
    }

    @PutMapping("/settleUp/{billId}")
    public Single<Response<BillResponse>> settleUp(@AuthenticationPrincipal User authUser,
        @PathVariable String billId) {
        return billService.settleUp(authUser, billId).map(ResponseHelper::ok);
    }

}
