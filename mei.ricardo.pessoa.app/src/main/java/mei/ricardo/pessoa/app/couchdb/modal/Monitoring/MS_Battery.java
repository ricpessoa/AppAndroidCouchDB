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
 * Created by rpessoa on 26/07/14.
 */
public class MS_Battery extends MonitorSensor {
    public enum NOTIFICATIONTYPE {CRITICAL, LOW, RANGE}

    private float value;
    private String notifification;

    public MS_Battery(String value, String notification, String mac_address, String subtype, String timestamp) {
        super(mac_address, subtype, timestamp);
        this.value = Float.parseFloat(value);
        this.notifification = notification;
    }

    public MS_Battery(Document document) throws Exception {
        super(document.getProperty("mac_address").toString(), document.getProperty("subtype").toString(), document.getProperty("timestamp").toString());
        this.value = Float.parseFloat(document.getProperty("value").toString());
        this.notifification = document.getProperty("notification").toString();
    }

    public float getValue() {
        return value;
    }

    public String getNotifification() {
        return notifification;
    }

    public Drawable getImage() {
        if (notifification == null)
            return Application.getmContext().getResources().getDrawable(R.drawable.bat_critical_small);

        if (notifification.equals(NOTIFICATIONTYPE.RANGE.toString())) {
            return Application.getmContext().getResources().getDrawable(R.drawable.bat_range_small);
        } else if (notifification.equals(NOTIFICATIONTYPE.CRITICAL.toString())) {
            return Application.getmContext().getResources().getDrawable(R.drawable.bat_critical_small);
        } else {
            return Application.getmContext().getResources().getDrawable(R.drawable.bat_low_small);
        }
    }

    public static ArrayList<MS_Battery> getSensorBatteryByMacAddressTimestamp(String macAddress, String timestamp, int numberResults) {
        QueryEnumerator rowEnum = MonitorSensor.getMonitorSensorByMacAddressSubtypeTimestamp(macAddress, Device.DEVICESTYPE.battery.toString(), timestamp, numberResults);
        ArrayList<MS_Battery> ms_batteryArrayList = new ArrayList<MS_Battery>();
        for (Iterator<QueryRow> it = rowEnum; it.hasNext(); ) {
            QueryRow row = it.next();
            Document document = row.getDocument();
            MS_Battery ms_battery = null;
            try {
                ms_battery = new MS_Battery(document);
                ms_batteryArrayList.add(ms_battery);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return ms_batteryArrayList;
    }
}
