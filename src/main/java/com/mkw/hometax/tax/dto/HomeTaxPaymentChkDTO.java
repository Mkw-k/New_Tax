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
public class HomeTaxPaymentChkDTO {
    @NotEmpty
    private String day;
    private String water;
    private String elec;
    private String gas;
    private String inter;
    private String managerFee;
    @NotEmpty
    private String monthFee;
    private String del;

}
