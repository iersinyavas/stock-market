package com.artsoft.stock.entity;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

@Data
@Entity
//@JsonIgnoreProperties({"hibernateLazyInitializer", "handler", "shareOrder"}) // İlişkili tablolarda json oluştururken iç içe sonsuz döngüye girmemesi için ve olmayan fieldalara mapleme yapmasını engellemek için
@Table(name = "trader", schema = "stock_market")
public class Trader implements Serializable {

    // Alıcı direkt alım yapacak. Market - Limit farketmez. Alış yapacağı miktar bunu belirleyecek zaten.
    // Alış fiyatı da olmayacak. O anki fiyat neyse o fiyattan alacak. Ama diğer kademelere de alış emri koyabilecek. Yani aslında alış ve satış emri kesişenler arasında alım satım olacak.
    // Alış - satış lot miktarı random olarak belirlenecek. Bu random sayı = toplam şirket hisse sayısı / elinde kağıt olan trader sayısına bölümü  ile bulunacak.
       // Örneğin: 1000 hisse varsa 100 de trader varsa 1000/100=10 random(10) olacak. Hatta 0 denk gelmemesi için random(10)+1
       // Aynı şekilde satış ta böyle olacak ama ellerindeki hisse sayısına göre.
    // Alım yapacağı gün(tur) sayısı olacak. Gün sayısı random belirlenecek ama random sayı bişeylere dayanmalı.
    // Db de alıcı mı satıcı mı bilgisi olacak. Bunlar sabit olmayacak değişebilir.


    @Id
    @Column(name = "trader_id")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "trader_gen")
    @SequenceGenerator(name = "trader_gen", sequenceName = "stock_market.trader_seq", allocationSize = 1)
    private Long traderId;

    @Column(name = "name")
    private String name;

    @Column(name = "balance")
    private BigDecimal balance;

    @Column(name = "return_investment_ratio")
    private BigDecimal returnInvestmentRatio;

    @Column(name = "have_lot")
    private BigDecimal haveLot;

    @Column(name = "current_have_lot")
    private BigDecimal currentHaveLot;

    @Column(name = "trader_behavior")
    private String traderBehavior;

    @Column(name = "prince_range_big")
    private BigDecimal priceBuy;

    @Column(name = "prince_range_small")
    private BigDecimal priceSell;

    /*@ManyToMany(fetch = FetchType.LAZY, cascade = {CascadeType.REMOVE, CascadeType.REMOVE})
    @JoinTable(name = "trader_share_ownership",
            joinColumns = @JoinColumn(name = "trader_id", referencedColumnName = "trader_id"),
            inverseJoinColumns = @JoinColumn(name = "share_ownership_id", referencedColumnName = "id")
    )
    private List<ShareOwnership> shareOwnershipList;*/
}
