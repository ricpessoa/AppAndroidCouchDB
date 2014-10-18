package mei.ricardo.pessoa.app.couchdb.modal.Monitoring;

import android.graphics.drawable.Drawable;

import com.couchbase.lite.Document;

import java.util.ArrayList;

import mei.ricardo.pessoa.app.Application;
import mei.ricardo.pessoa.app.R;

/**
 * Created by rpessoa on 30-09-2014.
 */
public class MS_Shoe extends MonitorSensor {
    public boolean removed;


    public MS_Shoe(String pressed, String mac_address, String subType, String timestamp) throws Exception {
        super(mac_address, subType, timestamp);
        this.removed = Boolean.parseBoolean(pressed);
    }

    public MS_Shoe(Document document) throws Exception {
        super(document.getProperty("mac_address").toString(), document.getProperty("subtype").toString(), document.getProperty("timestamp").toString());
        this.removed = Boolean.parseBoolean(document.getProperty("removed").toString());
    }

    public boolean isRemoved() {
        return removed;
    }

    public Drawable getImage() {
        if (removed) {
            return Application.getmContext().getResources().getDrawable(R.drawable.ic_notification_danger);
        }
        return null;
    }

    public static ArrayList<MS_Shoe> getShoeByMacAddressSubtypeTimestamp(String macaddress, String timestamp, int i) {
        return null;
    }

    @Override
    public String toString() {
        return "The Shoe was removed";
    }

    public static String toStringStatic() {
        return "The Shoe was removed";
    }
}