package com.mkw.hometax.tax.repository;

import com.mkw.hometax.tax.entity.HomeTaxInsertEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface HomeTaxInsertReepository extends JpaRepository<HomeTaxInsertEntity, String> {
    Optional<HomeTaxInsertEntity> findByMyId(String myId);
}
