package ru.largusshop.internal_orders.clients;

import com.alibaba.fastjson.JSON;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.largusshop.internal_orders.model.StockReport;
import ru.largusshop.internal_orders.utils.Connector;
import ru.largusshop.internal_orders.utils.Credentials;
import ru.largusshop.internal_orders.utils.ExceptionHandler;
import ru.largusshop.internal_orders.utils.exception.AppError;
import ru.largusshop.internal_orders.utils.exception.AppException;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.util.UUID;

import static java.util.Objects.isNull;

@Component
public class ReportClient {

    @Autowired
    private ExceptionHandler exceptionHandler;

    public StockReport getReportStockBasedOnDocumentWithId(UUID documentId, Credentials credentials) throws IOException {
        HttpURLConnection connection = Connector.getHttpURLConnection("/report/stock/byoperation?operation.id=" + documentId, "GET", credentials.getAuthStr());
        try (InputStream in = connection.getInputStream(); JsonReader rdr = Json.createReader(in)) {
            JsonObject jsonObject = rdr.readObject();
            if (isNull(jsonObject)) {
                throw new AppException("Ошибка получения остатков товаров по документу, ответ null. Document ID: " + documentId);
            }
            return JSON.parseObject(jsonObject.toString(), StockReport.class);
        } catch (IOException e) {
            exceptionHandler.handleException(connection, e, "report", documentId.toString(), "getReportById");
            throw new AppException("Ошибка получения остатков товаров по документу.  Document ID: " + documentId, AppError.APPLICATION_ERROR, e);
        }
    }
}
