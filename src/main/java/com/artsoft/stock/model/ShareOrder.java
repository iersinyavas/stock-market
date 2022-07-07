package com.artsoft.stock.model;

import com.artsoft.stock.exception.WrongLotInformationException;
import com.artsoft.stock.model.share.ShareCertificate;
import com.artsoft.stock.model.share.ShareCode;
import com.artsoft.stock.util.GeneralEnumeration.*;
import com.artsoft.stock.util.RandomData;
import com.artsoft.stock.util.SystemConstants;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Slf4j
public class ShareOrder {

    private Long id;
    private String customerName;
    protected BlockingQueue<ShareCertificate> lot;
    protected BigDecimal price;
    protected BigDecimal cost;
    private ShareCode shareCode;
    private Boolean isActive = Boolean.TRUE;
    protected ShareOrderStatus shareOrderStatus;
    private ShareOrderOperationStatus shareOrderOperationStatus;
    private ShareOrderType shareOrderType;
    @JsonIgnore
    protected BigDecimal tempLot = BigDecimal.ZERO;

    public ShareOrder(Share share, BigDecimal balance, HaveShareInformation haveShareInformation) throws WrongLotInformationException {
        this.customerName = Thread.currentThread().getName();
        this.shareCode = share.getShareCode();
        this.shareOrderStatus = ShareOrderStatus.values()[RandomData.shareOrderStatusIndex()];
        this.shareOrderType = ShareOrderType.values()[RandomData.shareOrderStatusIndex()];
        this.shareOrderOperationStatus = ShareOrderOperationStatus.CREATED;
    }

}
