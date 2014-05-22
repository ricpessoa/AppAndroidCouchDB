package mei.ricardo.pessoa.app.couchdb.modal;

import android.util.Log;

import com.couchbase.lite.CouchbaseLiteException;
import com.couchbase.lite.Database;
import com.couchbase.lite.Document;

import java.security.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import mei.ricardo.pessoa.app.Application;
import mei.ricardo.pessoa.app.couchdb.CouchDB;

/**
 * Created by rpessoa on 06/05/14.
 */
public class Device{
    private static String TAG = Device.class.getName();
    public enum deviceTypes {
        panic_button,
        GPS,
        temperature,
    }
    private String mac_address;
    private String name_device;
    HashMap<String, Object> sensors = new HashMap<String, Object>();
    private String timestamp;
    private boolean monitoring;
    private boolean showPanicButton = false, showSafezone= false, showTemperature= false;

    public Device() {
    }

    public Device(String mac_address, String name_device, HashMap<String, Object> arraySensors, boolean checked) {
        //mac address device is _id
        this.mac_address = mac_address;
        this.name_device = name_device;
        this.sensors = arraySensors;
        this.monitoring = checked;
    }

    public static Device getDeviceByID(String _id) {
        Document document = CouchDB.getmCouchDBinstance().getDatabase().getExistingDocument(_id);
        if (document == null)
            return null;

        Device tempDevice = new Device();
        tempDevice.mac_address = _id;
        tempDevice.name_device = document.getProperty("name_device").toString();
        tempDevice.sensors = (HashMap<String, Object>)document.getProperty("sensors");
        tempDevice.timestamp = document.getProperty("timestamp").toString();
        try {
            tempDevice.monitoring = (Boolean)document.getProperty("monitoring");
        }catch (NullPointerException ex) {
            tempDevice.monitoring = false;
        }
        tempDevice.whatSensorsShow();
        return tempDevice;
    }

    private void whatSensorsShow(){
        for (Map.Entry<String, Object> entry : sensors.entrySet()) {
            String key = entry.getKey();
            HashMap<Integer, String> value = new HashMap<Integer, String>();
            try {
                value = (HashMap<Integer, String>) entry.getValue();
            } catch (ClassCastException ex) {
                Log.d(TAG, "value -> error");
            }
            String type = value.get("type");

            if (type.equals(Device.deviceTypes.panic_button.toString()))
                showPanicButton = true;
            else if (type.equals(Device.deviceTypes.GPS.toString()))
                showSafezone = true;
            else if (type.equals(Device.deviceTypes.temperature.toString()))
                showTemperature = true;
        }
    }

    public static HashMap<String, Object> getSensorsByDeviceID(String _id) {
        Document document = CouchDB.getmCouchDBinstance().getDatabase().getExistingDocument(_id);
        HashMap<String, Object> hashMapSensors = new HashMap<String, Object>();
        if (document == null) {
            return null;
        }
        try {
            hashMapSensors = (HashMap<String, Object>) document.getProperty("sensors");
            Log.d(TAG, "Find n " + hashMapSensors.size() + " sensors");
        } catch (ClassCastException ex) {
            Log.e(TAG, "Error on cast Sensors from db");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return hashMapSensors;
    }

    public void saveDevice() throws CouchbaseLiteException {
        // create an object to hold document data
        Map<String, Object> properties = new HashMap<String, Object>();

        if (CouchDB.getmCouchDBinstance().getDatabase() != null) {
            properties.put("timestamp", System.currentTimeMillis());
            properties.put("name_device", name_device);
            properties.put("sensors", sensors);
            properties.put("type", "device");
            properties.put("monitoring", monitoring);
// getDocument if exist return document by id else create document with the parameter
            Document document = CouchDB.getmCouchDBinstance().getDatabase().getDocument(mac_address);
            properties.put("_rev",document.getCurrentRevisionId()); //get last rev document
// store the data in the document
            document.putProperties(properties);
        }
    }

    public String[] getSensors() {
        ArrayList<String> arrayListSensors = new ArrayList<String>();
        if(showPanicButton)
            arrayListSensors.add("Panic Button");
        if (showSafezone)
            arrayListSensors.add("GPS");
        if (showTemperature)
            arrayListSensors.add("Temperature");
        String[] sensors = new String[arrayListSensors.size()];
        sensors = arrayListSensors.toArray(sensors);
        return sensors;
    }

    public boolean isMonitoring() {
        return monitoring;
    }

    public void setMonitoring(boolean monitoring) {
        this.monitoring = monitoring;
    }


}
