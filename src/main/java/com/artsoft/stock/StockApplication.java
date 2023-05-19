package com.artsoft.stock;

import com.artsoft.stock.batch.ShareOrderCreator;
import com.artsoft.stock.batch.ShareOrderMatcher;
import com.artsoft.stock.batch.TreaderCreator;
import com.artsoft.stock.dto.ShareDTO;
import com.artsoft.stock.entity.Share;
import com.artsoft.stock.entity.ShareOrder;
import com.artsoft.stock.entity.Trader;
import com.artsoft.stock.repository.ShareOrderRepository;
import com.artsoft.stock.repository.ShareRepository;
import com.artsoft.stock.repository.TraderRepository;
import com.artsoft.stock.service.ShareService;
import com.artsoft.stock.util.GeneralEnumeration.*;
import com.artsoft.stock.util.RandomData;
import com.artsoft.stock.util.PriceStep;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.BlockingQueue;

@SpringBootApplication
@EnableSwagger2
@EnableBatchProcessing
@EnableScheduling
public class StockApplication /*implements CommandLineRunner */{

    private Random random;
    @Autowired
    public static ApplicationContext applicationContext;
    public static void main(String[] args) {
        applicationContext = SpringApplication.run(StockApplication.class, args);

    }

/*    @Override
    public void run(String... args) throws Exception {
        Share share = shareService.init(ShareCode.ALPHA.name());
        treaderCreator.start();
        shareOrderCreator.start();
        shareOrderMatcher.start();

        System.out.println();
    }*/

    @Bean
    public Docket api(){
        return new Docket(DocumentationType.SWAGGER_2)
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.artsoft.stock"))
                .build();
    }

}
