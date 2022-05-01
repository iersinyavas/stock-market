package com.artsoft.stock.model;

import lombok.*;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@ToString
public class Customer {
    private String name;
    private Portfolio portfolio;
    private BigDecimal salary;
}
