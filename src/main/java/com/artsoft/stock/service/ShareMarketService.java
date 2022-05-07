package com.artsoft.stock.service;

import com.artsoft.stock.model.Share;
import com.artsoft.stock.model.ShareOrder;
import com.artsoft.stock.model.share.ShareCode;
import com.artsoft.stock.repository.Database;
import com.artsoft.stock.util.GeneralEnumeration.*;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Map;
import java.util.concurrent.BlockingQueue;

@Service
public class ShareMarketService {

    public void processedShareOrders(ShareOrder buyShareOrder, ShareOrder sellShareOrder) {
        buyShareOrder.setShareOrderOperationStatus(ShareOrderOperationStatus.PROCESSING);
        sellShareOrder.setShareOrderOperationStatus(ShareOrderOperationStatus.PROCESSING);

        if (buyShareOrder.getBeforeLot().compareTo(sellShareOrder.getBeforeLot()) > 0) {
            buyShareOrder.setBeforeLot(buyShareOrder.getBeforeLot().subtract(sellShareOrder.getBeforeLot()));
            sellShareOrder.setBeforeLot(BigDecimal.ZERO);

            buyShareOrder.setShareOrderOperationStatus(ShareOrderOperationStatus.REMAINING);
            sellShareOrder.setShareOrderOperationStatus(ShareOrderOperationStatus.REMOVE);

        } else if (buyShareOrder.getBeforeLot().compareTo(sellShareOrder.getBeforeLot()) < 0) {
            sellShareOrder.setBeforeLot(sellShareOrder.getBeforeLot().subtract(buyShareOrder.getBeforeLot()));
            buyShareOrder.setBeforeLot(BigDecimal.ZERO);

            buyShareOrder.setShareOrderOperationStatus(ShareOrderOperationStatus.REMOVE);
            sellShareOrder.setShareOrderOperationStatus(ShareOrderOperationStatus.REMAINING);

        } else {
            buyShareOrder.setBeforeLot(BigDecimal.ZERO);
            sellShareOrder.setBeforeLot(BigDecimal.ZERO);

            buyShareOrder.setShareOrderOperationStatus(ShareOrderOperationStatus.REMOVE);
            sellShareOrder.setShareOrderOperationStatus(ShareOrderOperationStatus.REMOVE);

        }
    }
}
