package ru.largusshop.internal_orders.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.largusshop.internal_orders.clients.EntityClient;
import ru.largusshop.internal_orders.model.Organization;
import ru.largusshop.internal_orders.utils.Credentials;

import java.io.IOException;
import java.util.List;

@Service
public class OrganizationService {
    @Autowired
    private EntityClient entityClient;

    public List<Organization> getAllOrganizations() throws IOException, InterruptedException {
        return entityClient.downloadEntities("organization", Organization.class, Credentials.KRUTILIN, "");
    }
}
