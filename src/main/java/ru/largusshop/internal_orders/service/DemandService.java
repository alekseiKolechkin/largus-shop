package ru.largusshop.internal_orders.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.largusshop.internal_orders.clients.EntityClient;
import ru.largusshop.internal_orders.model.CustomerOrder;
import ru.largusshop.internal_orders.model.Demand;
import ru.largusshop.internal_orders.model.TemplateCustomerOrder;
import ru.largusshop.internal_orders.utils.Credentials;

import java.io.IOException;
import java.util.UUID;

@Service
public class DemandService {
    @Autowired
    private EntityClient entityClient;

    public Demand create(Demand demand) throws Exception {
        return entityClient.createEntity("demand", Demand.class, demand, Credentials.KRUTILIN);
    }

    public Demand getTemplateBasedOnCustomerOrder(CustomerOrder customerOrder) throws Exception {
        return entityClient.getTemplateBasedOnDocument("demand",
                                                       Demand.class,
                                                       TemplateCustomerOrder.builder()
                                                                            .customerOrder(customerOrder)
                                                                            .build(),
                                                       Credentials.KRUTILIN);
    }
}
