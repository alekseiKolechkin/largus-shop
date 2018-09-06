package ru.largusshop.internal_orders.controller;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.largusshop.internal_orders.model.Audit;
import ru.largusshop.internal_orders.model.CustomerOrder;
import ru.largusshop.internal_orders.model.Position;
import ru.largusshop.internal_orders.model.PositionDiff;
import ru.largusshop.internal_orders.model.Positions;
import ru.largusshop.internal_orders.service.AuditService;
import ru.largusshop.internal_orders.service.CustomerOrderService;
import ru.largusshop.internal_orders.service.InternalOrderService;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

@RestController
@RequestMapping("/internalOrder")
public class InternalOrderController {

    @Autowired
    private InternalOrderService internalOrderService;

    @Autowired
    private CustomerOrderService customerOrderService;

    @GetMapping("/processOrders")
    String processOrderd() throws Exception {
        return internalOrderService.createInternalOrdersFormCustomerOrders();
    }

    @GetMapping("/healthCheck")
    String healthCheck() {
        return "It's alive!";
    }
}
