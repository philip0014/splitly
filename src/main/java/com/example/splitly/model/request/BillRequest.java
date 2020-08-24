package com.example.splitly.model.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BillRequest {

    @NotNull
    @NotBlank
    private String description;

    @NotNull
    @NotBlank
    private String currencyCode;

    @NotNull
    private List<BillParticipantRequest> participants;

}
