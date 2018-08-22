package ru.largusshop.internal_orders.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.largusshop.internal_orders.clients.EntityClient;
import ru.largusshop.internal_orders.model.CustomerOrder;
import ru.largusshop.internal_orders.model.Demand;
import ru.largusshop.internal_orders.utils.Credentials;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static java.util.Objects.nonNull;

@Service
public class CustomerOrderService {
    @Autowired
    private EntityClient entityClient;

    public CustomerOrder create(CustomerOrder customerOrder) throws Exception {
        return entityClient.createEntity("customerOrder", CustomerOrder.class, customerOrder, Credentials.KRUTILIN);
    }

    public CustomerOrder getById(UUID id) {
        return null;
    }

    public List<CustomerOrder> search(String searchString) throws IOException {
        return entityClient.findEntities("customerOrder", CustomerOrder.class, Credentials.KRUTILIN, "search=" + searchString);
    }

//    public List<CustomerOrder> filter(Pair<String, String>... parameterValueFilter) throws IOException {
//        String filter = "filter=" + Arrays.stream(parameterValueFilter).map(a -> a.getKey() + "=" + a.getValue()).collect(Collectors.joining(";"));
//        return entityClient.findEntities("customerOrder", CustomerOrder.class, Credentials.KRUTILIN, filter);
//    }

    public List<CustomerOrder> filter(String parameters) throws IOException {
//        if(nonNull(parameterValueFilter)) {
//             parameters = "filter=" + Arrays.stream(parameterValueFilter)
//                                            .map(a -> a.getKey() + "=" + a.getValue())
//                                            .collect(Collectors.joining(";")) + "&" + parameters;
//        }
        return entityClient.findEntities("customerOrder", CustomerOrder.class, Credentials.KRUTILIN, parameters);
    }

    public void update(UUID id, CustomerOrder customerOrder) throws IOException {
        entityClient.updateEntity("customerOrder", id.toString(), customerOrder, Credentials.KRUTILIN);
    }
}
