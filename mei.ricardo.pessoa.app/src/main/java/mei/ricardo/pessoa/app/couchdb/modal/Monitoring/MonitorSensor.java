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
import java.util.List;

import mei.ricardo.pessoa.app.Application;
import mei.ricardo.pessoa.app.couchdb.modal.Device;
import mei.ricardo.pessoa.app.utils.InterfaceItem;

/**
 * Created by rpessoa on 03/06/14.
 */
public class MonitorSensor implements InterfaceItem {
    private static String TAG = MonitorSensor.class.getName();

    public enum SUBTYPE {panic_button, GPS, temperature, battery}

    public static String[] subtypeSections = {"Sensor Panic Button", "Sensor GPS", "Sensor Temperature", "Battery Level"};
    public String subtype;
    public String timestamp;
    public String mac_address;

    public MonitorSensor(String subtype) {
        this.subtype = subtype;
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

//    public static ArrayList<MonitorSensor> getSensorGPSByMacAddressAndSubtype(String macAddress, String subType, int limit) {
//        com.couchbase.lite.View view = Application.getmCouchDBinstance().viewGetMonitorSensor;
//        Query query = view.createQuery();
//        ArrayList<MS_GPS> arrayList = new ArrayList<MS_GPS>();
//
//        query.setStartKey(new Object[]{macAddress, subType, new HashMap()});
//        query.setEndKey(new Object[]{macAddress, subType});
//        query.setDescending(true);
//        query.setLimit(limit);
//
//        try {
//            QueryEnumerator rowEnum = query.run();
//            for (Iterator<QueryRow> it = rowEnum; it.hasNext(); ) {
//                QueryRow row = it.next();
//                Document document = row.getDocument();
//                if (subType.equals(SUBTYPE.GPS.toString())) {
//                    try {
//                        MS_GPS ms_gps = new MS_GPS(document.getProperty("address").toString(), document.getProperty("notification").toString(), document.getProperty("latitude").toString(), document.getProperty("longitude").toString(), macAddress, subType, document.getProperty("timestamp").toString());
//                        arrayList.add(ms_gps);
//                    } catch (Exception ex) {
//                        Log.e(TAG, "Error GPS not valid for some reason");
//                    }
//                }
//            }
//        } catch (CouchbaseLiteException e) {
//            e.printStackTrace();
//        }
//        return arrayList;
//    }

//    public static ArrayList<InterfaceItem> getSensorPanicButtonByMacAddressAndSubtype(String macAddress, String subType, String timestamp, int limit) {
//        com.couchbase.lite.View view = Application.getmCouchDBinstance().viewGetMonitorSensor;
//        Query query = view.createQuery();
//        ArrayList<InterfaceItem> arrayList = new ArrayList<InterfaceItem>();
//
//        List<Object> keyArray = new ArrayList<Object>();
//        query.setDescending(true);
//        query.setStartKey(new Object[]{macAddress, subType, timestamp, new HashMap()});
//        query.setEndKey(new Object[]{macAddress, subType, timestamp});
//        query.setLimit(limit);
//        try {
//            QueryEnumerator rowEnum = query.run();
//            for (Iterator<QueryRow> it = rowEnum; it.hasNext(); ) {
//                QueryRow row = it.next();
//                if (row.getKey().equals(keyArray)) {
//                    Document document = row.getDocument();
//                }
//            }
//
//        } catch (CouchbaseLiteException e) {
//            e.printStackTrace();
//        }
//
//        return arrayList;
//    }


    //        public static ArrayList<MonitorSensor> getLastNotificationToNotify() {
//            com.couchbase.lite.View view = Application.getmCouchDBinstance().viewGetMonitoringSensorsFromLastDay;
//            Query query = view.createQuery();
//            query.setDescending(true);
//            try {
//                QueryEnumerator rowEnum = query.run();
//                for (Iterator<QueryRow> it = rowEnum; it.hasNext(); ) {
//                    QueryRow row = it.next();
//                    Document document = row.getDocument();
//                    String subType = document.getProperty("subtype").toString();
//                    if (subType.equals(SUBTYPE.GPS.toString())) {
//                        try {
//                            MS_GPS ms_gps = new MS_GPS(document.getProperty("address").toString(), document.getProperty("notification").toString(), macAddress, subType, document.getProperty("timestamp").toString());
//                            arrayList.add(ms_gps);
//                        } catch (Exception ex) {
//                            Log.e(TAG, "Error GPS not valid for some reason");
//                        }
//                    } else if (subType.equals(SUBTYPE.temperature.toString())) {
//                        try {
//                            MS_Temperature ms_temperature = new MS_Temperature(document.getProperty("value").toString(), document.getProperty("notification").toString(), macAddress, subType, document.getProperty("timestamp").toString());
//                            arrayList.add(ms_temperature);
//                        } catch (Exception ex) {
//                            Log.e(TAG, "Error Temperature parse error value or other some reason");
//                        }
//                    } else if (subType.equals(SUBTYPE.panic_button.toString())) {
//                        try {
//                            MS_PanicButton ms_panicButton = new MS_PanicButton(document.getProperty("pressed").toString(), macAddress, subType, document.getProperty("timestamp").toString());
//                            arrayList.add(ms_panicButton);
//                        } catch (Exception ex) {
//                            Log.e(TAG, "Error Panic Button parse error value or other some reason");
//                        }
//                    }
//                }
//
//            } catch (CouchbaseLiteException e) {
//                e.printStackTrace();
//            }
//            return null;
//        }
//
    public String getMac_address() {
        return mac_address;
    }

    public String getTitle() {
        //this.getClass() instanceof  ? (() this.getClass()) : null;
        if (this.getClass() == MS_GPS.class) {
            MS_GPS ms_gps = (MS_GPS) this;
            return ms_gps.getAddress();
        } else if (this.getClass() == MS_Temperature.class) {
            MS_Temperature ms_temperature = (MS_Temperature) this;
            return ms_temperature.getValue() + "ºC Temperature";
        } else if (this.getClass() == MS_Battery.class) {
            MS_Battery ms_battery = (MS_Battery) this;
            return ms_battery.getValue() + "% Battery Level";
        } else if (this.getClass() == MS_PanicButton.class) {
            MS_PanicButton ms_panicButton = (MS_PanicButton) this;
            return ms_panicButton.isPressed() + " pressed";
        }
        MS_NotHave ms_notHave = (MS_NotHave) this;
        return "Not yet received any information from " + ms_notHave.getSubtype() + " sensor!";
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
