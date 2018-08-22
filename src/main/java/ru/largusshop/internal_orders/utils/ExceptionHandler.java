package ru.largusshop.internal_orders.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.largusshop.internal_orders.service.EmailService;
import ru.largusshop.internal_orders.utils.exception.AppError;
import ru.largusshop.internal_orders.utils.exception.AppException;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.stream.Collectors;

import static java.util.Objects.nonNull;

@Component
public class ExceptionHandler {

    @Autowired
    private EmailService emailService;

    private Boolean sendMail = true;

    public void handleException(HttpURLConnection connection, Exception e, String name, String id, String operation) {
        InputStream errorStream = connection.getErrorStream();
        LocalDateTime errorTime = LocalDateTime.now();
        if (nonNull(errorStream)) {
            BufferedReader rdr = new BufferedReader(new InputStreamReader(errorStream));
            String error = rdr.lines().collect(Collectors.joining("\n"));
            String details = e.toString();
            String message = errorTime + " Error while operation: " + operation + " with " + name + " with id " + id + "\n\nError: " + error + "/n/n" + details;
            System.err.println(message);
            if (sendMail) {
                emailService.sendEmail(message, "clearbox204@gmail.com");
            }
            throw new AppException(message, AppError.APPLICATION_ERROR, e);
        } else {
            String error = "Connection failed.";
            String details = errorTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) + " " + e.toString();
            String message = error + " Error while operation: " + operation + " with " + name + "with id " + id + "/n/n" + details;
            System.err.println(message);
            if (sendMail) {
                emailService.sendEmail(message, "clearbox204@gmail.com");
            }
            throw new AppException(message, AppError.APPLICATION_ERROR, e);
        }
    }
}
