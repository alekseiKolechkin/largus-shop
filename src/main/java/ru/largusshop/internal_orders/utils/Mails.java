package ru.largusshop.internal_orders.utils;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public enum Mails {
    ADMIN("clearbox204@gmail.com"),
//    MAKAROV("clearbox204@yandex,ru"),
    KRUTILIN("vit9232@yandex.ru");
    private String email;
    Mails(String email){
        this.email = email;
    }
    public String getMail(){
        return email;
    }
    public static List<String> getList(){
        return Stream.of(Mails.values()).map(Mails::getMail).collect(Collectors.toList());
    }
}
