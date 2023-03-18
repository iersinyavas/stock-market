package com.artsoft.stock.service;

import com.artsoft.stock.entity.Share;
import com.artsoft.stock.repository.ShareRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class ShareService {

    @Autowired
    private ShareRepository shareRepository;

    public void closingSession(){
        Share share = shareRepository.findById(1L).get();
        share.setCloseSellPrice(share.getCurrentSellPrice());
        share.setCloseBuyPrice(share.getCurrentBuyPrice());
        share.setOpenSellPrice(share.getCloseSellPrice());
        share.setOpenBuyPrice(share.getCloseBuyPrice());
        share.setMaxPrice(share.getCloseBuyPrice().add(share.getCloseBuyPrice().divide(BigDecimal.TEN)));
        share.setMinPrice(share.getCloseBuyPrice().subtract(share.getCloseBuyPrice().divide(BigDecimal.TEN)));
    }

    public void openSession(){

    }
}
