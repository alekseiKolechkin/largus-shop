package ru.largusshop.internal_orders.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.NestedRuntimeException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import ru.largusshop.internal_orders.service.ExceptionMapper;
import ru.largusshop.internal_orders.utils.exception.AppException;
import ru.largusshop.internal_orders.utils.exception.ExceptionInfo;

@ControllerAdvice
public class GlobalExceptionHandler {


    @Autowired
    private ExceptionMapper exceptionMapper;

    @ExceptionHandler(AppException.class)
    public ResponseEntity<Object> handleCheckedException(AppException e) {
        String message = "Message: " + e.getMessage() + "\nException: \n" + e + "\n";
        System.err.println(message);
        return new ResponseEntity<>(message, e.getResponseErrorCode());
    }

    @ExceptionHandler(Throwable.class)
    public ResponseEntity<ExceptionInfo> handleGenericException(Throwable e) {
//        if (e instanceof NestedRuntimeException &&
//                ((NestedRuntimeException) e).getRootCause() instanceof AppException) {
//            return handleCheckedException((AppException) ((NestedRuntimeException) e).getRootCause());
//        }

        ExceptionInfo exceptionInfo = exceptionMapper.mapThrowableOnEntity(e);
        System.err.println("Message: " + e.getMessage() + "\nException: \n" + e + "\n");
        return new ResponseEntity<>(exceptionInfo, HttpStatus.valueOf(exceptionInfo.getResponseErrorCode().name()));
    }
}
