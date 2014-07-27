package mei.ricardo.pessoa.app.couchdb.modal.Monitoring;

import com.couchbase.lite.Document;

/**
 * Created by rpessoa on 26/07/14.
 */
public class MS_Battery extends MonitorSensor {
    public enum NOTIFICATIONTYPE {CRITICAL, LOW, NORMAL}

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
}
