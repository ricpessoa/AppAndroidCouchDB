package mei.ricardo.pessoa.app.couchdb.modal;

import com.couchbase.lite.CouchbaseLiteException;
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
public class Device extends CouchDocument{
    private String name_device;
    private ArrayList<Sensor> sensors = new ArrayList<Sensor>();
    private String timestamp;

    public Device(String mac_address,String name_device){
        //mac address device is _id
        super(mac_address);
        this.name_device = name_device;
    }

    public void saveDevice() throws CouchbaseLiteException {
        // set up a time stamp to use later

        // create an object to hold document data
        Map<String, Object> properties = new HashMap<String, Object>();

        if(CouchDB.getmCouchDBinstance().getDatabase()!=null){
            properties.put("_id",this.get_id());
            properties.put("name_device", this.name_device);
            properties.put("timestamp", System.currentTimeMillis());

            ArrayList<Map<String, Object>> arrayPropertiesSensots = new ArrayList<Map<String, Object>>();
            for (Sensor sensor : sensors){
                Map<String, Object> sensorMap = new HashMap<String, Object>();
                if(sensor instanceof SensorTemperature){
                    SensorTemperature st = (SensorTemperature) sensor;
                    sensorMap.put("min_temperature",st.getMin_temperature());
                    sensorMap.put("max_temperature",st.getMax_temperature());
                    sensorMap.put("type",st.getType());
                }else{
                    sensorMap.put("name_sensor",sensor.getName_sensor());
                    sensorMap.put("type",sensor.getType());
                }
                arrayPropertiesSensots.add(sensorMap);
            }

            properties.put("sensors",arrayPropertiesSensots);
            properties.put("type","device");

// create a new document
            Document document = CouchDB.getmCouchDBinstance().getDatabase().createDocument();

// store the data in the document
            document.putProperties(properties);

        }


    }


}
