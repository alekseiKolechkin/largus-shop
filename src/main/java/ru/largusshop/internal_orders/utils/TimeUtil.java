package ru.largusshop.internal_orders.utils;

import com.alibaba.fastjson.JSON;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class TimeUtil {
    public static LocalDateTime deserializeTime() throws IOException {
        File lastSyncTime = new File("lastSyncTime");
        if(!lastSyncTime.exists()) {
            return null;
        }
        String json = Files.readAllLines(lastSyncTime.toPath(), StandardCharsets.UTF_8)
                           .stream()
                           .reduce((a, b) -> a + b)
                           .orElse("");
        return JSON.parseObject(json, LocalDateTime.class);
    }

    public static void serializeTime(LocalDateTime time) throws IOException {
        File lastSyncTime = new File("lastSyncTime");
        try (FileWriter writer = new FileWriter(lastSyncTime)) {
            writer.write(JSON.toJSONString(time));
        }
    }

    public static String getStringFromTime(LocalDateTime time){
        return time.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }

    public static String getUrlEncodedFromTime(LocalDateTime time) throws UnsupportedEncodingException {
        return URLEncoder.encode(getStringFromTime(time), "UTF-8");
    }
}
