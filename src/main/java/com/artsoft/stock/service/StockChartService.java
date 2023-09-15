package com.artsoft.stock.service;

import com.artsoft.stock.dto.CandleStick;
import com.artsoft.stock.dto.StockChart;
import com.artsoft.stock.entity.SwapProcess;
import com.artsoft.stock.repository.SwapProcessRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class StockChartService {

    private final SwapProcessRepository swapProcessRepository;

    public StockChart stockChartInfo(){
        List<SwapProcess> swapProcessList = swapProcessRepository.findAll();
        for(SwapProcess swapProcess : swapProcessList){
            LocalDateTime transactionTime = swapProcess.getTransactionTime();
            LocalDateTime setTransactionTime = LocalDateTime.of(transactionTime.getYear(), transactionTime.getMonth(), transactionTime.getDayOfMonth(), transactionTime.getHour(), transactionTime.getMinute(), 0);
            swapProcess.setTransactionTime(setTransactionTime);
        }
        Map<LocalDateTime, List<SwapProcess>> stockChartMap = swapProcessList.stream().collect(Collectors.groupingBy(SwapProcess::getTransactionTime));
        StockChart stockChart = new StockChart();
        List<CandleStick> candleStickList = new ArrayList<>();
        for (LocalDateTime localDateTime : stockChartMap.keySet()){
            CandleStick candleStick = new CandleStick();
            List<SwapProcess> dojiSwapProcessList = stockChartMap.get(localDateTime);
            BigDecimal volume = dojiSwapProcessList.stream().map(SwapProcess::getVolume).reduce(BigDecimal::add).get();
            //candleStick.setVolume(volume);
            SwapProcess swapProcess = dojiSwapProcessList.get(0);
            candleStick.setOpen(swapProcess.getPrice());
            swapProcess = dojiSwapProcessList.get(dojiSwapProcessList.size() - 1);
            candleStick.setClose(swapProcess.getPrice());
            BigDecimal high = dojiSwapProcessList.stream().map(SwapProcess::getPrice).max(Comparator.reverseOrder()).get();
            candleStick.setHigh(high);
            BigDecimal low = dojiSwapProcessList.stream().map(SwapProcess::getPrice).min(Comparator.reverseOrder()).get();
            candleStick.setLow(low);
            candleStick.setDate(localDateTime);
            candleStickList.add(candleStick);
        }

        candleStickList.sort(Comparator.comparing(CandleStick::getDate));
        stockChart.setCandleStickList(candleStickList);
        return stockChart;
    }
}
