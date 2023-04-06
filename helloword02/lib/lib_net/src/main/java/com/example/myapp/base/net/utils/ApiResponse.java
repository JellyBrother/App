package com.example.myapp.base.net.utils;

import com.google.gson.annotations.SerializedName;

public class ApiResponse<T> {
    //状态码
    @SerializedName(value = "code", alternate = {"resultCode", "errorCode", "status"})
    private long code;
    //信息
    @SerializedName(value = "msg", alternate = {"message", "resultInfo", "errorMsg", "error"})
    private String message;
    private String success;
    //数据
    private T data;

    public ApiResponse() {
    }

    public long getCode() {
        return code;
    }

    public void setCode(long code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public boolean isSuccess() {
        return NetUtil.isSuccess(code);
    }

    public String getSuccess() {
        return success;
    }

    public void setSuccess(String success) {
        this.success = success;
    }
}