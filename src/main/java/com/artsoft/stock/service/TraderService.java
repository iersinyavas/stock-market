package com.artsoft.stock.service;

import com.artsoft.stock.entity.Share;
import com.artsoft.stock.entity.Trader;
import com.artsoft.stock.repository.ShareRepository;
import com.artsoft.stock.repository.TraderRepository;
import com.artsoft.stock.util.RandomData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Random;

@Slf4j
@Service
public class TraderService {

    @Autowired
    private TraderRepository traderRepository;
    @Autowired
    private ShareRepository shareRepository;

    public Trader createTrader(Share share){
        Random random = new Random();
        String[] name = {"A","B","C","D","E","F","G","H","I","J","K","L","M","N","O","P","R","S","T","U","V","Y","Z","X","Q","W"};
        Trader trader = new Trader();
        StringBuilder nameBuilder = new StringBuilder();
        for (int i=0; i<random.nextInt(10)+1; i++){
            nameBuilder.append(name[random.nextInt(name.length)]);
        }
        trader.setName(nameBuilder.toString());
        trader.setBalance(new BigDecimal(500));

        BigDecimal randomLot = RandomData.randomLot(share.getLot());
        trader.setHaveLot(randomLot.compareTo(BigDecimal.valueOf(100L)) > 0 ? BigDecimal.valueOf(100L) : randomLot);
        trader.setCurrentHaveLot(trader.getHaveLot());
        trader.setCost(RandomData.randomShareOrderPrice(BigDecimal.ONE, BigDecimal.TEN));

        share.setLot(share.getLot().subtract(trader.getHaveLot()));
        shareRepository.save(share);
        traderRepository.save(trader);
        log.info("Trader adı : {}", trader.getName());
        return trader;
    }
}
