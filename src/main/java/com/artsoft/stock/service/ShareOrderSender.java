package com.artsoft.stock.service;

import com.artsoft.stock.entity.Share;
import com.artsoft.stock.entity.ShareOrder;

public interface ShareOrderSender {
    void sendShareOrderToStockMarket(Share share, ShareOrder shareOrder) throws InterruptedException;
}
