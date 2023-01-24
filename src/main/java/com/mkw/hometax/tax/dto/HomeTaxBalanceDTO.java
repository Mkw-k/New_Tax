package com.mkw.hometax.tax.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;

@Data
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class HomeTaxBalanceDTO {
    @NotEmpty
    private String myId;
    private String balance;
}
