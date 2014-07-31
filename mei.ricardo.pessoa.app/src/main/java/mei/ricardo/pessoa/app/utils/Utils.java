package mei.ricardo.pessoa.app.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by rpessoa on 24/07/14.
 * This class have some methods utilities in project
 */
public class Utils {

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

    public static boolean isNetworkAvailable(Context mContext) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public static long getTimestamp() {
        return System.currentTimeMillis() / 1000L;
    }
}
