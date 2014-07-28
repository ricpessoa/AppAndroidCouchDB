package mei.ricardo.pessoa.app.couchdb.modal.Monitoring;

import android.graphics.drawable.Drawable;

import com.couchbase.lite.Document;

import mei.ricardo.pessoa.app.Application;
import mei.ricardo.pessoa.app.R;

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
}
