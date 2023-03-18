package com.artsoft.stock.entity;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;

@Data
@Entity
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

    @Column(name = "total_cost")
    private BigDecimal cost;

    @Column(name = "total_amount")
    private BigDecimal totalAmount;

    @Column(name = "have_lot")
    private BigDecimal haveLot;
}
