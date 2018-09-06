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
public class Position {
    Float stock;
    Integer cost;
    Float inTransit;
    Float reserve;
    private UUID id;
    private Meta meta;
    private Float quantity;
    private Integer price;
    private Assortment assortment;
    private String group;
    private Long sum;
//    Integer quantity;

//    public String getName(){
//        if(nonNull(assortment)){
//            return assortment.getName();
//        }
//        return null;
//    }
//
//    public String getArticle(){
//        if(nonNull(assortment)){
////            return assortment.getCode();
//        }
//        return null;
//    }
//
//    public Integer getReserve(){
//        if(nonNull(assortment)){
////            return assortment.getReserve();
//        }
//        return null;
//    }
//
//    public Integer getInTransit(){
//        if(nonNull(assortment)){
////            return assortment.getInTransit();
//        }
//        return null;
//    }
//
//    public Integer getAvailable(){
//        if(nonNull(assortment)){
////            return assortment.getQuantity();
//        }
//        return null;
//    }
//
//    public Integer getStock(){
//        if(nonNull(assortment)){
//            return assortment.getStock();
//        }
//        return null;
//    }
//
//    public String getSupplier(){
//        if(nonNull(assortment)){
////            if(nonNull(assortment.getSupplierName())){
////                return assortment.getSupplierName();
////            }
////            if(nonNull(assortment.getProduct())){
////                if(nonNull(assortment.getProduct().getSupplier())){
////                    return assortment.getProduct().getSupplier().getName();
////                }
//            }
////        }
//        return null;
//    }
}
