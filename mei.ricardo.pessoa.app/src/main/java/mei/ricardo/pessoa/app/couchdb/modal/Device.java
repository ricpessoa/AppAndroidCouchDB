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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import mei.ricardo.pessoa.app.couchdb.CouchDB;
import mei.ricardo.pessoa.app.utils.DeviceRow;

/**
 * Created by rpessoa on 06/05/14.
 */
public class Device {
    private static String TAG = Device.class.getName();

    public enum DEVICESTYPE {
        panic_button,
        GPS,
        temperature,
        battery
    }

    public static String[] devicesTypesString = {"Panic Button", "GPS", "Temperature", "Battery"};

    private String mac_address;
    private String name_device;
    HashMap<String, Object> sensors = new HashMap<String, Object>();
    private String timestamp;

    private boolean monitoring;
    private boolean isDeleted;
    private String owner;
    private boolean showPanicButton = false, showSafezone = false, showTemperature = false, showBattery = false;
    private ArrayList<Sensor> arrayListSensors = null;

    public Device() {
    }

    public Device(String mac_address, String name_device, HashMap<String, Object> arraySensors, boolean checked) {
        //mac address device is _id
        this.mac_address = mac_address;
        this.name_device = name_device;
        this.sensors = arraySensors;
        this.monitoring = checked;
    }

    /**
     * This method is to construct TabHost with devices
     * LinkedHashMap preserve the inserting order (and is like HasMap)
     */
    public static LinkedHashMap<String, String> getHashMapOfDevices() {
        LinkedHashMap<String, String> deviceHaspMap = new LinkedHashMap<String, String>();
        deviceHaspMap.put("", "All Devices");
        com.couchbase.lite.View view = CouchDB.viewGetDevicesMonitoring;
        Query query = view.createQuery();
        try {
            QueryEnumerator rowEnum = query.run();
            for (Iterator<QueryRow> it = rowEnum; it.hasNext(); ) {
                QueryRow row = it.next();

                Object deviceID = row.getDocumentId();
                Object deviceName = row.getDocument().getProperty("name_device");
                if (deviceName != null) {
                    deviceHaspMap.put(deviceID.toString(), deviceName.toString());
                }

            }
        } catch (CouchbaseLiteException e) {
            e.printStackTrace();
        }

        if (deviceHaspMap.size() <= 2) { //if have only device not need show tab all devices
            deviceHaspMap.remove("");
        }

        return deviceHaspMap;
    }

    public static Device getDeviceByID(String _id) {
        Document document = CouchDB.getmCouchDBinstance().getDatabase().getExistingDocument(_id);
        if (document == null)
            return null;

        Device tempDevice = new Device();
        tempDevice.mac_address = _id;
        tempDevice.name_device = document.getProperty("name_device").toString();
        tempDevice.isDeleted = Boolean.parseBoolean(document.getProperty("deleted").toString());
        tempDevice.owner = document.getProperty("owner").toString();
        tempDevice.sensors = (HashMap<String, Object>) document.getProperty("sensors");
        tempDevice.timestamp = document.getProperty("timestamp").toString();
        try {
            tempDevice.monitoring = (Boolean) document.getProperty("monitoring");
        } catch (NullPointerException ex) {
            tempDevice.monitoring = false;
        }
        tempDevice.getSensorsOfDevice();
        return tempDevice;
    }

    public static ArrayList<DeviceRow> getAllDevicesNotDeleted() {
        ArrayList<DeviceRow> deviceRowsList = new ArrayList<DeviceRow>();

        com.couchbase.lite.View view = CouchDB.viewGetDevices;
        Query query = view.createQuery();
        try {
            QueryEnumerator rowEnum = query.run();
            for (Iterator<QueryRow> it = rowEnum; it.hasNext(); ) {
                QueryRow row = it.next();

                DeviceRow deviceRow = new DeviceRow();
                deviceRow.deviceID = row.getDocumentId();
                String nameDevice = "";
                HashMap<Object, Object> numbSensors = new HashMap<Object, Object>();
                Document document = row.getDocument();
                try {
                    nameDevice = document.getProperty("name_device").toString();
                    if (document.getProperty("deleted").equals(true))
                        continue; //this continue pass this document
                    numbSensors = (HashMap<Object, Object>) row.getDocument().getProperty("sensors");
                } catch (NullPointerException ex) {
                    nameDevice = "Device " + row.getDocumentId();
                } catch (Exception ex) {
                    Log.e(TAG, "Error in Device _id:" + nameDevice);
                }
                deviceRow.deviceName = nameDevice;
                if (numbSensors == null)
                    break;
                deviceRow.deviceDescription = "Number of Sensors " + numbSensors.size();
                deviceRowsList.add(deviceRow);

            }
        } catch (CouchbaseLiteException e) {
            e.printStackTrace();
        }
        return deviceRowsList;
    }

