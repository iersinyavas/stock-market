package com.artsoft.stock.entity;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "swap_process", schema = "stock_market")
public class SwapProcess implements Serializable {

    @Id
    @Column(name = "swap_process_id")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "swap_process_gen")
    @SequenceGenerator(name = "swap_process_gen", sequenceName = "stock_market.swap_process_seq", allocationSize = 1)
    private Long swapProcessId;

    @Column(name = "buyer")
    private String buyer;

    @Column(name = "seller")
    private String seller;

    @Column(name = "lot")
    private BigDecimal lot;

    @Column(name = "share_order_status")
    private String shareOrderStatus;

    @Column(name = "volume")
    private BigDecimal volume;

    @Column(name = "price")
    private BigDecimal price;

    @Column(name = "transaction_time")
    private LocalDateTime transactionTime;
}
