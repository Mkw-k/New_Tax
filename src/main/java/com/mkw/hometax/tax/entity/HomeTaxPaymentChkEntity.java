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
//관리자가 관리하는 월세 납부 내역 엔터티임
//전체 광과금 납부 여부 및 월세 납부 현황을 확인하는 엔터티임
public class HomeTaxPaymentChkEntity {
    @Id
    private String day;
    private String Water;
    private String Elec;
    private String Gas;
    private String Inter;
    private String ManagerFee;
    private String MonthFee;
    private String del;
}
