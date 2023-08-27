package com.artsoft.stock.repository;

import com.artsoft.stock.entity.Trader;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface TraderRepository extends JpaRepository<Trader, Long> {

    @Query(value = "select t.traderId from Trader t where t.traderId <> :traderId")
    List<Long> getTraderListForOpenSession(Long traderId);

    @Query(value = "select t.traderId from Trader t where t.traderId <> :traderId and t.traderId in :traderIdList")
    List<Long> getTraderIdListByTraderId(List<Long> traderIdList, Long traderId);

    @Query(value = "select t from Trader t where t.traderId in :traderIdList")
    List<Trader> getTraderListByTraderId(List<Long> traderIdList);

    @Query(value = "select t from Trader t where t.currentHaveLot = 0 and t.traderId <> :traderId")
    List<Trader> getTraderListByCurrentHaveLotEqualsZero(Long traderId);

}
