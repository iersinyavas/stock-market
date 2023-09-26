package com.artsoft.stock.entity;

import lombok.Data;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.List;

@Data
@Entity
@Table(name = "share_ownership", schema = "stock_market")
public class ShareOwnership {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "share_ownership_gen")
    @SequenceGenerator(name = "share_ownership_gen", sequenceName = "stock_market.share_ownership_seq", allocationSize = 1)
    private Long id;

    @Column(name = "share_code")
    private String shareCode;

    @Column(name = "have_lot")
    private BigDecimal haveLot;

    @Column(name = "current_have_lot")
    private BigDecimal currentHaveLot;

    @Column(name = "trader_behavior")
    private String traderBehavior;

    @Column(name = "prince_range_big")
    private BigDecimal princeRangeBig;

    @Column(name = "prince_range_small")
    private BigDecimal princeRangeSmall;

    /*@ManyToMany(mappedBy = "shareOwnershipList", fetch = FetchType.LAZY)
    private List<Trader> traderList;*/
}
