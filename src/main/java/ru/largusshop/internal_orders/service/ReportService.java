package ru.largusshop.internal_orders.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.largusshop.internal_orders.clients.EntityClient;
import ru.largusshop.internal_orders.clients.ReportClient;
import ru.largusshop.internal_orders.model.StockReport;
import ru.largusshop.internal_orders.utils.Credentials;

import java.io.IOException;
import java.util.UUID;

@Service
public class ReportService {
    @Autowired
    private ReportClient reportClient;

    public StockReport getStockReportByDocWithId(UUID uuid) throws IOException {
        return reportClient.getReportStockBasedOnDocumentWithId(uuid, Credentials.KRUTILIN);
    }
}
