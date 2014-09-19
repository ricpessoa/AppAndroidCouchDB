package mei.ricardo.pessoa.app.couchdb.modal.Monitoring;

import android.graphics.drawable.Drawable;
import android.provider.SyncStateContract;
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
import mei.ricardo.pessoa.app.couchdb.modal.Device;
import mei.ricardo.pessoa.app.utils.InterfaceItem;
import mei.ricardo.pessoa.app.utils.Utils;

/**
 * Created by rpessoa on 03/06/14.
 */
public class MonitorSensor implements InterfaceItem {
    private static String TAG = MonitorSensor.class.getName();
    public static String type = "monitoring_sensor";

    public enum SUBTYPE {panic_button, GPS, temperature, battery}

    public static String[] subtypeSections = {"Sensor Panic Button", "Sensor GPS", "Sensor Temperature", "Battery Level"};
    public String subtype;
    public String timestamp;
    public String mac_address;

    /**
     * this script is only for access variables of document
     */
    public static String doc_mac_adress = "mac_address";
    public static String doc_notification = "notification";
    public static String doc_seen = "seen";
    public static String doc_subtype = "subtype";
    public static String doc_timestamp = "timestamp";
    public static String doc_type = "type";
    public static String doc_value = "value";

    /**
     * This method is only when find MS_NotHave
     */
    public MonitorSensor(String subtype) {
        String sensorType;
        if (SUBTYPE.panic_button.toString().equals(subtype)) {
            sensorType = subtypeSections[0];
        } else if (SUBTYPE.GPS.toString().equals(subtype)) {
            sensorType = subtypeSections[1];
        } else if (SUBTYPE.battery.toString().equals(subtype)) {
            sensorType = subtypeSections[2];
        } else if (SUBTYPE.temperature.toString().equals(subtype)) {
            sensorType = subtypeSections[3];
        } else {
            sensorType = "Sensor unknown";
        }
        this.subtype = sensorType;
    }

    //faster constructor only to show in list
    public MonitorSensor(String mac_address, String subtype, String timestamp) {
        this.mac_address = mac_address;
        this.subtype = subtype;
        this.timestamp = timestamp;
    }

    /**
     * This method is to show the monitoring sensor in listview (mode lite)
     */
    public static ArrayList<InterfaceItem> getMonitoringSensorByMacAddressAndSubtype(String macAddress, String subType, int limit) {
        com.couchbase.lite.View view = Application.getmCouchDBinstance().viewGetMonitorSensor;
        Query query = view.createQuery();
        ArrayList<InterfaceItem> arrayList = new ArrayList<InterfaceItem>();
        //List<Object> keyArray = new ArrayList<Object>();
        Log.d(TAG, "Find for keys:[" + macAddress + "," + subType + "]");

        List<Object> keyArray = new ArrayList<Object>();
        keyArray.add(macAddress);
        keyArray.add(subType);
        query.setStartKey(new Object[]{macAddress, subType, new HashMap()});
        query.setEndKey(new Object[]{macAddress, subType});
        query.setLimit(limit);
        query.setDescending(true);

        try {
            QueryEnumerator rowEnum = query.run();
            for (Iterator<QueryRow> it = rowEnum; it.hasNext(); ) {
                QueryRow row = it.next();
                Document document = row.getDocument();
                if (subType.equals(SUBTYPE.GPS.toString())) {
                    try {
                        MS_GPS ms_gps = new MS_GPS(document.getProperty("address").toString(), document.getProperty("notification").toString(), macAddress, subType, document.getProperty("timestamp").toString());
                        arrayList.add(ms_gps);
                    } catch (Exception ex) {
                        Log.e(TAG, "Error GPS not valid for some reason");
                    }
                } else if (subType.equals(SUBTYPE.temperature.toString())) {
                    try {
                        MS_Temperature ms_temperature = new MS_Temperature(document.getProperty("value").toString(), document.getProperty("notification").toString(), macAddress, subType, document.getProperty("timestamp").toString());
                        arrayList.add(ms_temperature);
                    } catch (Exception ex) {
                        Log.e(TAG, "Error Temperature parse error value or other some reason");
                    }
                } else if (subType.equals(SUBTYPE.panic_button.toString())) {
                    try {
                        MS_PanicButton ms_panicButton = new MS_PanicButton(document.getProperty("pressed").toString(), macAddress, subType, document.getProperty("timestamp").toString());
                        arrayList.add(ms_panicButton);
                    } catch (Exception ex) {
                        Log.e(TAG, "Error Panic Button parse error value or other some reason");
                    }
                } else if (subType.equals(SUBTYPE.battery.toString())) {
                    try {
                        MS_Battery ms_battery = new MS_Battery(document.getProperty("value").toString(), document.getProperty("notification").toString(), macAddress, subType, document.getProperty("timestamp").toString());
                        arrayList.add(ms_battery);
                    } catch (Exception ex) {
                        Log.e(TAG, "Error Panic Button parse error value or other some reason");
                    }
                }
            }

        } catch (CouchbaseLiteException e) {
            e.printStackTrace();
        }
        if (arrayList.size() == 0) {
            arrayList.add(new MS_NotHave(subType));
        }
        return arrayList;
    }

