package ru.largusshop.internal_orders.utils;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;

public class Connector {

    public String loginMakarov = "admin@largusspb";
    public String passMakarov = "c79391d268e0";
    public String loginKrut = "krasnodar@сиг";
    public String passKrut = "JSNYuI1hQ7";

    private static final String SKLAD_API = "https://online.moysklad.ru/api/remap/1.1";


    public static HttpURLConnection getHttpURLConnection(String requestUrl, String method, String authStr) throws IOException {
        URL url = new URL(SKLAD_API + requestUrl);
        String authEncoded = Base64.getEncoder().encodeToString(authStr.getBytes(Charset.forName("UTF8")));

        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod(method);
        connection.setDoOutput(true);
        connection.setRequestProperty("Authorization", "Basic " + authEncoded);
        connection.setRequestProperty("Content-Type", "application/json;charset=utf-8");
        connection.setRequestProperty("Accept", "application/json");
        System.err.println(LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MM--yyyy HH:mm:ss"))+"  get connection: " + method + " " + requestUrl);
        return connection;
    }

}
