package com.artsoft.stock.service;

import com.artsoft.stock.entity.Share;

public interface Payable {
    void execute(Share share);
}
