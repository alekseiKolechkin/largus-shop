package ru.largusshop.internal_orders.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.largusshop.internal_orders.service.InternalOrderService;

@RestController
@RequestMapping("/internalOrder")
public class InternalOrderController {

    @Autowired
    InternalOrderService internalOrderService;

    @GetMapping("/processOrders")
    String processOrderd() throws Exception {
        return internalOrderService.createInternalOrdersFormCustomerOrders();
    }

    @GetMapping("/healthCheck")
    String healthCheck(){
        return "It's alive!";
    }
}
