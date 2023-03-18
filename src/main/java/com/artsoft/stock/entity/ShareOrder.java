package com.artsoft.stock.entity;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;

@Data
@Entity
@Table(name = "share_order", schema = "stock_market")
public class ShareOrder implements Serializable {

    @Id
    @Column(name = "share_order_id")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "share_gen")
    @SequenceGenerator(name = "share_order_gen", sequenceName = "stock_market.share_order_seq", allocationSize = 1)
    private Long shareOrderId;

    @ManyToOne
    @JoinColumn(name = "trader", referencedColumnName = "trader_id")
    private Trader trader;

    @Column(name = "price")
    private BigDecimal price;

    @Column(name = "lot")
    private BigDecimal lot;

    @Column(name = "volume")
    private BigDecimal volume;

    @Column(name = "share_order_status")
    private String shareOrderStatus;

    @Column(name = "share_order_type")
    private String shareOrderType;

    @Column(name = "create_time")
    private Timestamp createTime;
}
