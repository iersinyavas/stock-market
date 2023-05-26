package com.artsoft.stock.controller;

import com.artsoft.stock.entity.ShareOrder;
import com.artsoft.stock.exception.InsufficientBalanceException;
import com.artsoft.stock.exception.InsufficientLotException;
import com.artsoft.stock.request.ShareOrderRequest;
import com.artsoft.stock.response.BaseResponse;
import com.artsoft.stock.service.ShareOrderService;
import com.artsoft.stock.service.StockMarketService;
import com.artsoft.stock.util.BatchUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.ws.rs.core.MediaType;

@RestController
@RequestMapping("/trader")
@RequiredArgsConstructor
@CrossOrigin
public class TraderController {

    private final BatchUtil batchUtil;
    private final StockMarketService stockMarketService;
    private final ShareOrderService shareOrderService;

    @PostMapping(value = "/send-share-order", produces = MediaType.APPLICATION_JSON)
    public ResponseEntity<BaseResponse<Boolean>> sendShareOrder(@RequestBody ShareOrderRequest shareOrderRequest) throws InterruptedException, InsufficientLotException, InsufficientBalanceException {
        ShareOrder shareOrder = shareOrderService.createShareOrder(shareOrderRequest);
        stockMarketService.sendShareOrderToStockMarket(batchUtil.getShare(), shareOrder);
        return ResponseEntity.ok(new BaseResponse<>(Boolean.TRUE));
    }
}

