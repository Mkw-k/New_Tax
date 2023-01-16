package com.mkw.hometax.tax.repository;

import com.mkw.hometax.tax.entity.HomeTaxPaymentChkEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface HomeTaxPaymentChkRepository extends JpaRepository<HomeTaxPaymentChkEntity, String> {
    Optional<HomeTaxPaymentChkEntity> findByDay(String s);
}
