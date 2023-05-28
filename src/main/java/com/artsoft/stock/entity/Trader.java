package com.artsoft.stock.entity;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;

@Data
@Entity
//@JsonIgnoreProperties({"hibernateLazyInitializer", "handler", "shareOrder"}) // İlişkili tablolarda json oluştururken iç içe sonsuz döngüye girmemesi için
@Table(name = "trader", schema = "stock_market")
public class Trader implements Serializable {

    @Id
    @Column(name = "trader_id")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "trader_gen")
    @SequenceGenerator(name = "trader_gen", sequenceName = "stock_market.trader_seq", allocationSize = 1)
    private Long traderId;

    @Column(name = "name")
    private String name;

    @Column(name = "balance")
    private BigDecimal balance;

    @Column(name = "cost")
    private BigDecimal cost;

    @Column(name = "have_lot")
    private BigDecimal haveLot;

    @Column(name = "current_have_lot")
    private BigDecimal currentHaveLot;
}
