package ru.largusshop.internal_orders.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Counterparty {
    private Meta meta;
    private  String name;
    private String description;
    private String code;
    private String created;
    private  String email;
    private String phone;
    private String actualAddress;
    private String companyType;
    private Employee owner;
    List<String> tags;
    List<Attribute> attributes;

}
