package com.artsoft.stock.service;

@FunctionalInterface
public interface Controllable {
    <T> T control(T t);
}
