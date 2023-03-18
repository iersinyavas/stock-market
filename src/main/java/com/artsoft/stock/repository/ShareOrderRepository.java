package com.artsoft.stock.repository;

import com.artsoft.stock.dto.ShareOrderSummaryInfoForMatchDTO;
import com.artsoft.stock.entity.ShareOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface ShareOrderRepository extends JpaRepository<ShareOrder, Long> {
    @Query(value = "select new com.artsoft.stock.dto.ShareOrderSummaryInfoForMatchDTO(s.shareOrderStatus, sum(s.lot)) from ShareOrder s where (s.shareOrderStatus = 'BUY' and s.price >= :currentBuyPrice) or (s.shareOrderStatus = 'SELL' and s.price <= :currentSellPrice) and s.shareOrderType = 'LIMIT' " +
            "group by s.shareOrderStatus")
    List<ShareOrderSummaryInfoForMatchDTO> getSummaryInfoForMatch(BigDecimal currentSellPrice, BigDecimal currentBuyPrice);

    @Query(value = "select s from ShareOrder s where (s.shareOrderStatus = 'BUY' and s.price >= :currentBuyPrice) or (s.shareOrderStatus = 'SELL' and s.price <= :currentSellPrice) and s.shareOrderType = 'LIMIT'")
    List<ShareOrder> getShareOrderListForOpenSession(BigDecimal currentSellPrice, BigDecimal currentBuyPrice);

    @Modifying(clearAutomatically = true)
    @Query(value = "delete from ShareOrder s where s.shareOrderId in :idList", nativeQuery = true)
    void deleteAllByIdList(List<Long> idList);
}
