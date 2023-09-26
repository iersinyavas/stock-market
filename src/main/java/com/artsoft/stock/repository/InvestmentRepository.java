package com.artsoft.stock.repository;

import com.artsoft.stock.entity.Investment;
import com.artsoft.stock.entity.Trader;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InvestmentRepository extends JpaRepository<Investment, Long> {

}
