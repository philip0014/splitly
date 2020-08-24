package com.example.splitly.service.impl;

import com.example.splitly.exception.BaseErrorException;
import com.example.splitly.helper.ConverterHelper;
import com.example.splitly.helper.LogHelper;
import com.example.splitly.helper.PageResponseHelper;
import com.example.splitly.model.BillStatus;
import com.example.splitly.model.Message;
import com.example.splitly.model.entity.Bill;
import com.example.splitly.model.entity.User;
import com.example.splitly.model.entity.UserClient;
import com.example.splitly.model.request.BillParticipantRequest;
import com.example.splitly.model.request.BillPayRequest;
import com.example.splitly.model.request.BillRequest;
import com.example.splitly.model.response.BillResponse;
import com.example.splitly.model.response.PageResponse;
import com.example.splitly.repository.BillRepository;
import com.example.splitly.repository.UserClientRepository;
import com.example.splitly.repository.UserRepository;
import com.example.splitly.service.BillService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.util.Pair;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import rx.Single;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Currency;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class BillServiceImpl implements BillService {

    @Autowired
    private BillRepository billRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserClientRepository userClientRepository;

    @Override
    public Single<List<BillResponse>> getPendingBills(User authUser) {
        return Single.<List<BillResponse>>create(singleSubscriber -> {
            List<Bill> bills =
                billRepository.findByIdInAndStatus(authUser.getBills(), BillStatus.PENDING);
            Map<String, UserClient> mapOfUserClient = new HashMap<>();
            Set<String> userIds =
                bills.stream().map(Bill::getReceiverUserId).collect(Collectors.toSet());
            userIds.addAll(bills.stream().map(Bill::getGiverUserId).collect(Collectors.toSet()));
            List<UserClient> userClients = userClientRepository.findByIdIn(userIds);
            userClients.forEach(userClient -> mapOfUserClient.put(userClient.getId(), userClient));

            List<BillResponse> billResponses = bills.stream().map(bill -> {
                UserClient receiverClient = mapOfUserClient.get(bill.getReceiverUserId());
                UserClient giverClient = mapOfUserClient.get(bill.getGiverUserId());
                return ConverterHelper
                    .convertFromBillToBillResponse(bill, receiverClient, giverClient);
            }).sorted(Comparator.comparing(BillResponse::getCreatedAt).reversed())
                .collect(Collectors.toList());

            singleSubscriber.onSuccess(billResponses);
        }).doOnError(t -> {
            LogHelper.error(Message.BILL_SERVICE_ERROR, "getPendingBills", authUser.getId(), t);
        });
    }

    @Override
    public Single<PageResponse<BillResponse>> getCompleteBills(User authUser, int page, int size) {
        return Single.<PageResponse<BillResponse>>create(singleSubscriber -> {
            Sort sort = Sort.by("createdAt").descending();
            PageRequest pageRequest = PageRequest.of(page, size, sort);
            Page<Bill> billPage = billRepository
                .findByIdInAndStatus(authUser.getBills(), BillStatus.COMPLETE, pageRequest);

            Map<String, UserClient> mapOfUserClient = new HashMap<>();
            Set<String> userIds = billPage.getContent().stream().map(Bill::getReceiverUserId)
                .collect(Collectors.toSet());
            userIds.addAll(billPage.getContent().stream().map(Bill::getGiverUserId)
                .collect(Collectors.toSet()));
            List<UserClient> userClients = userClientRepository.findByIdIn(userIds);
            userClients.forEach(userClient -> mapOfUserClient.put(userClient.getId(), userClient));

            List<BillResponse> billResponses = billPage.getContent().stream().map(bill -> {
                UserClient receiverClient = mapOfUserClient.get(bill.getReceiverUserId());
                UserClient giverClient = mapOfUserClient.get(bill.getGiverUserId());
                return ConverterHelper
                    .convertFromBillToBillResponse(bill, receiverClient, giverClient);
            }).collect(Collectors.toList());
            singleSubscriber.onSuccess(PageResponseHelper
                .create(billResponses, page, billPage.getTotalPages(), size,
                    (int) billPage.getTotalElements()));
        }).doOnError(t -> {
            LogHelper.error(Message.BILL_SERVICE_ERROR, "getCompleteBills", authUser.getId(), t);
        });
    }

    @Override
    public Single<List<BillResponse>> create(User authUser, BillRequest request) {
        return Single.<List<BillResponse>>create(singleSubscriber -> {
            validateParticipants(request.getParticipants());

            Map<String, UserClient> mapOfGiverClient = new HashMap<>();
            List<Pair<User, Bill>> listOfPairUserBill = new ArrayList<>();
            List<Bill> bills = request.getParticipants().stream().map(billParticipantRequest -> {
                validateSameUser(authUser, billParticipantRequest.getUserId());
                User giver = validateUserExists(billParticipantRequest.getUserId());
                mapOfGiverClient
                    .put(giver.getId(), ConverterHelper.convertFromUserToUserClient(giver));
                Bill bill = toBill(request.getDescription(), authUser.getId(), giver.getId(),
                    request.getCurrencyCode(), billParticipantRequest.getNominal());
                listOfPairUserBill.add(Pair.of(giver, bill));
                return bill;
            }).collect(Collectors.toList());

            billRepository.saveAll(bills);

            listOfPairUserBill.forEach(userBillPair -> {
                User user = userBillPair.getFirst();
                Bill bill = userBillPair.getSecond();
                user.getBills().add(bill.getId());
                userRepository.save(user);
            });

            List<String> billIds = bills.stream().map(Bill::getId).collect(Collectors.toList());
            authUser.getBills().addAll(billIds);
            userRepository.save(authUser);

            UserClient receiverClient = ConverterHelper.convertFromUserToUserClient(authUser);
            List<BillResponse> billResponses =
                bills.stream().map(bill -> {
                    UserClient giverClient = mapOfGiverClient.get(bill.getGiverUserId());
                    return ConverterHelper
                        .convertFromBillToBillResponse(bill, receiverClient, giverClient);
                }).collect(Collectors.toList());
            singleSubscriber.onSuccess(billResponses);
        }).doOnError(t -> {
            LogHelper.error(Message.BILL_SERVICE_ERROR, "create", request, t);
        });
    }

    @Override
    public Single<BillResponse> pay(User authUser, String billId, BillPayRequest request) {
        return Single.<BillResponse>create(singleSubscriber -> {
            Bill bill = validateBillExists(billId);
            validateUserGiver(authUser, bill);
            UserClient receiverClient = validateUserClientExists(bill.getReceiverUserId());
            UserClient giverClient = ConverterHelper.convertFromUserToUserClient(authUser);

            bill.setNominalPaid(bill.getNominalPaid().add(request.getNominalPaid()));
            if (bill.getNominalPaid().compareTo(bill.getNominalNeeded()) >= 0) {
                bill.setStatus(BillStatus.COMPLETE);
            }
            billRepository.save(bill);

            BillResponse billResponse =
                ConverterHelper.convertFromBillToBillResponse(bill, receiverClient, giverClient);
            singleSubscriber.onSuccess(billResponse);
        }).doOnError(t -> {
            LogHelper.error(Message.BILL_SERVICE_ERROR, "pay", billId, request, t);
        });
    }

    @Override
    public Single<BillResponse> settleUp(User authUser, String billId) {
        return Single.<BillResponse>create(singleSubscriber -> {
            Bill bill = validateBillExists(billId);
            validateUserGiver(authUser, bill);
            UserClient receiverClient = validateUserClientExists(bill.getReceiverUserId());
            UserClient giverClient = ConverterHelper.convertFromUserToUserClient(authUser);

            bill.setNominalPaid(bill.getNominalNeeded());
            bill.setStatus(BillStatus.COMPLETE);
            billRepository.save(bill);

            BillResponse billResponse =
                ConverterHelper.convertFromBillToBillResponse(bill, receiverClient, giverClient);
            singleSubscriber.onSuccess(billResponse);
        }).doOnError(t -> {
            LogHelper.error(Message.BILL_SERVICE_ERROR, "settleUp", billId, t);
        });
    }

    private void validateSameUser(User authUser, String userId) {
        if (authUser.getId().equals(userId)) {
            throw new BaseErrorException(HttpStatus.BAD_REQUEST,
                Message.BILL_CANNOT_ADD_TO_YOURSELF);
        }
    }

    private void validateParticipants(List<BillParticipantRequest> participants) {
        if (CollectionUtils.isEmpty(participants)) {
            throw new BaseErrorException(HttpStatus.BAD_REQUEST,
                Message.BILL_PARTICIPANT_CANNOT_BE_EMPTY);
        }
    }

    private User validateUserExists(String userId) {
        return userRepository.findById(userId).orElseThrow(() -> new BaseErrorException(
            HttpStatus.BAD_REQUEST, String.format(Message.USER_NOT_FOUND, userId)));
    }

    private UserClient validateUserClientExists(String userId) {
        return userClientRepository.findById(userId).orElseThrow(() -> new BaseErrorException(
            HttpStatus.BAD_REQUEST, String.format(Message.USER_NOT_FOUND, userId)));
    }

    private void validateUserGiver(User authUser, Bill bill) {
        if (!authUser.getId().equals(bill.getGiverUserId())) {
            throw new BaseErrorException(HttpStatus.BAD_REQUEST,
                String.format(Message.BILL_NOT_FOUND, bill.getId()));
        }
    }

    private Bill validateBillExists(String billId) {
        return billRepository.findByIdAndStatus(billId, BillStatus.PENDING).orElseThrow(
            () -> new BaseErrorException(HttpStatus.BAD_REQUEST,
                String.format(Message.BILL_NOT_FOUND, billId)));
    }

    private Bill toBill(String description, String receiverUserId, String giverUserId,
        String currencyCode, BigDecimal nominal) {
        return Bill.builder()
            .description(description)
            .receiverUserId(receiverUserId)
            .giverUserId(giverUserId)
            .currency(Currency.getInstance(currencyCode))
            .nominalNeeded(nominal)
            .build();
    }

}
