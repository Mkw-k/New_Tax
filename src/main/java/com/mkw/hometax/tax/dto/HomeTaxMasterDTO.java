package com.mkw.hometax.tax.dto;

import com.mkw.hometax.Accounts.Account;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.validation.constraints.NotEmpty;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class HomeTaxMasterDTO {
    @NotEmpty
    private String day;
    private String water;
    private String elec;
    private String gas;
    private String inter;
    @NotEmpty
    private String managerFee;
    @NotEmpty
    private String monthFee;
    private String totalFee;
    @CreationTimestamp
    private LocalDateTime inptDttm;
    @UpdateTimestamp
    private LocalDateTime updtDttm;
    private Account manager;

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