    private void getSensorsOfDevice() {
        arrayListSensors = new ArrayList<Sensor>();
        for (Map.Entry<String, Object> entry : sensors.entrySet()) {
            String key = entry.getKey();
            HashMap<Integer, Object> value = new HashMap<Integer, Object>();
            try {
                value = (HashMap<Integer, Object>) entry.getValue();
            } catch (ClassCastException ex) {
                Log.d(TAG, "value -> error");
            }
            String type = value.get("type").toString();
            boolean isEnable = false;
            try {
                isEnable = Boolean.parseBoolean(value.get("enable").toString());
            } catch (NullPointerException ex) {
                Log.e(TAG, "Error. Parse sensor enable");
            }
            if (type.equals(DEVICESTYPE.panic_button.toString())) {
                showPanicButton = true;
                SensorPanicButton sensorPanicButton = new SensorPanicButton(isEnable);
                arrayListSensors.add(sensorPanicButton);
            } else if (type.equals(DEVICESTYPE.GPS.toString())) {
                showSafezone = true;
                SensorGPS sensorGPS = new SensorGPS(isEnable);
                arrayListSensors.add(sensorGPS);
            } else if (type.equals(DEVICESTYPE.temperature.toString())) {
                showTemperature = true;
                SensorTemperature sensorTemperature = new SensorTemperature(isEnable);
                sensorTemperature.parseSensorTemperature(value);
                arrayListSensors.add(sensorTemperature);
            } else if (type.equals(DEVICESTYPE.battery.toString())) {
                showBattery = true;
                SensorBattery sensorBattery = new SensorBattery(isEnable);
                sensorBattery.parseSensorBattery(value);
                arrayListSensors.add(sensorBattery);
            }
        }
    }

    public static Device getSensorsToSearch(String id) {
        Document document = CouchDB.getmCouchDBinstance().getDatabase().getExistingDocument(id);
        if (document == null)
            return null;

        Device tempDevice = new Device();
        tempDevice.sensors = (HashMap<String, Object>) document.getProperty("sensors");
        tempDevice.getSensorsOfDevice();
        return tempDevice;
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

    public void saveDevice(boolean updateSensors) throws CouchbaseLiteException {
        // create an object to hold document data
        Map<String, Object> properties = new HashMap<String, Object>();

        if (CouchDB.getmCouchDBinstance().getDatabase() != null) {
            properties.put("timestamp", System.currentTimeMillis());
            properties.put("name_device", name_device);
            if (updateSensors)
                properties.put("sensors", parseToSave(arrayListSensors, sensors));
            else
                properties.put("sensors", sensors);
            properties.put("type", "device");
            properties.put("monitoring", monitoring);
            properties.put("deleted", isDeleted());
            properties.put("owner", owner);
// getDocument if exist return document by id else create document with the parameter
            Document document = CouchDB.getmCouchDBinstance().getDatabase().getDocument(mac_address);
            properties.put("_rev", document.getCurrentRevisionId()); //get last rev document
// store the data in the document
            document.putProperties(properties);
        }
    }


    public static void deleteDevice(String deviceID) {
        Device device = getDeviceByID(deviceID);
        device.setDeleted(true);
        try {
            device.saveDevice(false);
        } catch (CouchbaseLiteException e) {
            e.printStackTrace();
            Log.e(TAG, "error deleting device");
        }
    }

    private HashMap<String, Object> parseToSave(ArrayList<Sensor> arrayListSensors, HashMap<String, Object> sensors) {
        for (Map.Entry<String, Object> entry : sensors.entrySet()) {
            String key = entry.getKey();
            HashMap<String, Object> value = new HashMap<String, Object>();
            try {
                value = (HashMap<String, Object>) entry.getValue();
            } catch (ClassCastException ex) {
                Log.d(TAG, "value -> error");
            }
            String type = value.get("type").toString();
            if (type.equals(DEVICESTYPE.temperature.toString())) {
                value.put("min_temperature", ((SensorTemperature) arrayListSensors.get(2)).getMin_temperature());
                value.put("max_temperature", ((SensorTemperature) arrayListSensors.get(2)).getMax_temperature());
            } else if (type.equals(DEVICESTYPE.battery.toString())) {
                value.put("low_battery", ((SensorBattery) arrayListSensors.get(3)).getLow_battery());
                value.put("critical_battery", ((SensorBattery) arrayListSensors.get(3)).getCritical_battery());
            }
        }
        return sensors;
    }

    public String[] getSensorsToShowInArrayString() {
        ArrayList<String> arrayListSensors = new ArrayList<String>();
        if (showPanicButton)
            arrayListSensors.add(devicesTypesString[0]);
        if (showSafezone)
            arrayListSensors.add(devicesTypesString[1]);
        if (showTemperature)
            arrayListSensors.add(devicesTypesString[2]);
        if (showBattery)
            arrayListSensors.add(devicesTypesString[3]);

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

    public boolean isShowPanicButton() {
        return showPanicButton;
    }

    public boolean isShowSafezone() {
        return showSafezone;
    }

    public boolean isShowTemperature() {
        return showTemperature;
    }

    public boolean isShowBattery() {
        return showBattery;
    }

    public ArrayList<Sensor> getArrayListSensors() {
        return arrayListSensors;
    }

    public String getMac_address() {
        return mac_address;
    }

    public boolean isDeleted() {
        return isDeleted;
    }

    public void setDeleted(boolean isDeleted) {
        this.isDeleted = isDeleted;
    }

    public String getName_device() {
        return name_device;
    }

    public void setName_device(String name_device) {
        this.name_device = name_device;
    }
}
