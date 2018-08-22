package ru.largusshop.internal_orders.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.largusshop.internal_orders.clients.EntityClient;
import ru.largusshop.internal_orders.model.Supply;
import ru.largusshop.internal_orders.utils.Credentials;

@Service
public class SupplyService {
    @Autowired
    private EntityClient entityClient;

    public Supply create(Supply supply) throws Exception {
        return entityClient.createEntity("supply", Supply.class, supply, Credentials.KRUTILIN);
    }
}
