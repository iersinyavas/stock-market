package com.artsoft.stock.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Table(name = "share", schema = "stock_market")
public class Share implements Serializable {

    @Id
    @Column(name = "share_id")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "share_gen")
    @SequenceGenerator(name = "share_gen", sequenceName = "stock_market.share_seq", allocationSize = 1)
    private Long shareId;

    @Column(name = "code")
    private String code;

    @Column(name = "lot")
    private BigDecimal lot;

    @Column(name = "current_sell_price")
    private BigDecimal currentSellPrice;

    @Column(name = "current_buy_price")
    private BigDecimal currentBuyPrice;

    @Column(name = "open_sell_price")
    private BigDecimal openSellPrice;

    @Column(name = "open_buy_price")
    private BigDecimal openBuyPrice;

    @Column(name = "close_sell_price")
    private BigDecimal closeSellPrice;

    @Column(name = "close_buy_price")
    private BigDecimal closeBuyPrice;

    @Column(name = "max_price")
    private BigDecimal maxPrice;

    @Column(name = "min_price")
    private BigDecimal minPrice;
}
