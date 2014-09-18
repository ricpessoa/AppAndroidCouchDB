package mei.ricardo.pessoa.app.couchdb.modal.Monitoring;

import android.graphics.drawable.Drawable;

import com.couchbase.lite.Document;
import com.couchbase.lite.QueryEnumerator;
import com.couchbase.lite.QueryRow;

import java.util.ArrayList;
import java.util.Iterator;

import mei.ricardo.pessoa.app.Application;
import mei.ricardo.pessoa.app.R;
import mei.ricardo.pessoa.app.couchdb.modal.Device;

/**
 * Created by rpessoa on 05/06/14.
 */
public class MS_Temperature extends MonitorSensor {

    public enum NOTIFICATIONTYPE {LOW, RANGE, HIGH}

    ;
    private float value;
    private String notification;

    public MS_Temperature(String value, String notification, String mac_address, String subType, String timestamp) throws Exception {
        super(mac_address, subType, timestamp);
        this.value = Float.parseFloat(value);
        this.notification = notification;
    }

    public MS_Temperature(Document document) throws Exception {
        super(document.getProperty("mac_address").toString(), document.getProperty("subtype").toString(), document.getProperty("timestamp").toString());
        this.value = Float.parseFloat(document.getProperty("value").toString());
        this.notification = document.getProperty("notification").toString();
    }

    public float getValue() {
        return value;
    }

    public String getNotification() {
        return notification;
    }

    public Drawable getImage() {
        if (notification == null)
            return Application.getmContext().getResources().getDrawable(R.drawable.temp_red);

        if (notification.equals(NOTIFICATIONTYPE.RANGE.toString())) {
            return Application.getmContext().getResources().getDrawable(R.drawable.temp_green);
        } else if (notification.equals(NOTIFICATIONTYPE.LOW.toString())) {
            return Application.getmContext().getResources().getDrawable(R.drawable.temp_blue);
        } else {
            return Application.getmContext().getResources().getDrawable(R.drawable.temp_red);

        }
    }

    public static ArrayList<MS_Temperature> getSensorTemperatureByMacAddressTimestamp(String macAddress, String timestamp, int numberResults) {
        QueryEnumerator rowEnum = MonitorSensor.getMonitorSensorByMacAddressSubtypeTimestamp(macAddress, Device.DEVICESTYPE.temperature.toString(), timestamp, numberResults);
        ArrayList<MS_Temperature> ms_temperatureArrayList = new ArrayList<MS_Temperature>();
        for (Iterator<QueryRow> it = rowEnum; it.hasNext(); ) {
            QueryRow row = it.next();
            Document document = row.getDocument();
            MS_Temperature ms_temperature = null;
            try {
                ms_temperature = new MS_Temperature(document);
                ms_temperatureArrayList.add(ms_temperature);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return ms_temperatureArrayList;
    }

    public boolean isNecessaryNotify() {
        if (this.notification.equals(MS_Battery.NOTIFICATIONTYPE.RANGE.toString()))
            return false;
        return true;
    }
}
