package com.artsoft.stock.service;

import com.artsoft.stock.entity.FundIncrease;
import com.artsoft.stock.repository.FundIncreaseRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class FundIncreaseService extends BaseService{

    private final FundIncreaseRepository fundIncreaseRepository;

    public FundIncrease getFundIncrease(Long id){
        return fundIncreaseRepository.findById(id).get();
    }


}
