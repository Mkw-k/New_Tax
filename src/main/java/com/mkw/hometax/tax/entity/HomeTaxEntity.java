package com.mkw.hometax.tax.entity;

import lombok.*;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table
@Builder @AllArgsConstructor
@NoArgsConstructor
@Getter @Setter @EqualsAndHashCode(of = "myId")
public class HomeTaxEntity {
    private String seq;
    @Id
    private String myId;
    private String day;
    private String water;
    private String elec;
    private String gas;
    private String inter;
    private String managerFee;
    private String monthFee;
    private String totalFee;
    private String del;
}
