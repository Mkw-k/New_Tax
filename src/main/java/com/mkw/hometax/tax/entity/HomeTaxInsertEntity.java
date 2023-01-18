package com.mkw.hometax.tax.entity;

import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.LocalDateTime;

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
    @Id @GeneratedValue
    private Integer seq;
    private String myId;
    private String inputFee;
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime insertDate;
    private String day;
    private String delYn;
    private String confirmYn;
}
