package com.builderlinebr.smarttrainer.calculation;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class CalcTime {
    public String ConvertLongToTimeString(long interval) {
        long hours = TimeUnit.MILLISECONDS.toHours(interval) - (TimeUnit.MILLISECONDS.toHours(interval) / 24) * 24;
        long mins = TimeUnit.MILLISECONDS.toMinutes(interval) - (TimeUnit.MILLISECONDS.toMinutes(interval) / 60) * 60;
        long sec = TimeUnit.MILLISECONDS.toSeconds(interval) - (TimeUnit.MILLISECONDS.toSeconds(interval) / 60) * 60;
        // long milis = TimeUnit.MILLISECONDS.toMillis(interval) - (TimeUnit.MILLISECONDS.toMillis(interval) / 1000) * 1000;

        String h = Long.toString(hours);
        if (hours < 10) h = "0" + h;
        String m = Long.toString(mins);
        if (mins < 10) m = "0" + m;
        String s = Long.toString(sec);
        if (sec < 10) s = "0" + s;


        final StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(h).append(":").append(m).append(":").append(s);
        return stringBuilder.toString();
    }

    public String convertIntervalToTimeString(float interval) {

        int hours = (int) (interval / 3600);
        int mins =(int) ((interval - hours*3600)/60);
        int secs = (int) (interval - hours*3600 - mins*60);
        String h = Integer.toString(hours);
        if (hours < 10) h = "0" + h;
        String m = Integer.toString(mins);
        if (mins < 10) m = "0" + m;
        String s = Integer.toString(secs);
        if (secs < 10) s = "0" + s;
        final StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(h).append(":").append(m).append(":").append(s);
        return stringBuilder.toString();
    }

    public String convertLongToDate(long timestamp) {
        String result = "";
        Timestamp ts = new Timestamp(timestamp * 1000);


        Date date = new Date(ts.getTime());
        SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy");
        result = format.format(date);


        return result;
    }

    public long convertDateTimeToLong(String dateTime) {
        long result = 0;
        SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        try {
            Date parsedDate = format.parse(dateTime);
            result = parsedDate.getTime() / 1000L;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return result;
    }

    public long convertDateToLong(Date dateTime) {
        long result = 0;
        result = dateTime.getTime() / 1000L;
        return result;
    }

    public long convertTimeToLong(String time) {
        long result = 0;

        int hour = Integer.parseInt(time.substring(0, 2));
        int mins = Integer.parseInt(time.substring(3, 5));
        int sec = Integer.parseInt(time.substring(6));

        result = (hour * 3600 + mins * 60 + sec) * 1000;
        return result;
    }
}
