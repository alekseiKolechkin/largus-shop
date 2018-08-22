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
public class Demand {
    UUID id;
    Meta meta;
    Positions positions;
    String name;
    String description;
    String moment;
    Boolean applicable;
    Boolean vatEnabled;
    Boolean vatIncluded;
    Organization organization;
    Supplier agent;
    Store store;
    CustomerOrder customerOrder;
}
