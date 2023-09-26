package com.artsoft.stock.service;

import com.artsoft.stock.entity.Share;
import com.artsoft.stock.repository.ShareRepository;
import com.artsoft.stock.repository.TraderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PaidFundIncreaseService implements Payable {

    private final TraderRepository traderRepository;
    private final ShareRepository shareRepository;
    @Override
    public void execute(Share share) {

    }
}
