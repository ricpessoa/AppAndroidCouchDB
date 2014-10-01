package mei.ricardo.pessoa.app.couchdb.modal;

import android.util.Log;

import com.couchbase.lite.CouchbaseLiteException;
import com.couchbase.lite.Document;
import com.couchbase.lite.Query;
import com.couchbase.lite.QueryEnumerator;
import com.couchbase.lite.QueryRow;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import mei.ricardo.pessoa.app.Application;
import mei.ricardo.pessoa.app.couchdb.CouchDB;

/**
 * Created by rpessoa on 12/07/14.
 */
public class Safezone {
    private static String TAG = Safezone.class.getCanonicalName();
    public static String type = "safezone";

    public static String[] typeNotifications = {"NONE", "CHECK_INS_ONLY", "CHECK_OUTS_ONLY", "ALL"};

    private String _id;
    private String address;
    private String name;
    //why double?? because double have 64 bits compared by float 32 bits //http://www.java-samples.com/showtutorial.php?tutorialid=261
    private double latitude;
    private double longitude;
    private int radius;
    private String notification; //[CHECK-IN CHECK-OUT OR BOTH]
    private String timestamp;
    private String device;

    public Safezone(String address, String name, String latitude, String longitude, String radius, String notification, String timestamp, String device) {
        this.address = address;
        this.name = name;
        this.latitude = Float.parseFloat(latitude);
        this.longitude = Float.parseFloat(longitude);
        this.radius = Integer.parseInt(radius);
        this.notification = notification;
        this.timestamp = timestamp;
        this.device = device;
    }

    public Safezone(String _id, String address, String name, String latitude, String longitude, String radius, String notification, String timestamp, String device) {
        this._id = _id;
        this.address = address;
        this.name = name;
        this.latitude = Double.parseDouble(latitude);
        this.longitude = Double.parseDouble(longitude);
        this.radius = Integer.parseInt(radius);
        this.notification = notification;
        this.timestamp = timestamp;
        this.device = device;
    }

    public String get_id() {
        return _id;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public int getRadius() {
        return radius;
    }

    public void setRadius(int radius) {
        this.radius = radius;
    }

    public String getNotification() {
        return notification;
    }

    public int getNotificationPosition() {
        if (getNotification().equals(typeNotifications[1]))
            return 1;
        else if (getNotification().equals(typeNotifications[2]))
            return 2;
        else if (getNotification().equals(typeNotifications[3]))
            return 3;
        return 0;
    }

    public void setNotification(String notification) {
        this.notification = notification;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getDevice() {
        return device;
    }

    public void setDevice(String device) {
        this.device = device;
    }

    public static ArrayList<Safezone> getSafezonesOfDeviceGPS(String macAddress) {
        //CouchDB.viewGetSafezonesToFilterWithMacAddress;
        com.couchbase.lite.View view = Application.getmCouchDBinstance().viewGetSafezonesToFilterWithMacAddress;
        Query query = view.createQuery();
        ArrayList<Safezone> arrayListSafezones = new ArrayList<Safezone>();
        List<Object> keyArray = new ArrayList<Object>();
        keyArray.add(macAddress);
        query.setKeys(keyArray);
        query.setDescending(true);


        try {
            QueryEnumerator rowEnum = query.run();
            for (Iterator<QueryRow> it = rowEnum; it.hasNext(); ) {
                QueryRow row = it.next();
                Document document = row.getDocument();

                try {
                    Safezone safezone = new Safezone(document.getId(), document.getProperty("address").toString(), document.getProperty("name").toString(), document.getProperty("latitude").toString(), document.getProperty("longitude").toString(), document.getProperty("radius").toString(), document.getProperty("notification").toString(), document.getProperty("timestamp").toString(), document.getProperty("device").toString());
                    arrayListSafezones.add(safezone);
                } catch (Exception ex) {
                    Log.e(TAG, "DeviceRow error parse");
                }

            }

        } catch (CouchbaseLiteException e) {
            e.printStackTrace();
        }

        return arrayListSafezones;
    }

    public static Safezone getSafezoneByID(String idSafezone) {
        Document document = CouchDB.getmCouchDBinstance().getDatabase().getExistingDocument(idSafezone);
        if (document == null)
            return null;
        Safezone tempSafezone = null;
        try {
            tempSafezone = new Safezone(idSafezone, document.getProperty("address").toString(), document.getProperty("name").toString(), document.getProperty("latitude").toString(), document.getProperty("longitude").toString(), document.getProperty("radius").toString(), document.getProperty("notification").toString(), document.getProperty("timestamp").toString(), document.getProperty("device").toString());
        } catch (Exception ex) {
            Log.d(TAG, "Error getting safezone " + idSafezone);
        }
        return tempSafezone;
    }

    public void saveSafezone(boolean isToInsertNewSafezone) throws CouchbaseLiteException {
        // create an object to hold document data
        Map<String, Object> properties = new HashMap<String, Object>();
        if (CouchDB.getmCouchDBinstance().getDatabase() != null) {
            properties.put("address", this.getAddress());
            properties.put("latitude", this.getLatitude());
            properties.put("longitude", this.getLongitude());
            properties.put("name", this.getName());
            properties.put("notification", this.getNotification());
            properties.put("radius", this.getRadius());
            properties.put("timestamp", System.currentTimeMillis());
            properties.put("type", "safezone");
            properties.put("device", this.getDevice());

// getDocument if exist return document by id else create document with the parameter
            Document document = CouchDB.getmCouchDBinstance().getDatabase().getDocument(this.get_id());
            if (!isToInsertNewSafezone) {
                properties.put("_rev", document.getCurrentRevisionId()); //get last rev document
            }
// store the data in the document
            document.putProperties(properties);
        }
    }

    public static boolean delete(String idSafezone) {
        Document document = CouchDB.getmCouchDBinstance().getDatabase().getExistingDocument(idSafezone);
        if (document != null) {
            try {
                document.delete();
                return true;
            } catch (CouchbaseLiteException e) {
                e.printStackTrace();
                return false;
            }
        }
        return false;
    }
}
