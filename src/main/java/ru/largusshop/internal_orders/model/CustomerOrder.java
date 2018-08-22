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
public class CustomerOrder extends MetaClass {
    UUID id;
    Positions positions;
    //    List<Attribute> attributes;
    String name;
    String description;
    String moment;
    Boolean applicable;
    Boolean vatEnabled;
    Boolean vatIncluded;
    Organization organization;
    Counterparty agent;
    State state;
}
