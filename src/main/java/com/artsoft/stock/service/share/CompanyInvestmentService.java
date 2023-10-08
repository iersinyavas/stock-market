package com.artsoft.stock.service.share;

import com.artsoft.stock.entity.Investment;
import com.artsoft.stock.entity.Share;
import com.artsoft.stock.repository.ShareRepository;
import com.artsoft.stock.util.BatchUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Random;

@Slf4j
@Service
@RequiredArgsConstructor
public class CompanyInvestmentService implements Payable {

    private final ShareRepository shareRepository;
    private final BatchUtil batchUtil;
    private Random random = new Random();
    @Override
    public void execute(Share share) {
        share = shareRepository.findByCode(share.getCode());
        BigDecimal investmentAmount = this.calculateInvestmentAmount(share);
        Investment investment = new Investment();
        investment.setInvestmentAmount(investmentAmount);
        investment.setReturnInvestmentRatio(random.nextInt(100)+1);
        investment.setPastDayInvestment(BigDecimal.ZERO);
        List<Investment> investmentList = share.getInvestmentList();
        investmentList.add(investment);
        share.setTotalInvestmentAmount(share.getTotalInvestmentAmount().add(investmentAmount));
        BigDecimal divide = BigDecimal.valueOf(investment.getReturnInvestmentRatio()).divide(BigDecimal.valueOf(100), 2, RoundingMode.FLOOR).multiply(investmentAmount);
        investment.setReturnInvestment(divide.multiply(BigDecimal.valueOf(random.nextInt(investment.getReturnInvestmentRatio().intValue())).add(BigDecimal.ONE))
                .divide(BigDecimal.valueOf(100), 2, RoundingMode.FLOOR));
        investment.setExpensesRatio(random.nextInt(investment.getReturnInvestmentRatio())+1);

        int sum = investmentList.stream().mapToInt(Investment::getReturnInvestmentRatio).sum();
        BigDecimal returnInvestmentAverage = BigDecimal.valueOf(sum).divide(BigDecimal.valueOf(investmentList.size()), 2, RoundingMode.FLOOR);
        share.setReturnInvestmentAverage(returnInvestmentAverage);
        shareRepository.save(share);
        batchUtil.setShare(share);
        log.info("{} şirketi {} liralık yatırım yaptı.", share.getCode(), investmentAmount);
    }

    private BigDecimal calculateInvestmentAmount(Share share){
        return share.getFund().subtract(share.getTotalInvestmentAmount()).divide(BigDecimal.valueOf(100)).multiply(BigDecimal.valueOf(random.nextInt(100)).add(BigDecimal.ONE));
    }
}
