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
    private Long shareCertificateNo;
    private ShareCode shareCode;
    private BigDecimal price;
    private BigDecimal beforePrice;

    private static Long nextShareCertificateNo = 0L;
    public ShareCertificate(ShareCode shareCode) {
        this.shareCertificateNo = nextShareCertificateNo();
        this.shareCode = shareCode;
    }

    public static Long nextShareCertificateNo(){
        return ++nextShareCertificateNo;
    }
}
