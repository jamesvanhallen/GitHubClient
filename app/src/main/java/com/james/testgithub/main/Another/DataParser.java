package com.james.testgithub.main.Another;

/**
 * Created by james on 03.02.15.
 */
import java.text.SimpleDateFormat;
import java.util.Date;

public class DataParser {

    public static final int FORMAT_ONLY_DATE = 1000;
    public static final int FORMAT_ONLY_TIME = 1001;
    public static final int FORMAT_DATETIME_SL = 1002;
    public static final int FORMAT_DATETIME_NL = 1003;

    public static String formatDate(Date date, int formatType) {
        SimpleDateFormat dateFormat;
        switch(formatType) {
            case FORMAT_ONLY_DATE:
                dateFormat = new SimpleDateFormat("yyyy.MM.dd");
                break;
            case FORMAT_ONLY_TIME:
                dateFormat = new SimpleDateFormat("HH:mm");
                break;
            case FORMAT_DATETIME_NL:
                dateFormat = new SimpleDateFormat("yyyy.MM.dd\nHH:mm");
                break;
            default:
                dateFormat = new SimpleDateFormat("yyyy.MM.dd HH:mm");
                break;
        }
        return dateFormat.format(date);
    }

}
