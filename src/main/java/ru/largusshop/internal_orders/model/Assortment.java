package ru.largusshop.internal_orders.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Assortment {
    private Meta meta;
    private String name;
    private String code;
//    private Supplier supplier;
    private Price buyPrice;
//    private Integer reserve;
//    private Integer inTransit;
//    private Integer quantity;
    private Integer stock;
    private Product product;
//    private Float weight;

//    public String getSupplierName(){
//        if(nonNull(supplier)){
//            return supplier.getName();
//        }
//        return null;
//    }
}
