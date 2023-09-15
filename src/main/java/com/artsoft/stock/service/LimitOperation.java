package com.artsoft.stock.service;

import com.artsoft.stock.entity.ShareOrder;

import java.util.concurrent.BlockingQueue;

public interface LimitOperation {
    void execute(BlockingQueue<ShareOrder> limitSellShareOrderQueue, BlockingQueue<ShareOrder> limitBuyShareOrderQueue) throws InterruptedException;
}
