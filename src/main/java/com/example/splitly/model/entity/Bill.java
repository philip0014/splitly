package com.example.splitly.model.entity;

import com.example.splitly.model.BillStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = Bill.COLLECTION_NAME)
public class Bill {

    public static final String COLLECTION_NAME = "bill";

    @Id
    private String id;

    private String description;
    private String receiverUserId; // Who needs to get the money
    private String giverUserId; // Who needs to pay
    private Currency currency;

    private BigDecimal nominalNeeded;

    @Builder.Default
    private BigDecimal nominalPaid = BigDecimal.valueOf(0);

    @Builder.Default
    private Date createdAt = new Date();

    @Builder.Default
    private BillStatus status = BillStatus.PENDING;

}
