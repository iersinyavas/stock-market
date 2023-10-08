package com.artsoft.stock.service;

import com.artsoft.stock.dto.CandleStick;
import com.artsoft.stock.dto.SwapProcessDTO;
import com.artsoft.stock.entity.Share;
import com.artsoft.stock.entity.SwapProcess;
import com.artsoft.stock.repository.SwapProcessRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class CandleStickService {

    private final SwapProcessRepository swapProcessRepository;
    private static int minute = 0;
    private static BigDecimal open = BigDecimal.ZERO;
    private static BigDecimal close = BigDecimal.ZERO;
    private static BigDecimal low = BigDecimal.ZERO;
    private static BigDecimal high = BigDecimal.ZERO;
    private static BigDecimal volume = BigDecimal.ZERO;

    protected final SimpMessagingTemplate template;
    ObjectMapper objectMapper = new ObjectMapper();
    MappingJackson2MessageConverter mappingJackson2MessageConverter = new MappingJackson2MessageConverter();

    public void saveAndSendSwapProcess(SwapProcess swapProcess, SwapProcessDTO swapProcessDTO, Share share) {
        template.setMessageConverter(mappingJackson2MessageConverter);
        CandleStick candleStick;
        if (Objects.isNull(share)){
            candleStick = this.setValue(swapProcessDTO, swapProcess);
        }else {
            candleStick = this.setValue(share);
        }

        String candleStickJson;
        try {
            candleStickJson = objectMapper.writeValueAsString(candleStick);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        template.convertAndSend("/topic/stock-chart", candleStickJson);
    }
    public CandleStick setValue(SwapProcessDTO swapProcessDTO, SwapProcess swapProcess){
        CandleStick candleStick = new CandleStick();
        /*DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        LocalDateTime formatDateTime = LocalDateTime.parse(now, formatter);*/
        swapProcess.setTransactionTime(LocalDateTime.now());

        LocalDateTime transactionTime = swapProcess.getTransactionTime();
        transactionTime = LocalDateTime.of(transactionTime.getYear(), transactionTime.getMonth(), transactionTime.getDayOfMonth(), transactionTime.getHour(), transactionTime.getMinute());
        swapProcessDTO.setTransactionTime(transactionTime);
        swapProcessRepository.save(swapProcess);

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
            //candleStick.setVolume(swapProcessDTO.getVolume());
        }else {
            candleStick.setDate(swapProcessDTO.getTransactionTime());
            high = (swapProcessDTO.getPrice().compareTo(high) > 0) ? swapProcessDTO.getPrice() : high;
            candleStick.setHigh(high);
            candleStick.setClose(swapProcessDTO.getPrice());
            low = (swapProcessDTO.getPrice().compareTo(low) < 0) ? swapProcessDTO.getPrice() : low;
            candleStick.setLow(low);
            candleStick.setOpen(open);
            //candleStick.setVolume(swapProcessDTO.getVolume().add(volume));
        }
        return candleStick;
    }

    public CandleStick setValue(Share share){
        SwapProcessDTO swapProcessDTO = new SwapProcessDTO();
        swapProcessDTO.setPrice(share.getPriceStep().getPrice());
        LocalDateTime transactionTime = LocalDateTime.now();
        transactionTime = LocalDateTime.of(transactionTime.getYear(), transactionTime.getMonth(), transactionTime.getDayOfMonth(), transactionTime.getHour(), transactionTime.getMinute());
        swapProcessDTO.setTransactionTime(transactionTime);
        CandleStick candleStick = new CandleStick();
        if (minute != swapProcessDTO.getTransactionTime().getMinute()){
            minute = swapProcessDTO.getTransactionTime().getMinute();
            open = swapProcessDTO.getPrice();
            high = swapProcessDTO.getPrice();
            low = swapProcessDTO.getPrice();
            close = swapProcessDTO.getPrice();
            candleStick.setDate(swapProcessDTO.getTransactionTime());
            candleStick.setOpen(swapProcessDTO.getPrice());
            candleStick.setLow(swapProcessDTO.getPrice());
            candleStick.setHigh(swapProcessDTO.getPrice());
            candleStick.setClose(swapProcessDTO.getPrice());
        }else {
            candleStick.setDate(swapProcessDTO.getTransactionTime());
            high = (swapProcessDTO.getPrice().compareTo(high) > 0) ? swapProcessDTO.getPrice() : high;
            candleStick.setHigh(high);
            candleStick.setClose(swapProcessDTO.getPrice());
            low = (swapProcessDTO.getPrice().compareTo(low) < 0) ? swapProcessDTO.getPrice() : low;
            candleStick.setLow(low);
            candleStick.setOpen(open);
        }
        return candleStick;
    }
}
