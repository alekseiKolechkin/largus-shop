package ru.largusshop.internal_orders.utils.exception;

import org.springframework.http.HttpStatus;

import static java.util.Objects.nonNull;

public class AppException extends RuntimeException {

    private final Enum level;
    private final Enum component;
    private final int errorNumber;
    private final HttpStatus responseErrorCode;
    private final String message;

    public AppException(String message, AppError error, Throwable cause) {
        super(message, cause);
        String causeMessage = nonNull(cause) ? "\nError message: " + cause.getMessage() : "";
        this.level = error.getLevel();
        this.component = error.getComponent();
        this.responseErrorCode = error.getStatus();
        this.errorNumber = error.getErrNumber();
        this.message = message + causeMessage;
    }

    public AppException(ExceptionInfo entity) {
        super(entity.getMessage());
        this.errorNumber = entity.getErrorNumber();
        this.responseErrorCode = entity.getResponseErrorCode();
        this.level = entity.getLevel();
        this.component = entity.getComponent();
        this.message = entity.getMessage();
    }

    public AppException(String message, AppError error) {
        this(message, error, null);
    }

    public AppException(AppError error, Throwable cause) {
        this(error.getMessage(), error, cause);
    }

    public AppException(AppError error) {
        this(error.getMessage(), error, null);
    }

    public AppException(String message) {
        this(message, AppError.APPLICATION_ERROR);
    }

    public Enum getLevel() {
        return level;
    }

    public Enum getComponent() {
        return component;
    }

    public int getErrorNumber() {
        return errorNumber;
    }

    public HttpStatus getResponseErrorCode() {
        return responseErrorCode;
    }

    @Override
    public String getMessage() {
        return message;
    }

}
