package com.artsoft.stock.model.share;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Slf4j
public class ShareCertificate {
    private Integer shareCertificateNo;
    private ShareCode shareCode;
    private BigDecimal price;
    private BigDecimal beforePrice;

    public ShareCertificate(ShareCode shareCode, Integer shareCertificateNo) {
        this.shareCertificateNo = shareCertificateNo;
        this.shareCode = shareCode;
    }

}
