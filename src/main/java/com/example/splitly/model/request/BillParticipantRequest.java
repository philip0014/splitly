package com.example.splitly.model.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BillParticipantRequest {

    @NotNull
    @NotBlank
    private String userId;

    @NotNull
    private BigDecimal nominal;

}
