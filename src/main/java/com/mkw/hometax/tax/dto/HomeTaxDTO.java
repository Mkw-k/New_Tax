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
public class HomeTaxDTO {
    @NotEmpty
    private String day;
    private String myId;
    private String water;
    private String elec;
    private String gas;
    private String inter;
    @NotEmpty
    private String managerFee;
    @NotEmpty
    private String monthFee;
    private String totalFee;

    public void calculateTotalFee() {
        this.totalFee = String.valueOf(
                parseInt(water)
                        +parseInt(elec)
                        +parseInt(gas)
                        +parseInt(inter)
                        +parseInt(managerFee)
                        +parseInt(monthFee));
    }

    public int parseInt(String fee){
        return Integer.parseInt(fee);
    }
}
