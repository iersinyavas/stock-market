package com.artsoft.stock.service.operation;

import com.artsoft.stock.entity.Share;
import com.artsoft.stock.entity.ShareOrder;
import com.artsoft.stock.exception.InsufficientBalanceException;

import java.util.concurrent.BlockingQueue;

public interface MarketOperation {
    Boolean execute(Share share, BlockingQueue<ShareOrder> shareOrderQueue, ShareOrder shareOrder) throws InterruptedException, InsufficientBalanceException;
}
