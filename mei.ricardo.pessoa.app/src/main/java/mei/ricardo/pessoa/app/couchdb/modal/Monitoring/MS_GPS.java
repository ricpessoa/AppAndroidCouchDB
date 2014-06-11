package mei.ricardo.pessoa.app.couchdb.modal.Monitoring;

import android.graphics.drawable.Drawable;

import mei.ricardo.pessoa.app.Application;
import mei.ricardo.pessoa.app.R;

/**
 * Created by rpessoa on 05/06/14.
 */
public class MS_GPS extends MonitorSensor{

    public static String[] NOTIFICATIONTYPE = {"CHECK-IN", "CHECK-OUT"};
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
    //show ms_gps on the map
    public MS_GPS(String address, String notification,String latit,String longi,String subtype, String timestamp) {
        super(subtype,timestamp);
        this.address = address;
        this.notification = notification;
        this.latitude = Float.parseFloat(latit);
        this.longitude = Float.parseFloat(longi);
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

    public Drawable getImage() {
        if(notification.equals(NOTIFICATIONTYPE[0])){
            return Application.getmContext().getResources().getDrawable(R.drawable.check_in_small);
        }else{
            return Application.getmContext().getResources().getDrawable(R.drawable.check_out_small);
        }
    }

}
