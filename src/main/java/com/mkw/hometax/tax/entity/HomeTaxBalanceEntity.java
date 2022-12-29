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
public class HomeTaxBalanceEntity {
    @Id
    private String myId;
    private String balance;
}
