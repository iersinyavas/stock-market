package com.artsoft.stock.service;

import com.artsoft.stock.util.BatchUtil;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

@Configuration
@AllArgsConstructor
@NoArgsConstructor
public abstract class BaseService {

    @Autowired
    protected BatchUtil batchUtil;
}
