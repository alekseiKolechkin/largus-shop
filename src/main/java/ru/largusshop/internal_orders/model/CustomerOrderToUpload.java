package ru.largusshop.internal_orders.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomerOrderToUpload {
    UUID id;
    Meta meta;
    List<Position> positions;
//    List<Attribute> attributes;
    String name;
    String description;
    String moment;
    final Boolean applicable = false;
    Boolean vatEnabled;
    Boolean vatIncluded;
    Organization organization;
    Supplier agent;
    State state;
}
