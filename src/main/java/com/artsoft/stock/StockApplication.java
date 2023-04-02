package com.artsoft.stock;

import com.artsoft.stock.batch.ShareOrderCreator;
import com.artsoft.stock.batch.ShareOrderMatcher;
import com.artsoft.stock.batch.TreaderCreator;
import com.artsoft.stock.entity.Share;
import com.artsoft.stock.entity.ShareOrder;
import com.artsoft.stock.entity.Trader;
import com.artsoft.stock.repository.ShareOrderRepository;
import com.artsoft.stock.repository.ShareRepository;
import com.artsoft.stock.repository.TraderRepository;
import com.artsoft.stock.service.ShareService;
import com.artsoft.stock.util.GeneralEnumeration;
import com.artsoft.stock.util.RandomData;
import com.artsoft.stock.util.PriceStep;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.BlockingQueue;

@SpringBootApplication
public class StockApplication implements CommandLineRunner {
    @Autowired
    private ShareOrderCreator shareOrderCreator;
    @Autowired
    private TreaderCreator treaderCreator;
    @Autowired
    private ShareOrderMatcher shareOrderMatcher;
    @Autowired
    private TraderRepository traderRepository;
    @Autowired
    private ShareRepository shareRepository;
    @Autowired
    private ShareOrderRepository shareOrderRepository;
    @Autowired
    private ShareService shareService;

    private Random random;
    public static void main(String[] args) {
        SpringApplication.run(StockApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        //Hissede bir fiyatta işlem gerçekleşmesi için emirdeki hissenin tamamı alınmalı mı yoksa parça parça alınabilir mi?
        //İşlem fiyatından ucuza satılan veya pahalıya alınanlar hemen işleme alındığında ücret farkıyla işlem nasıl gerçekleşir
        treaderCreator.start();
        shareOrderCreator.start();
        shareOrderMatcher.start();
    }
}
