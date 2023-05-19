package com.artsoft.stock.repository;

import com.artsoft.stock.dto.ShareOrderSummaryInfoForMatchDTO;
import com.artsoft.stock.entity.ShareOrder;
import com.artsoft.stock.entity.SwapProcess;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface SwapProcessRepository extends JpaRepository<SwapProcess, Long> {

}