    public static QueryEnumerator getMonitorSensorByMacAddressSubtypeTimestamp(String macAddress, String subType, String timestamp, int limit) {
        com.couchbase.lite.View view = Application.getmCouchDBinstance().viewGetMonitorSensor;
        Query query = view.createQuery();
        ArrayList<InterfaceItem> arrayList = new ArrayList<InterfaceItem>();
        query.setDescending(true);
        query.setStartKey(new Object[]{macAddress, subType, timestamp, new HashMap()});
        query.setEndKey(new Object[]{macAddress, subType, timestamp});
        query.setLimit(limit);
        QueryEnumerator rowEnum;
        try {
            rowEnum = query.run();
        } catch (CouchbaseLiteException e) {
            e.printStackTrace();
            return null;
        }
        return rowEnum;
    }

    public static void setSeenToDocument(Document doc) {
        Map<String, Object> curProperties = doc.getProperties();

        // make a copy of the document properties
        Map<String, Object> newProperties = new HashMap<String, Object>();
        newProperties.putAll(curProperties);
        boolean seen = true;

        newProperties.put("seen", seen);
        try {
            doc.putProperties(newProperties);
            Log.d(TAG, "update monitoring successful to " + seen);
        } catch (CouchbaseLiteException e) {
            Log.e(TAG, "Error updating database", e);
            e.printStackTrace();
        }
    }

    public String getMac_address() {
        return mac_address;
    }

    public String getTitle() {
        if (this.getClass() == MS_GPS.class) {
            MS_GPS ms_gps = (MS_GPS) this;
            return ms_gps.getAddress();
        } else if (this.getClass() == MS_Temperature.class) {
            MS_Temperature ms_temperature = (MS_Temperature) this;
            return "Temperature " + ms_temperature.getValue() + "ºC";
        } else if (this.getClass() == MS_Battery.class) {
            MS_Battery ms_battery = (MS_Battery) this;
            return "Battery Level " + ms_battery.getValue() + "%";
        } else if (this.getClass() == MS_PanicButton.class) {
            MS_PanicButton ms_panicButton = (MS_PanicButton) this;
            return "Panic Button pressed";
        }
        MS_NotHave ms_notHave = (MS_NotHave) this;
        return "Not yet received any information from " + ms_notHave.getSubtype() + " sensor!";
    }

    public String getTextToShowInFragmentNotification() {
        String str_construct = "";
        Device device = Device.getDeviceByID(this.getMac_address());

        if (device != null) {
            str_construct = device.getNameOrMacAdress();
        }
        if (this.getClass() == MS_GPS.class) {
            MS_GPS ms_gps = (MS_GPS) this;
            str_construct += " in " + Utils.ConvertTimestampToDateFormat(ms_gps.getTimestamp()) + "\n";
            return str_construct + ms_gps.getAddress();
        } else if (this.getClass() == MS_Temperature.class) {
            MS_Temperature ms_temperature = (MS_Temperature) this;
            str_construct += " in " + Utils.ConvertTimestampToDateFormat(ms_temperature.getTimestamp()) + "\n";
            return str_construct + "Temperature " + ms_temperature.getValue() + "ºC";
        } else if (this.getClass() == MS_Battery.class) {
            MS_Battery ms_battery = (MS_Battery) this;
            str_construct += " in " + Utils.ConvertTimestampToDateFormat(ms_battery.getTimestamp()) + "\n";
            return str_construct + "Battery Level " + ms_battery.getValue() + "%";
        } else if (this.getClass() == MS_PanicButton.class) {
            MS_PanicButton ms_panicButton = (MS_PanicButton) this;
            str_construct += " in " + Utils.ConvertTimestampToDateFormat(ms_panicButton.getTimestamp()) + "\n";
            return str_construct + "Panic Button pressed";
        }
        return null;
    }


    @Override
    public boolean isSection() {
        return false;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public String getSubtype() {
        return subtype;
    }

    public Drawable getImage() {
        return null;
    }
}
