package com.artsoft.stock.entity;

import com.artsoft.stock.service.LimitShareOrderService;
import lombok.Data;

import javax.persistence.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Random;

@Data
@Entity
@Table(name = "investment", schema = "stock_market")
public class Investment {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "investment_gen")
    @SequenceGenerator(name = "investment_gen", sequenceName = "stock_market.investment_seq", allocationSize = 1)
    private Long id;

    @Column(name = "investment_amount")
    private BigDecimal investmentAmount;

    @Column(name = "return_investment_ratio")
    private Integer returnInvestmentRatio;

    @Column(name = "return_investment")
    private BigDecimal returnInvestment;

    @Column(name = "past_day_investment")
    private BigDecimal pastDayInvestment;

    @ManyToMany(mappedBy = "investmentList", fetch = FetchType.LAZY)
    private List<Share> shareList;
}
