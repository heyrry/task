package com.herry.task.repository.dto;

import java.io.Serializable;

/**
 * @author baofeng
 * @since 2025/5/7
 */
public class Result <T> implements Serializable {
    private boolean success;
    private String code;
    private String message;
    private T data;

    public Result() {
    }

    public Result(boolean success, String code, String message, T data) {
        this.success = success;
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public boolean isSuccess() {
        return this.success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getCode() {
        return this.code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMessage() {
        return this.message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return this.data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public static <T> Result<T> getSuccessResult(T v) {
        Result<T> result = new Result();
        result.setSuccess(true);
        result.setData(v);
        result.code = "200";
        return result;
    }

    public static <T> Result<T> getFailureResult(String code, String message) {
        Result<T> result = new Result();
        result.setSuccess(false);
        result.setCode(code);
        result.setMessage(message);
        return result;
    }

    public String toString() {
        return "Result{success=" + this.success + ", code='" + this.code + '\'' + ", message='" + this.message + '\'' + ", data=" + this.data + '}';
    }
}
