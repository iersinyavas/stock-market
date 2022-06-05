package com.artsoft.stock.service;

import com.artsoft.stock.model.*;
import com.artsoft.stock.model.share.ShareCertificate;
import com.artsoft.stock.model.thread.Customer;
import com.artsoft.stock.repository.Database;
import com.artsoft.stock.util.GeneralEnumeration.*;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.util.concurrent.BlockingQueue;


@Slf4j
public class ShareMarketService {

    Object lock = new Object();

    public void openLock(){
        synchronized (lock){
            lock.notify();
        }
    }

    public synchronized void processedShareOrders(Share share, BlockingQueue<ShareOrder> buyShareOrderStatusQueue, BlockingQueue<ShareOrder> sellShareOrderStatusQueue, ShareOrder buyShareOrder, ShareOrder sellShareOrder, Customer buyCustomer, Customer sellCustomer) throws InterruptedException {
        synchronized (lock){
            buyShareOrder.setShareOrderOperationStatus(ShareOrderOperationStatus.PROCESSING);
            sellShareOrder.setShareOrderOperationStatus(ShareOrderOperationStatus.PROCESSING);

            BlockingQueue<ShareCertificate> buyHaveShareCertificateQueue = Database.customerMap.get(buyShareOrder.getCustomerName()).getPortfolio().getHaveShareInformationMap().get(buyShareOrder.getShareCode()).getHaveShareLot();
            BlockingQueue<ShareCertificate> sellHaveShareCertificateQueue = sellShareOrder.getLot();
            BigDecimal cost = BigDecimal.ZERO;
            while (!sellShareOrder.getLot().isEmpty() && buyShareOrder.getLot().remainingCapacity() != 0){
                ShareCertificate shareCertificate = sellHaveShareCertificateQueue.take();
                shareCertificate.setBeforePrice(shareCertificate.getPrice());
                shareCertificate.setPrice(sellShareOrder.getPrice());
                buyHaveShareCertificateQueue.put(shareCertificate);
                buyShareOrder.getLot().put(shareCertificate);
                cost = cost.add(shareCertificate.getPrice());
            }
            if (sellShareOrder.getLot().isEmpty()){
                sellShareOrderStatusQueue.take();
            }
            if (buyShareOrder.getLot().remainingCapacity() == 0){
                buyShareOrderStatusQueue.take();
            }
            sellCustomer.getPortfolio().addBalance(cost);
        }

    }

    private void swapShare(Customer buyCustomer, Customer sellCustomer, ShareOrder shareOrder){

    }

}
