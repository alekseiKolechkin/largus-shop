package ru.largusshop.internal_orders.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.largusshop.internal_orders.service.CustomerOrderService;

import java.util.UUID;

@RestController
@RequestMapping("/customerOrder")
public class CustomerOrderController {
    @Autowired
    private CustomerOrderService customerOrderService;

    @GetMapping("{customerOrderId}/audit")
    public String audit(@PathVariable UUID customerOrderId) {
         if (customerOrderService.createCustomerOrderFromAudit(customerOrderId)) {
             return "Заказ покупателя создан";
         }
         return "Создание заказа не требуется";
    }
}
