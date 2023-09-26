package com.artsoft.stock.service;

import com.artsoft.stock.entity.Share;
import org.springframework.stereotype.Service;

@Service
public class DividendPaymentService implements Payable{
    @Override
    public void execute(Share share) {

    }
}
