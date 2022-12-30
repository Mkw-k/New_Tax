package com.mkw.hometax.tax.repository;

import com.mkw.hometax.tax.entity.HomeTaxMasterEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface HomeTaxMasterRepository extends JpaRepository<HomeTaxMasterEntity, String> {
    Optional<HomeTaxMasterEntity> findByDay(String day);
}
