package com.mkw.hometax.tax.entity;

import com.mkw.hometax.Accounts.Account;
import lombok.*;

import javax.persistence.*;

@Entity
@Table
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode(of = "id")
//월별 월세 총합 내역 => 이 엔터티를 통하여 개인별 엔터티를 입력한다
public class HomeTaxMasterEntity {
    @Id @GeneratedValue
    private Integer id;
    private String day;
    private String water;
    private String elec;
    private String gas;
    private String inter;
    private String managerFee;
    private String monthFee;
    private String totalFee;
    @ManyToOne
    @JoinColumn(name = "MYID")
    private Account inptUser;
}
