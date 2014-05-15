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
public class Device extends Document {
    public enum deviceTypes {
        panic_button,
        GPS,
        temperature,
    }

    private static String TAG = Device.class.getName();
    private String name_device;
    HashMap<Integer,Sensor> sensors = new HashMap<Integer, Sensor>();
    private String timestamp;

    public Device(String mac_address, String name_device, HashMap<Integer,Sensor> arraySensors) {
        //mac address device is _id
        //super(mac_address);
        super(CouchDB.getmCouchDBinstance().getDatabase(), mac_address);
        this.name_device = name_device;
        this.sensors = arraySensors;
    }

    public static HashMap<String,Object> getSensorsByDeviceID(String _id){
        Document document = CouchDB.getmCouchDBinstance().getDatabase().getExistingDocument(_id);
        HashMap<String,Object> hashMapSensors = new HashMap<String,Object>();
        if(document == null){
            return null;
        }
        try {
            hashMapSensors = (HashMap<String,Object>) document.getProperty("sensors");
            Log.d(TAG,"Find n "+hashMapSensors.size()+" sensors");
        }catch (ClassCastException ex){
            Log.e(TAG, "Error on cast Sensors from db");
        }catch (Exception ex){
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
            properties.put("activity_sensors",sensors);
            properties.put("type", "device");
// getDocument if exist return document by id else create document with the parameter
            Document document = CouchDB.getmCouchDBinstance().getDatabase().getDocument(this.getId());
// store the data in the document
            document.putProperties(properties);
        }
    }
}
