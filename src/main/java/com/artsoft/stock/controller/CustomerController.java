package com.artsoft.stock.controller;

import com.artsoft.stock.model.HaveShareInformation;
import com.artsoft.stock.model.Portfolio;
import com.artsoft.stock.model.share.ShareCode;
import com.artsoft.stock.repository.Database;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/stock/customer")
public class CustomerController {

    @GetMapping("/allCustomer")
    public ResponseEntity<Map<String, Portfolio>> allCustomers(){
        return new ResponseEntity<Map<String, Portfolio>>(this.setCurrentTotalValue(), HttpStatus.OK);
    }

    public Map<String, Portfolio> setCurrentTotalValue(){
        Database.customerMap.keySet().forEach(customerName -> {
            HaveShareInformation haveShareInformation = Database.customerMap.get(customerName).getPortfolio().getHaveShareInformationMap().get(ShareCode.ALPHA);
            haveShareInformation.setCurrentTotalValue(haveShareInformation.getHaveShareLot()
                    .multiply(Database.shareMap.get(ShareCode.ALPHA)
                            .getCurrentSellPrice()).subtract(haveShareInformation.getTotalCost()));
        });

        Map<String, Portfolio> portfolioMap = new HashMap<>();
        Database.customerMap.keySet().forEach(customerName -> {
            portfolioMap.put(customerName, Database.customerMap.get(customerName).getPortfolio());
        });

        return portfolioMap;
    }
}
