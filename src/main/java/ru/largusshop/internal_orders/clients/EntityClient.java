package ru.largusshop.internal_orders.clients;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.largusshop.internal_orders.model.Audit;
import ru.largusshop.internal_orders.model.TemplateCustomerOrder;
import ru.largusshop.internal_orders.service.EmailService;
import ru.largusshop.internal_orders.utils.Connector;
import ru.largusshop.internal_orders.utils.Credentials;
import ru.largusshop.internal_orders.utils.ExceptionHandler;
import ru.largusshop.internal_orders.utils.exception.AppException;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonReader;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

@Component
public class EntityClient {

    @Autowired
    private EmailService emailService;
    @Autowired
    private ExceptionHandler exceptionHandler;

    public <T> List<T> downloadEntities(String entityName, Class<T> entityType, Credentials credentials, String parameters) throws IOException, InterruptedException {
        int offset = 0;
        int counter = 0;
        List<T> entity = new ArrayList<>();
        while (true) {
            HttpURLConnection connection = Connector.getHttpURLConnection("/entity/" + entityName + "?" + parameters + "limit=100&offset=" + offset, "GET", credentials.getAuthStr());
            try (InputStream in = connection.getInputStream(); JsonReader rdr = Json.createReader(in)) {
                JsonArray rows = rdr.readObject().getJsonArray("rows");
                if (isNull(rows)) {
                    return new ArrayList<>();
                }
                if (rows.size() == 0) {
                    break;
                }
                List<T> parsed = JSON.parseArray(rows.toString(), entityType);
                entity.addAll(parsed);
            } catch (Exception e) {
                exceptionHandler.handleException(connection, e, entityName, "<no Id>", "downloadEntities");
                return null;
            }
            Thread.sleep(500L);
            offset += 100;
            counter++;
            System.out.println(counter);
        }
        return entity;
    }

    public <T> T getEntityById(String entityName, Class<T> entityType, String id, Credentials credentials, String parameters) throws IOException {
        HttpURLConnection connection = Connector.getHttpURLConnection("/entity/" + entityName + "/" + id + "?" + parameters, "GET", credentials.getAuthStr());
        try (InputStream in = connection.getInputStream(); JsonReader rdr = Json.createReader(in)) {
            JsonObject jsonObject = rdr.readObject();
            if (isNull(jsonObject)) {
                return null;
            }
            return JSON.parseObject(jsonObject.toString(), entityType);
        } catch (Exception e) {
            exceptionHandler.handleException(connection, e, entityName, id, "downloadById");
            return null;
        }
    }

    public <T> List<T> findEntities(String entityName, Class<T> entityType, Credentials credentials, String parameters) throws IOException {
        HttpURLConnection connection = Connector.getHttpURLConnection("/entity/" + entityName + "?" + parameters, "GET", credentials.getAuthStr());
        try (InputStream in = connection.getInputStream(); JsonReader rdr = Json.createReader(in)) {
            JsonArray rows = rdr.readObject().getJsonArray("rows");
            if (isNull(rows) || rows.size() == 0) {
                return null;
            }
            return JSON.parseArray(rows.toString(), entityType);
        } catch (Exception e) {
            exceptionHandler.handleException(connection, e, entityName, "", "findEntities");
            return null;
        }
    }

