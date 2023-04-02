package com.artsoft.stock.repository;

import com.artsoft.stock.entity.Trader;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface TraderRepository extends JpaRepository<Trader, Long> {

    @Query(value = "select t.traderId from Trader t where t.haveLot > 0 and t.balance >= :price")
    List<Long> getTraderListForOpenSession(BigDecimal price);

    @Query(value = "select t from Trader t where t.haveLot > 0 and t.balance >= :price and t.traderId in :traderIdList")
    List<Trader> getTraderListByTraderId(List<Long> traderIdList, BigDecimal price);

    @Query(value = "select t.traderId from Trader t where t.haveLot > 0 and t.cost <= :currentSellPrice")
    List<Trader> getTraderListWantOnlyBuy(BigDecimal currentSellPrice);

    @Query(value = "select t from Trader t")
    List<Trader> getTraderListByBrokerageFirm(String brokerageFirm);

    @Query(value = "select t.traderId from Trader t where t.haveLot > 0 and t.balance >= :price")
    List<Long> getTraderList(BigDecimal price);
}
