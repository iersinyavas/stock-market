package com.artsoft.stock.repository;

import com.artsoft.stock.dto.ShareOrderSummaryInfoForMatchDTO;
import com.artsoft.stock.entity.ShareOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface ShareOrderRepository extends JpaRepository<ShareOrder, Long> {
    @Query(value = "select new com.artsoft.stock.dto.ShareOrderSummaryInfoForMatchDTO(s.price, s.shareOrderStatus, sum(s.lot)) from ShareOrder s where s.shareOrderStatus = 'SELL' and s.shareOrderType = 'LIMIT' " +
            "group by s.shareOrderStatus, s.price order by s.price desc")
    List<ShareOrderSummaryInfoForMatchDTO> getSummaryInfoForSellMatch();

    @Query(value = "select new com.artsoft.stock.dto.ShareOrderSummaryInfoForMatchDTO(s.price, s.shareOrderStatus, sum(s.lot)) from ShareOrder s where s.shareOrderStatus = 'BUY' and s.shareOrderType = 'LIMIT' " +
            "group by s.shareOrderStatus, s.price order by s.price desc")
    List<ShareOrderSummaryInfoForMatchDTO> getSummaryInfoForBuyMatch();

    @Query(value = "select s from ShareOrder s where s.price = :price and s.shareOrderType = 'LIMIT'")
    List<ShareOrder> getShareOrderListForSelectPrice(BigDecimal price);

    @Query(value = "delete from ShareOrder s where s.shareOrderId in :idList")
    void deleteAllByIdList(List<Long> idList);
}
