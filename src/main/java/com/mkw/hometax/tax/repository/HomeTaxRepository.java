package com.mkw.hometax.tax.repository;

import com.mkw.hometax.tax.entity.HomeTaxEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface HomeTaxRepository extends JpaRepository<HomeTaxEntity, String> {
    Optional<HomeTaxEntity> findByDay(String day);
}
