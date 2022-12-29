package com.mkw.hometax.tax.entity;

import lombok.*;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class HomeTaxPaymentChkEntity {
    @Id
    private String bDay;
    private String bWater;
    private String bElec;
    private String bGas;
    private String bInter;
    private String bManagerFee;
    private String bMonthFee;
    private String bTotal;
    private String del;
}
