package ru.largusshop.internal_orders.service;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import ru.largusshop.internal_orders.utils.exception.AppException;
import ru.largusshop.internal_orders.utils.exception.ExceptionInfo;

@Service
public class ExceptionMapper {

    public ExceptionInfo getInternalExceptionEntity(Exception exception, HttpStatus status) {
        ExceptionInfo entity = new ExceptionInfo();
        entity.setMessage(exception.getMessage());
        entity.setResponseErrorCode(status);
        return entity;
    }

    public ExceptionInfo getApplicationEntity(AppException e) {

        ExceptionInfo entity = new ExceptionInfo();

        entity.setMessage(e.getMessage());
        entity.setErrorNumber(e.getErrorNumber());
        entity.setComponent(e.getComponent());
        entity.setLevel(e.getLevel());
        entity.setResponseErrorCode(e.getResponseErrorCode());
        entity.setTimeStamp();

        return entity;
    }

    public ExceptionInfo mapThrowableOnEntity(Throwable throwable) {
        ExceptionInfo entity = new ExceptionInfo();
        entity.setMessage(throwable.getMessage());
        entity.setErrorNumber(HttpStatus.INTERNAL_SERVER_ERROR.value());
        entity.setResponseErrorCode(HttpStatus.INTERNAL_SERVER_ERROR);
        return entity;
    }
}
