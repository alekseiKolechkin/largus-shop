package ru.largusshop.internal_orders.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.largusshop.internal_orders.clients.EntityClient;
import ru.largusshop.internal_orders.model.StockReport;
import ru.largusshop.internal_orders.model.Store;
import ru.largusshop.internal_orders.utils.Credentials;

import javax.swing.text.html.ListView;
import java.io.IOException;
import java.util.List;

@Service
public class StoreService {
    @Autowired
    private EntityClient entityClient;
    public List<Store> getAllStores() throws IOException, InterruptedException {
        return entityClient.downloadEntities("store", Store.class, Credentials.KRUTILIN, "");
    }
}
