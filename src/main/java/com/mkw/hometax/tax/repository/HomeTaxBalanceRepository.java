package com.mkw.hometax.tax.repository;

import com.mkw.hometax.tax.entity.HomeTaxBalanceEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface HomeTaxBalanceRepository extends JpaRepository<HomeTaxBalanceEntity, String> {
    Optional<HomeTaxBalanceEntity> findByMyId(String myId);
}
