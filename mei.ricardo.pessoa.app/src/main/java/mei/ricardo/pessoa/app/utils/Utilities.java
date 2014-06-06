package mei.ricardo.pessoa.app.utils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by rpessoa on 06/06/14.
 */
public abstract class Utilities {

    public static String ConvertTimestampToDateFormat(String time) {
        try {
            long timestamp = Long.parseLong(time) * 1000;
            Date date = new Date(timestamp);
            DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
            return dateFormat.format(date);
        } catch (Exception ex) {
            return "";
        }
    }
}
