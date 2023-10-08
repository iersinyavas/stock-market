package com.artsoft.stock.service.share;

import com.artsoft.stock.entity.Share;

public interface Payable {
    void execute(Share share);
}
