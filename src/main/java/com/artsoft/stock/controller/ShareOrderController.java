package com.artsoft.stock.controller;

import com.artsoft.stock.service.ShareOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/share-order")
public class ShareOrderController {

    @Autowired
    private ShareOrderService shareOrderService;

}
