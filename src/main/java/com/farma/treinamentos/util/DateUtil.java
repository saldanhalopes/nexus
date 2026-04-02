package com.farma.treinamentos.util;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

public class DateUtil {
    
    private static final DateTimeFormatter ISO_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
    
    public static LocalDateTime nowUtc() {
        return LocalDateTime.now(ZoneOffset.UTC);
    }
    
    public static String format(LocalDateTime dateTime) {
        return dateTime.format(ISO_FORMATTER);
    }
    
    public static LocalDateTime parse(String dateTime) {
        return LocalDateTime.parse(dateTime, ISO_FORMATTER);
    }
}