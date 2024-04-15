package com.example.nexus.repository;

import com.example.nexus.model.entity.Sale;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.time.LocalDate;
import java.util.Optional;

public interface SaleRepository extends JpaRepository<Sale, Long> {
    @Query("SELECT SUM(s.price) FROM Sale s WHERE s.saleDate >= ?1 AND s.saleDate <= ?2")
    Optional<Float> findSumByStartAndEndDate(LocalDate startDate, LocalDate endDate);
}