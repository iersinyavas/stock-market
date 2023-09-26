package com.artsoft.stock.entity;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;

@Data
@Entity
@Table(name = "fund_increase", schema = "stock_market")
public class FundIncrease implements Serializable {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "fund_increase_gen")
    @SequenceGenerator(name = "fund_increase", sequenceName = "stock_market.fund_increase_seq", allocationSize = 1)
    private Long id;

    @Column(name = "code")
    private String code;

    @Column(name = "value")
    private String value;

    @Column(name = "description")
    private String description;
}
