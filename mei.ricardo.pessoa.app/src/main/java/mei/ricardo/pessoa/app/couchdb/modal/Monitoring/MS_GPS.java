package mei.ricardo.pessoa.app.couchdb.modal.Monitoring;

/**
 * Created by rpessoa on 05/06/14.
 */
public class MS_GPS extends MonitorSensor{

    public enum NOTIFICATIONTYPE {CHECKIN, CHECKOUT};
    public float latitude;
    public float longitude;
    public String address;
    public String notification;

    //create ms_gps only to show in list (faster)
    public MS_GPS(String address, String notification,String subtype, String timestamp) {
        super(subtype,timestamp);
        this.address = address;
        this.notification = notification;
    }

    public String getNotification() {
        return notification;
    }

    public float getLatitude() {
        return latitude;
    }

    public float getLongitude() {
        return longitude;
    }

    public String getAddress() {
        return address;
    }
}
