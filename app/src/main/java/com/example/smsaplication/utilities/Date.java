package com.example.smsaplication.utilities;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Date {

    public static String getDate(){
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
        LocalDateTime now = LocalDateTime.now();
        return now.toString();
    }
}
