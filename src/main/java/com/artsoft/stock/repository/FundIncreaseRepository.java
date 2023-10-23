package com.artsoft.stock.repository;

import com.artsoft.stock.entity.FundIncrease;
import com.artsoft.stock.entity.Investment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FundIncreaseRepository extends JpaRepository<FundIncrease, Long> {

}
