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
@EqualsAndHashCode(of = "myId")
//각 개인별 월세 납부 내역
public class HomeTaxInsertEntity {
    private String seq;
    @Id
    private String myId;
    private String inputFee;
    private String insertDay;
    private String day;
    private String delYn;
    private String confirmYn;
}
