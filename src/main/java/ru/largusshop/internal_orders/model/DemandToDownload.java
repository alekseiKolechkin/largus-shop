package ru.largusshop.internal_orders.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DemandToDownload {
    final Boolean applicable = false;
    UUID id;
    Meta meta;
    List<Position> positions;
    String name;
    String description;
    String moment;
    Boolean vatEnabled;
    Boolean vatIncluded;
    Organization organization;
    Supplier agent;
    MetaClass store;
    String deliveryPlannedMoment;
}
