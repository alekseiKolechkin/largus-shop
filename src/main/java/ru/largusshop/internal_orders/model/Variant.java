package ru.largusshop.internal_orders.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Variant {
    private String name;
    private String code;
    private String externalCode;
    private Boolean archived;
    private List<Characteristic> characteristics;
    private Integer minPrice;
    private Price buyPrice;
    private List<Price> salePrices;
    private List<String> barcodes;
    private Product product;
}