    public <T> void updateEntity(String entityName, String id, T entity, Credentials credentials) throws IOException {
        HttpURLConnection connection = Connector.getHttpURLConnection("/entity/" + entityName + "/" + id, "PUT", credentials.getAuthStr());
        try {
            OutputStreamWriter osw = new OutputStreamWriter(connection.getOutputStream(), StandardCharsets.UTF_8);
            String body = JSON.toJSONString(entity, SerializerFeature.DisableCircularReferenceDetect, SerializerFeature.PrettyFormat);
            osw.write(body);
            osw.flush();
            osw.close();
            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                LocalDateTime errorTime = LocalDateTime.now();
                InputStream errorStream = connection.getErrorStream();
                if (nonNull(errorStream)) {
                    BufferedReader rdr = new BufferedReader(new InputStreamReader(errorStream));
                    String error = rdr.lines().collect(Collectors.joining("<br>"));
                    String message = "Error while updating " + entityName + " with id: " + id;
                    emailService.sendEmail(errorTime + "<br>" + message + "<br>" + error, "clearbox204@gmail.com");
                    return;

                } else {
                    String error = "Unknown error while updating " + entityName + " with id: " + id;
                    String message = errorTime + "<br>" + error + "<br>" + connection.getResponseMessage() + "<br>" + connection.getResponseCode();
                    System.err.println(message);
                    emailService.sendEmail(message, "clearbox204@gmail.com");
                    return;
                }
            }
        } catch (Exception e) {
            String details = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) + " Error while entity updating " + entityName + " with id: " + id + " " + e.toString();
            System.err.println(details);
            emailService.sendEmail(details, "clearbox204@gmail.com");
            exceptionHandler.handleException(connection, e, entityName, "", "findEntities");
        }
    }

    public <T> T createEntity(String entityName, Class<T> entityType, T entity, Credentials credentials) throws IOException {
        HttpURLConnection connection = Connector.getHttpURLConnection("/entity/" + entityName + "?expand=positions.assortment.product", "POST", credentials.getAuthStr());
        try {
            OutputStreamWriter osw = new OutputStreamWriter(connection.getOutputStream(), StandardCharsets.UTF_8);
            String body = JSON.toJSONString(entity, SerializerFeature.DisableCircularReferenceDetect, SerializerFeature.PrettyFormat);
            osw.write(body);
            osw.flush();
            osw.close();
            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                LocalDateTime errorTime = LocalDateTime.now();
                InputStream errorStream = connection.getErrorStream();
                if (nonNull(errorStream)) {
                    BufferedReader rdr = new BufferedReader(new InputStreamReader(errorStream));
                    String error = rdr.lines().collect(Collectors.joining("<br>"));
                    String message = "Error while creating " + entityName + "<br>";
                    emailService.sendEmail(errorTime + "<br>" + message + "<br>" + error + "<br>Body: " + body, "clearbox204@gmail.com");
                    throw new AppException(errorTime + "<br>" + message + "<br>" + error + "<br>Body: " + body);

                } else {
                    String error = "Unknown error while creating " + entityName + " body: " + body;
                    String message = errorTime + "<br>" + error + "<br>" + connection.getResponseMessage() + "<br>" + connection.getResponseCode();
                    System.err.println(message);
                    emailService.sendEmail(message, "clearbox204@gmail.com");
                    throw new AppException(message);
                }
            }
            try (InputStream in = connection.getInputStream(); JsonReader rdr = Json.createReader(in)) {
                JsonObject jsonObject = rdr.readObject();
                if (isNull(jsonObject)) {
                    String message = "Response entity after creation is null. Entity name: " + entityName + " entity: " + entity.toString();
                    System.err.println(message);
                    emailService.sendEmail(message, "clearbox204@gmail.com");
                    throw new AppException(message);
                }
                return JSON.parseObject(jsonObject.toString(), entityType);
            } catch (Exception e) {
                exceptionHandler.handleException(connection, e, entityName, "", "findEntities");
                return null;
            }
        } catch (Exception e) {
            String details = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) + " Error while entity creating " + entityName + " " + e.toString();
            System.err.println(details);
            emailService.sendEmail(details, "clearbox204@gmail.com");
            exceptionHandler.handleException(connection, e, entityName, "", "findEntities");
            return null;
        }
    }

    //TODO: correct messages
    public <T> T getTemplateBasedOnDocument(String entityName, Class<T> entityType, TemplateCustomerOrder customerOrder, Credentials credentials) throws Exception {
        HttpURLConnection connection = Connector.getHttpURLConnection("/entity/" + entityName + "/new?expand=positions.assortment,positions.assortment.product", "PUT", credentials.getAuthStr());
        try {
            OutputStreamWriter osw = new OutputStreamWriter(connection.getOutputStream(), StandardCharsets.UTF_8);
            String body = JSON.toJSONString(customerOrder, SerializerFeature.DisableCircularReferenceDetect, SerializerFeature.PrettyFormat);
            osw.write(body);
            osw.flush();
            osw.close();
            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                LocalDateTime errorTime = LocalDateTime.now();
                InputStream errorStream = connection.getErrorStream();
                if (nonNull(errorStream)) {
                    BufferedReader rdr = new BufferedReader(new InputStreamReader(errorStream));
                    String error = rdr.lines().collect(Collectors.joining("<br>"));
                    String message = "Error while getting template " + entityName + "<br>";
                    System.err.println(message);
                    emailService.sendEmail(errorTime + "<br>" + message + "<br>" + error + "<br> body: " + body, "clearbox204@gmail.com");
                    throw new AppException(errorTime + "<br>" + message + "<br>" + error + "<br> body: " + body);

                } else {
                    String error = "Unknown error while getting template " + entityName + "<br>";
                    String message = errorTime + "<br>" + error + "<br>" + connection.getResponseMessage() + "<br>" + connection.getResponseCode() + "<br> body: " + body;
                    System.err.println(message);
                    emailService.sendEmail(message, "clearbox204@gmail.com");
                    throw new AppException(message);
                }
            }
            try (InputStream in = connection.getInputStream(); JsonReader rdr = Json.createReader(in)) {
                JsonObject jsonObject = rdr.readObject();
                if (isNull(jsonObject)) {
                    String message = "Response entity after temlate creation is null. Entity name: " + entityName + " entity: " + customerOrder.toString();
                    System.err.println(message);
                    emailService.sendEmail(message, "clearbox204@gmail.com");
                    throw new AppException(message);
                }
                return JSON.parseObject(jsonObject.toString(), entityType);
            } catch (Exception e) {
                exceptionHandler.handleException(connection, e, entityName, "", "getTemplateBasedOnDocument");
                throw e;
            }
        } catch (Exception e) {
            String details = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) + " Error while entity creating " + entityName + " " + e.toString();
            System.err.println(details);
            emailService.sendEmail(details, "clearbox204@gmail.com");
            throw e;
        }
    }

    public List<Audit> audit(String entityName, UUID entityId, Credentials credentials, String parameters) throws IOException, InterruptedException {
        int offset = 0;
        int counter = 0;
        List<Audit> entity = new ArrayList<>();
        while (true) {
            HttpURLConnection connection = Connector.getHttpURLConnection("/entity/" + entityName + "/" + entityId + "/audit?" + parameters + "limit=100&offset=" + offset, "GET", credentials.getAuthStr());
            try (InputStream in = connection.getInputStream(); JsonReader rdr = Json.createReader(in)) {
                JsonArray rows = rdr.readObject().getJsonArray("rows");
                if (isNull(rows)) {
                    return new ArrayList<>();
                }
                if (rows.size() == 0) {
                    break;
                }
                List<Audit> parsed = JSON.parseArray(rows.toString(), Audit.class);
                entity.addAll(parsed);
            } catch (Exception e) {
                exceptionHandler.handleException(connection, e, entityName, entityId.toString(), "audit");
                return null;
            }
            Thread.sleep(500L);
            offset += 100;
            counter++;
            System.out.println(counter);
        }
        return entity;
    }

}
