package com.artsoft.stock.response;

public class BaseResponse<T> {
    private T data;
    private boolean success = true;

    public BaseResponse(T data) {
        this.data = data;
    }

    public BaseResponse(T data, boolean success) {
        this.data = data;
        this.success = success;
    }

    public BaseResponse() {
    }

    public BaseResponse(boolean success) {
        this.success = success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public T getData() {
        return this.data;
    }

    public void setData(T data) {
        this.data = data;
    }

}
