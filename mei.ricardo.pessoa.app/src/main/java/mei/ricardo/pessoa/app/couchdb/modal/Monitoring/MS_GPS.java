package mei.ricardo.pessoa.app.couchdb.modal.Monitoring;

import android.graphics.drawable.Drawable;
import android.util.Log;

import com.couchbase.lite.CouchbaseLiteException;
import com.couchbase.lite.Document;
import com.couchbase.lite.Query;
import com.couchbase.lite.QueryEnumerator;
import com.couchbase.lite.QueryRow;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import mei.ricardo.pessoa.app.Application;
import mei.ricardo.pessoa.app.R;
import mei.ricardo.pessoa.app.couchdb.modal.Device;

/**
 * Created by rpessoa on 05/06/14.
 */
public class MS_GPS extends MonitorSensor {
    private static String TAG = MS_GPS.class.getCanonicalName();
    public static String[] NOTIFICATIONTYPE = {"CHECK-IN", "CHECK-OUT"};
    public float latitude;
    public float longitude;
    public String address;
    public String notification;

    //create ms_gps only to show in list (faster)
    public MS_GPS(String address, String notification, String mac_address, String subtype, String timestamp) {
        super(mac_address, subtype, timestamp);
        this.address = address;
        this.notification = notification;
    }

    //show ms_gps on the map
    public MS_GPS(String address, String notification, String latit, String longi, String mac_address, String subtype, String timestamp) {
        super(mac_address, subtype, timestamp);
        this.address = address;
        this.notification = notification;
        this.latitude = Float.parseFloat(latit);
        this.longitude = Float.parseFloat(longi);
    }

    public MS_GPS(Document document) throws Exception {
        super(document.getProperty("mac_address").toString(), document.getProperty("subtype").toString(), document.getProperty("timestamp").toString());
        this.address = document.getProperty("address").toString();
        this.notification = document.getProperty("notification").toString();
        this.latitude = Float.parseFloat(document.getProperty("latitude").toString());
        this.longitude = Float.parseFloat(document.getProperty("longitude").toString());
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
        if (notification.equals(NOTIFICATIONTYPE[0])) {
            return Application.getmContext().getResources().getDrawable(R.drawable.ic_gps_check_in);
        } else if(notification.equals(NOTIFICATIONTYPE[1])){
            return Application.getmContext().getResources().getDrawable(R.drawable.ic_gps_check_out);
        }else
            return Application.getmContext().getResources().getDrawable(R.drawable.ic_gps_location);

    }

    public static ArrayList<MS_GPS> getSensorGPSByMacAddressTimestamp(String macAddress, String timestamp, int numberResults) {
        QueryEnumerator rowEnum = MonitorSensor.getMonitorSensorByMacAddressSubtypeTimestamp(macAddress, Device.DEVICESTYPE.GPS.toString(), timestamp, numberResults);
        ArrayList<MS_GPS> ms_gpsArrayList = new ArrayList<MS_GPS>();
        for (Iterator<QueryRow> it = rowEnum; it.hasNext(); ) {
            QueryRow row = it.next();
            Document document = row.getDocument();
            MS_GPS ms_gps = null;
            try {
                ms_gps = new MS_GPS(document);
                ms_gpsArrayList.add(ms_gps);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return ms_gpsArrayList;
    }

    /**
     * this method show in listview/MAP the MS_GPS
     */
    public static ArrayList<MS_GPS> getSensorGPSByMacAddressAndSubtype(String macAddress, String subType, int limit) {
        com.couchbase.lite.View view = Application.getmCouchDBinstance().viewGetMonitorSensor;
        Query query = view.createQuery();
        ArrayList<MS_GPS> arrayList = new ArrayList<MS_GPS>();

        query.setStartKey(new Object[]{macAddress, subType, new HashMap()});
        query.setEndKey(new Object[]{macAddress, subType});
        query.setDescending(true);
        query.setLimit(limit);

        try {
            QueryEnumerator rowEnum = query.run();
            for (Iterator<QueryRow> it = rowEnum; it.hasNext(); ) {
                QueryRow row = it.next();
                Document document = row.getDocument();
                if (subType.equals(SUBTYPE.GPS.toString())) {
                    try {
                        MS_GPS ms_gps = new MS_GPS(document.getProperty("address").toString(), document.getProperty("notification").toString(), document.getProperty("latitude").toString(), document.getProperty("longitude").toString(), macAddress, subType, document.getProperty("timestamp").toString());
                        arrayList.add(ms_gps);
                    } catch (Exception ex) {
                        Log.e(TAG, "Error GPS not valid for some reason");
                    }
                }
            }
        } catch (CouchbaseLiteException e) {
            e.printStackTrace();
        }
        return arrayList;
    }

    @Override
    public String toString() {
        return "GPS: "+ notification+" - "+ address ;
    }
}
