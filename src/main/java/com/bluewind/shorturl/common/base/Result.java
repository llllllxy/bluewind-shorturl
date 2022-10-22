package com.bluewind.shorturl.common.base;

import com.bluewind.shorturl.common.consts.HttpStatus;
import org.slf4j.MDC;

import java.io.Serializable;

/**
 * @author liuxingyu01
 * @date 2022-03-11-16:58
 **/
public class Result implements Serializable {
    private static final long serialVersionUID = -1491499610241557029L;

    private Integer code;
    private String msg;
    private Object data;

    private String traceId;

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public String getTraceId() {
        return traceId;
    }

    public void setTraceId(String traceId) {
        this.traceId = traceId;
    }

    public Result(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
        this.traceId = MDC.get("traceId");
    }

    public Result(Integer code, String msg, Object data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
        this.traceId = MDC.get("traceId");
    }

    public static Result ok(String msg, Object data) {
        return new Result(HttpStatus.SUCCESS, msg, data);
    }

    public static Result ok(String msg) {
        return new Result(HttpStatus.SUCCESS, msg, null);
    }

    public static Result error(String msg) {
        return new Result(HttpStatus.ERROR, msg);
    }

    public static Result create(Integer code, String msg, Object data) {
        return new Result(code, msg, data);
    }

    public static Result create(Integer code, String msg) {
        return new Result(code, msg);
    }
}
