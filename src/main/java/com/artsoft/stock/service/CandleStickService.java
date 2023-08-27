package com.artsoft.stock.service;

import com.artsoft.stock.dto.CandleStick;
import com.artsoft.stock.dto.SwapProcessDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Slf4j
@Service
@RequiredArgsConstructor
public class CandleStickService {

    private static int minute = 0;
    private static BigDecimal open = BigDecimal.ZERO;
    private static BigDecimal close = BigDecimal.ZERO;
    private static BigDecimal low = BigDecimal.ZERO;
    private static BigDecimal high = BigDecimal.ZERO;
    private static BigDecimal volume = BigDecimal.ZERO;
    public CandleStick setValue(SwapProcessDTO swapProcessDTO){
        CandleStick candleStick = new CandleStick();
        /*DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        LocalDateTime formatDateTime = LocalDateTime.parse(now, formatter);*/
        if (minute != swapProcessDTO.getTransactionTime().getMinute()){
            minute = swapProcessDTO.getTransactionTime().getMinute();
            open = swapProcessDTO.getPrice();
            high = swapProcessDTO.getPrice();
            low = swapProcessDTO.getPrice();
            close = swapProcessDTO.getPrice();
            volume = swapProcessDTO.getVolume();
            candleStick.setDate(swapProcessDTO.getTransactionTime());
            candleStick.setOpen(swapProcessDTO.getPrice());
            candleStick.setLow(swapProcessDTO.getPrice());
            candleStick.setHigh(swapProcessDTO.getPrice());
            candleStick.setClose(swapProcessDTO.getPrice());
            candleStick.setVolume(swapProcessDTO.getVolume());
        }else {
            candleStick.setDate(swapProcessDTO.getTransactionTime());
            high = (swapProcessDTO.getPrice().compareTo(high) > 0) ? swapProcessDTO.getPrice() : high;
            candleStick.setHigh(high);
            candleStick.setClose(swapProcessDTO.getPrice());
            low = (swapProcessDTO.getPrice().compareTo(low) < 0) ? swapProcessDTO.getPrice() : low;
            candleStick.setLow(low);
            candleStick.setOpen(open);
            candleStick.setVolume(swapProcessDTO.getVolume().add(volume));
        }
        return candleStick;
    }
}
