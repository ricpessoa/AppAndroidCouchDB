package mei.ricardo.pessoa.app.couchdb.modal.Monitoring;

import android.graphics.drawable.Drawable;

import mei.ricardo.pessoa.app.Application;
import mei.ricardo.pessoa.app.R;

/**
 * Created by rpessoa on 05/06/14.
 */
public class MS_Temperature extends MonitorSensor{


    public enum NOTIFICATIONTYPE{LOW, RANGE,HIGH};
    private float value;
    private String notifification;

    public MS_Temperature(String value, String notification,String mac_address, String subType, String timestamp) {
        super(mac_address,subType,timestamp);
        this.value = Float.parseFloat(value);
        this.notifification = notification;
    }
    public float getValue() {
        return value;
    }
    public Drawable getImage() {
        if (notifification==null)
            return Application.getmContext().getResources().getDrawable(R.drawable.temp_red_small);

        if(!notifification.equals(NOTIFICATIONTYPE.RANGE.toString())) {
            return Application.getmContext().getResources().getDrawable(R.drawable.temp_red_small);
        }
        return  Application.getmContext().getResources().getDrawable(R.drawable.temp_green_small);
    }
}
