package ru.largusshop.internal_orders.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Supplier {
    private Meta meta;
    private String id;
    private String name;
    private String email;
}
