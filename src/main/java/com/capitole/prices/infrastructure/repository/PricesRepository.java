package com.capitole.prices.infrastructure.repository;

import com.capitole.prices.domain.dto.Price;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.List;

public interface PricesRepository extends JpaRepository<Price, Serializable> {

    @Query(value= "SELECT p FROM Price p WHERE ((?1 BETWEEN p.startDate AND p.endDate) AND (p.productId=?2) AND (p.brandId=?3)) order by p.priority DESC")
    List<Price> findTopByProductIdAndBrandIdAndDateBetweenStartDateAndEndDate(Timestamp dateToFound, Long productId, Long brandId);
}

