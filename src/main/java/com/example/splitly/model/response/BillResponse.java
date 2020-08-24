package com.example.splitly.model.response;

import com.example.splitly.model.BillStatus;
import com.example.splitly.model.entity.UserClient;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BillResponse {

    private String id;
    private String description;
    private UserClient receiver;
    private UserClient giver;
    private Currency currency;
    private BigDecimal nominalNeeded;
    private BigDecimal nominalPaid;
    private Date createdAt;
    private BillStatus status;

}
