package com.artsoft.stock.repository;

import com.artsoft.stock.model.Customer;
import com.artsoft.stock.model.Share;
import com.artsoft.stock.model.ShareOrder;
import com.artsoft.stock.model.share.ShareCode;
import com.artsoft.stock.util.GeneralEnumeration.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class Database {

    public static Map<ShareCode, Share> shareMap = new HashMap<>();
    public static Map<String, Customer> customerMap = new HashMap<>();
    public static Map<ShareCode, Map<BigDecimal, Map<ShareOrderStatus, BlockingQueue<ShareOrder>>>> shareOrder = new HashMap<>();
    public static Map<String, BlockingQueue<ShareOrder>> processedShareOrders = new HashMap<>();

}
