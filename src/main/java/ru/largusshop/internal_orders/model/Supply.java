package ru.largusshop.internal_orders.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Supply extends MetaClass {
    UUID id;
    String name;
    Organization organization;
    Counterparty agent;
    Store store;
    Positions positions;
    Employee owner;
    Boolean applicable;
    Group group;
}
