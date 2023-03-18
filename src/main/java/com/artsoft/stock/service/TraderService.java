package com.artsoft.stock.service;

import com.artsoft.stock.entity.Share;
import com.artsoft.stock.entity.Trader;
import com.artsoft.stock.repository.ShareRepository;
import com.artsoft.stock.repository.TraderRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Random;

@Slf4j
@Service
public class TraderService {

    @Autowired
    private TraderRepository traderRepository;
    @Autowired
    private ShareRepository shareRepository;

    public void createTrader(Share share){
        Random random = new Random();
        String[] name = {"A","B","C","D","E","F","G","H","I","J","K","L","M","N","O","P","R","S","T","U","V","Y","Z","X","Q","W"};
        Trader trader = new Trader();
        StringBuilder nameBuilder = new StringBuilder();
        for (int i=0; i<random.nextInt(10)+1; i++){
            nameBuilder.append(name[random.nextInt(name.length)]);
        }
        trader.setName(nameBuilder.toString());
        trader.setBalance(new BigDecimal(1000));
        trader.setTotalAmount(BigDecimal.ZERO);

        trader.setHaveLot((trader.getBalance().divide(share.getCurrentBuyPrice()).compareTo(share.getLot()) <= 0)
                ? BigDecimal.valueOf(random.nextInt(trader.getBalance().divide(share.getCurrentBuyPrice()).intValue())+1)
                : BigDecimal.valueOf(random.nextInt(share.getLot().intValue())+1));

        share.setLot(share.getLot().subtract(trader.getHaveLot()));
        trader.setTotalAmount(trader.getTotalAmount().add(trader.getHaveLot().multiply(share.getOpenBuyPrice())));
        trader.setBalance(trader.getBalance().subtract(trader.getTotalAmount()));
        if (trader.getHaveLot().compareTo(BigDecimal.ZERO) == 0){
            trader.setCost(BigDecimal.ZERO);
        }else {
            trader.setCost(trader.getTotalAmount().divide(trader.getHaveLot()));
        }

        shareRepository.save(share);
        traderRepository.save(trader);
        log.info("Trader adı : {}", trader.getName());
    }

    public List<Trader> getTraderListByBrokerageFirm(String brokerageFirm){
        return traderRepository.getTraderListByBrokerageFirm(brokerageFirm);
    }
}
