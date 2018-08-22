package ru.largusshop.internal_orders.utils.exception;

import org.springframework.http.HttpStatus;

import java.time.LocalDate;
import java.util.Map;

public class ExceptionInfo {
    private Enum level;
    private Enum component;
    private int errorNumber;
    private HttpStatus responseErrorCode;
    private String message;
    private String timeStamp;
    private Map<String, String> details;
    private String detailMessage;

    public ExceptionInfo() {
        this.component = Component.APPLICATION;
        this.level = Level.ERROR;
        setTimeStamp();
    }

    public Enum getLevel() {
        return level;
    }

    public void setLevel(Enum level) {
        this.level = level;
    }

    public Enum getComponent() {
        return component;
    }

    public void setComponent(Enum component) {
        this.component = component;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }

    public void setTimeStamp() {
        this.timeStamp = LocalDate.now().toString();
    }

    public int getErrorNumber() {
        return errorNumber;
    }

    public void setErrorNumber(int errorNumber) {
        this.errorNumber = errorNumber;
    }

    public HttpStatus getResponseErrorCode() {
        return responseErrorCode;
    }

    public void setResponseErrorCode(HttpStatus responseErrorCode) {
        this.responseErrorCode = responseErrorCode;
    }


    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Map<String, String> getDetails() {
        return details;
    }

    public void setDetailsMap(Map<String, String> details) {
        this.details = details;
    }

    public String getDetailMessage() {
        return detailMessage;
    }

    public void setDetailMessage(String detailMessage) {
        this.detailMessage = detailMessage;
    }
}
