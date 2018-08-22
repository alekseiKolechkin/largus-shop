package ru.largusshop.internal_orders.utils;

public enum  Credentials {
    MAKAROV("admin@largusspb:c79391d268e0"),
    KRUTILIN("krasnodar@сиг:JSNYuI1hQ7");
    private String authStr;
    Credentials(String authStr){
        this.authStr = authStr;
    }

    public String getAuthStr() {
        return authStr;
    }
}
