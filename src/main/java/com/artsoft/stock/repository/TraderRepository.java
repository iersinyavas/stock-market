package com.artsoft.stock.repository;

import com.artsoft.stock.entity.Trader;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface TraderRepository extends JpaRepository<Trader, Long> {

    @Query(value = "select t.traderId from Trader t where t.haveLot > 0 and t.balance >= :price and t.traderId <> :traderId")
    List<Long> getTraderListForOpenSession(BigDecimal price, Long traderId);

    @Query(value = "select t.traderId from Trader t where t.balance >= :price and t.traderId <> :traderId and t.traderId in :traderIdList")
    List<Long> getTraderListByTraderId(List<Long> traderIdList, Long traderId, BigDecimal price);

    @Query(value = "select t from Trader t where t.currentHaveLot = 0 and t.traderId <> :traderId")
    List<Trader> getTraderListByCurrentHaveLotEqualsZero(Long traderId);

}
