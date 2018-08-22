package ru.largusshop.internal_orders.utils;

import org.apache.commons.lang3.StringUtils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class Encoder {
    public static String encode(String s) throws UnsupportedEncodingException {
        if (StringUtils.isEmpty(s)) {
            return s;
        }
        return URLEncoder.encode(s, "UTF-8");
    }
}
