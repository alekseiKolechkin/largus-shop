package ru.largusshop.internal_orders.utils.exception;

import org.springframework.http.HttpStatus;

public enum AppError {

    VALIDATION_EXCEPTION(Level.WARNING, Component.APPLICATION, HttpStatus.BAD_REQUEST.value(),
                         HttpStatus.BAD_REQUEST, "error.application.validation.problem"),
    NOT_FOUND_EXCEPTION(Level.WARNING, Component.APPLICATION, HttpStatus.NOT_FOUND.value(),
                        HttpStatus.NOT_FOUND, "error.application.resource.not.found"),
    NOT_ALLOWED_EXCEPTION(Level.FATAL, Component.APPLICATION, HttpStatus.UNAUTHORIZED.value(),
                          HttpStatus.UNAUTHORIZED, "error.application.unauthorized"),
    ATTACHMENT_EXCEPTION(Level.ERROR, Component.ATTACHMENT, HttpStatus.INTERNAL_SERVER_ERROR.value(),
                         HttpStatus.INTERNAL_SERVER_ERROR, "error.attachment.excelInputOrOutputOperationFailed"),
    VERSIONING_EXCEPTION(Level.ERROR, Component.DB, HttpStatus.INTERNAL_SERVER_ERROR.value(),
                         HttpStatus.INTERNAL_SERVER_ERROR, "error.versioning.conflict"),
    APPLICATION_ERROR(Level.ERROR, Component.APPLICATION, HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST, "");

    private final Enum level;
    private final Enum component;
    private final int errorNumber;
    private final HttpStatus status;
    private final String message;

    AppError(Enum level, Enum component, int errorNumber, HttpStatus status, String message) {
        this.level = level;
        this.component = component;
        this.errorNumber = errorNumber;
        this.status = status;
        this.message = message;
    }

    public HttpStatus getStatus() {
        return status;
    }

    public Enum getLevel() {
        return level;
    }

    public Enum getComponent() {
        return component;
    }

    public String getMessage() {
        return message;
    }

    public int getErrNumber() {
        return errorNumber;
    }
}
