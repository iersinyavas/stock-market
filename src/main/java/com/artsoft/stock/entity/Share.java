package com.artsoft.stock.entity;

import com.artsoft.stock.util.PriceStep;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
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

    @Column(name = "current_lot")
    private BigDecimal currentLot;

    @Column(name = "price")
    private BigDecimal price;

    @Column(name = "max_price")
    private BigDecimal maxPrice;

    @Column(name = "min_price")
    private BigDecimal minPrice;

    @Column(name = "fund")
    private BigDecimal fund;

    @Column(name = "profit")
    private BigDecimal profit;

    @Column(name = "own_resources")
    private BigDecimal ownResources;

    @Column(name = "total_investment_amount")
    private BigDecimal totalInvestmentAmount;

    @Column(name = "market_value")
    private BigDecimal marketValue;

    @Column(name = "market_book_ratio")
    private BigDecimal marketBookRatio;

    @Column(name = "price_income_ratio")
    private BigDecimal priceIncomeRatio;

    @ManyToOne
    @JoinColumn(name = "fund_increase", referencedColumnName = "id")
    private FundIncrease fundIncrease;

    @ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinTable(name = "share_investment",
            joinColumns = @JoinColumn(name = "share_id", referencedColumnName = "share_id"),
            inverseJoinColumns = @JoinColumn(name = "investment_id", referencedColumnName = "id")
    )
    private List<Investment> investmentList;

    private transient PriceStep priceStep;

}
