package mei.ricardo.pessoa.app.couchdb.modal;

import java.util.ArrayList;

/**
 * Created by rpessoa on 12/07/14.
 */
public class SensorGPS extends Sensor {
    private static String TAG = SensorGPS.class.getCanonicalName();
    //private ArrayList<DeviceRow> safezoneArrayList = new ArrayList<DeviceRow>();

    public SensorGPS() {
        super("Sensor GPS", Device.DEVICESTYPE.GPS.toString());
    }

    public SensorGPS(boolean isEnable) {
        super("Sensor GPS", Device.DEVICESTYPE.GPS.toString(), isEnable);
    }


//    public ArrayList<DeviceRow> getSafezoneArrayList() {
//        return safezoneArrayList;
//    }
//
//    public void setSafezoneArrayList(ArrayList<DeviceRow> safezoneArrayList) {
//        this.safezoneArrayList = safezoneArrayList;
//    }
//
//    public static void getSafezone(int Device_ID, Sensor sensorGPS) {
//        //get safezones Array
//        ArrayList<DeviceRow> arrayListOfSafezones = new ArrayList<DeviceRow>();
//
//    }
}
