package com.mkw.hometax.tax.entity;

import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table
@Builder @AllArgsConstructor
@NoArgsConstructor
@Getter @Setter
//각 개인별 월별 월세 내역
public class HomeTaxEntity {
    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue
    private Long id;
    private String day;
    private String water;
    private String elec;
    private String gas;
    private String inter;
    private String managerFee;
    private String monthFee;
    private String totalFee;
    private String del;
    private String myId;
    @Column(name = "INPT_DTTM")
    @CreationTimestamp
    private LocalDateTime inptDttm;
    @Column(name = "UPDT_DTTM")
    @UpdateTimestamp
    private LocalDateTime updtDttm;
}
