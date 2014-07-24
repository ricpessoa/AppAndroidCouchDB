package mei.ricardo.pessoa.app.couchdb.modal;

import android.util.Log;

import java.util.HashMap;

/**
 * Created by rpessoa on 12/07/14.
 */
public class SensorBattery extends Sensor {
    private static String TAG = SensorTemperature.class.getName();
    private float low_battery = 25;
    private float critical_battery = 15;

    public SensorBattery() {
        super("Sensor Battery", Device.DEVICESTYPE.battery.toString());
    }

    public SensorBattery(boolean isEnable) {
        super("Sensor Battery", Device.DEVICESTYPE.battery.toString(), isEnable);
    }

//    public SensorBattery(String name, String type, float low_battery, float critical_battery) {
//        super(name, type);
//        this.low_battery = low_battery;
//        this.critical_battery = critical_battery;
//    }

    public float getCritical_battery() {
        return critical_battery;
    }

    public void setCritical_battery(float critical_battery) {
        this.critical_battery = critical_battery;
    }

    public float getLow_battery() {
        return low_battery;
    }

    public void setLow_battery(float low_battery) {
        this.low_battery = low_battery;
    }

    public void parseSensorBattery(HashMap<Integer, Object> value) {
        float lowBat = 0, critBat = 0;
        try {
            lowBat = Float.parseFloat(value.get("low_battery").toString());
        } catch (Exception ex) {
            Log.e(TAG, "Error getting the max temperature of sensor");
        }
        try {
            critBat = Float.parseFloat(value.get("critical_battery").toString());
        } catch (Exception ex) {
            Log.e(TAG, "Error getting the min temperature of sensor");
        }

        this.low_battery = lowBat;
        this.critical_battery = critBat;
    }
}
