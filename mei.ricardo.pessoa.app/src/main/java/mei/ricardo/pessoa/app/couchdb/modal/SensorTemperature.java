package mei.ricardo.pessoa.app.couchdb.modal;

import android.util.Log;

import java.util.HashMap;

/**
 * Created by rpessoa on 13/05/14.
 */
public class SensorTemperature extends Sensor {
    private static String TAG = SensorBattery.class.getName();
    private float min_temperature;
    private float max_temperature;

    public SensorTemperature() {
        super("Sensor Temperature", Device.DEVICESTYPE.temperature.toString());
    }

    public SensorTemperature(boolean isEnable) {
        super("Sensor Temperature", Device.DEVICESTYPE.temperature.toString(), isEnable);
    }

//    public SensorTemperature(String name, String type, float min_temperature, float max_temperature) {
//        super(name, type);
//        this.min_temperature = min_temperature;
//        this.max_temperature = max_temperature;
//    }

    public float getMax_temperature() {
        return max_temperature;
    }

    public void setMax_temperature(float max_temperature) {
        this.max_temperature = max_temperature;
    }

    public float getMin_temperature() {
        return min_temperature;
    }

    public void setMin_temperature(float min_temperature) {
        this.min_temperature = min_temperature;
    }

    public void parseSensorTemperature(HashMap<Integer, Object> value) {
        float maxTemp = 0, minTemp = 0;
        try {
            maxTemp = Float.parseFloat(value.get("max_temperature").toString());
        } catch (Exception ex) {
            Log.e(TAG, "Error getting the max temperature of sensor");
        }
        try {
            minTemp = Float.parseFloat(value.get("min_temperature").toString());
        } catch (Exception ex) {
            Log.e(TAG, "Error getting the min temperature of sensor");
        }


        this.min_temperature = minTemp;
        this.max_temperature = maxTemp;
    }

}
