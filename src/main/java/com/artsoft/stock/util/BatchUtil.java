package com.artsoft.stock.util;

import com.artsoft.stock.entity.Share;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

@Component
@Getter
@Setter
public class BatchUtil {

    @Value("${spring.batch.job.names}")
    private String jobName;

    @Value("${share.shareId}")
    private Long shareId;

    @Value("${traderId}")
    private Long traderId;

    private Share share;
}
