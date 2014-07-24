package mei.ricardo.pessoa.app.couchdb.modal;

import java.util.ArrayList;

/**
 * Created by rpessoa on 12/07/14.
 */
public class SensorGPS extends Sensor {
    private static String TAG = SensorGPS.class.getCanonicalName();
    //private ArrayList<Safezone> safezoneArrayList = new ArrayList<Safezone>();

    public SensorGPS() {
        super("Sensor GPS", Device.DEVICESTYPE.GPS.toString());
    }

    public SensorGPS(boolean isEnable) {
        super("Sensor GPS", Device.DEVICESTYPE.GPS.toString(), isEnable);
    }


//    public ArrayList<Safezone> getSafezoneArrayList() {
//        return safezoneArrayList;
//    }
//
//    public void setSafezoneArrayList(ArrayList<Safezone> safezoneArrayList) {
//        this.safezoneArrayList = safezoneArrayList;
//    }
//
//    public static void getSafezone(int Device_ID, Sensor sensorGPS) {
//        //get safezones Array
//        ArrayList<Safezone> arrayListOfSafezones = new ArrayList<Safezone>();
//
//    }
}
