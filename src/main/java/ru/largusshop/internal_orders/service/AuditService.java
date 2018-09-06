package ru.largusshop.internal_orders.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.largusshop.internal_orders.clients.EntityClient;
import ru.largusshop.internal_orders.model.Audit;
import ru.largusshop.internal_orders.utils.Credentials;
import ru.largusshop.internal_orders.utils.exception.AppError;
import ru.largusshop.internal_orders.utils.exception.AppException;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@Service
public class AuditService {
    @Autowired
    private EntityClient entityClient;

    public List<Audit> auditCustomerOrder(UUID uuid) {
        try {
            return entityClient.audit("customerOrder", uuid, Credentials.KRUTILIN, "");
        } catch (IOException | InterruptedException e) {
            throw new AppException("Ошибка получения истории изменений заказа.  Document ID: " + uuid, AppError.APPLICATION_ERROR, e);
        }
    }
}
