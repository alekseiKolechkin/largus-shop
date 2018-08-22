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
public class Product {
    public final static Uom UOM_KOMPL = Uom.builder().meta(Meta.builder()
                                                               .href("https://online.moysklad.ru/api/remap/1.1/entity/uom/0e1312ef-6029-11e8-9107-5048000a48fe")
                                                               .metadataHref("https://online.moysklad.ru/api/remap/1.1/entity/uom/metadata")
                                                               .mediaType("application/json")
                                                               .type("uom").build()).build();

    public final static Uom UOM_SHT = Uom.builder().meta(Meta.builder()
                                                             .href("https://online.moysklad.ru/api/remap/1.1/entity/uom/19f1edc0-fc42-4001-94cb-c9ec9c62ec10")
                                                             .metadataHref("https://online.moysklad.ru/api/remap/1.1/entity/uom/metadata")
                                                             .mediaType("application/json")
                                                             .type("uom").build()).build();
    private Meta meta;
    private String id;
    private String name;
    //    private String exceptionnnnnn;
    private String description;
    private String code;
    private Integer vat;
    private ProductFolder productFolder;
    private Uom uom;
    private Integer minPrice;
    private Price buyPrice;
    private List<Price> salePrices;
    private Counterparty supplier;
    private String article;
    private Float weight;
    private Float volume;
    private List<String> barcodes;
    private Integer minimumBalance;
    private Boolean isSerialTrackable;
}
