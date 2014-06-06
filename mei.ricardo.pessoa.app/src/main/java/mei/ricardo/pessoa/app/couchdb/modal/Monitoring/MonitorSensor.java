package mei.ricardo.pessoa.app.couchdb.modal.Monitoring;

import android.util.Log;

import com.couchbase.lite.CouchbaseLiteException;
import com.couchbase.lite.Document;
import com.couchbase.lite.Query;
import com.couchbase.lite.QueryEnumerator;
import com.couchbase.lite.QueryRow;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import mei.ricardo.pessoa.app.Application;
import mei.ricardo.pessoa.app.couchdb.modal.Monitoring.Utils.InterfaceItem;

/**
 * Created by rpessoa on 03/06/14.
 */
public class MonitorSensor implements InterfaceItem {
    private static String TAG = MonitorSensor.class.getName();

    public enum SUBTYPE {panic_button, GPS, temperature}

    public String subtype;
    public String timestamp;
    public String mac_address;

    //faster constructor only to show in list
    public MonitorSensor(String subtype, String timestamp) {
        this.subtype = subtype;
        this.timestamp = timestamp;
    }


    public static ArrayList<InterfaceItem> getMonitoringSensorByMacAddressAndSubtype(String macAddress, String subType, int limit) {
        com.couchbase.lite.View view = Application.getmCouchDBinstance().viewGetMonitorSensor;
        Query query = view.createQuery();
        ArrayList<InterfaceItem> arrayList = new ArrayList<InterfaceItem>();
        //List<Object> keyArray = new ArrayList<Object>();
        Log.d(TAG, "Find for keys:[" + macAddress + "," + subType + "]");

        List<Object> keyArray = new ArrayList<Object>();
        int count = 1;
        keyArray.add(macAddress);
        keyArray.add(subType);
        //query.setLimit(5);
        query.setDescending(true);
        //query.setKeys(keyArray);

        try {
            QueryEnumerator rowEnum = query.run();
            for (Iterator<QueryRow> it = rowEnum; it.hasNext(); ) {
                QueryRow row = it.next();
                if (row.getKey().equals(keyArray)) {
                    Document document = row.getDocument();
                    if (subType.equals(SUBTYPE.GPS.toString())) {
                        try {
                            MS_GPS ms_gps = new MS_GPS(document.getProperty("address").toString(), document.getProperty("notification").toString(), subType, document.getProperty("timestamp").toString());
                            arrayList.add(ms_gps);
                        } catch (Exception ex) {
                            Log.e(TAG, "Error GPS not valid for some reason");
                        }
                    } else if (subType.equals(SUBTYPE.temperature.toString())) {
                        try {
                            MS_Temperature ms_temperature = new MS_Temperature(document.getProperty("value").toString(), subType, document.getProperty("timestamp").toString());
                            arrayList.add(ms_temperature);
                        } catch (Exception ex) {
                            Log.e(TAG, "Error Temperature parse error value or other some reason");
                        }
                    }else if (subType.equals(SUBTYPE.panic_button.toString())){
                        try {
                            MS_PanicButton ms_panicButton = new MS_PanicButton(document.getProperty("pressed").toString(),subType,document.getProperty("timestamp").toString());
                            arrayList.add(ms_panicButton);
                        }catch (Exception ex){
                            Log.e(TAG, "Error Panic Button parse error value or other some reason");
                        }
                    }
                    if (limit > 0)
                        count++;
                    //Log.d(TAG, "Document ID:" + row.getDocumentId());
                    //arrayList.add(row.getDocumentId()+" - "+row.getDocument().getProperty("subtype").toString());
                }
                if (limit > 0 && limit == count)
                    return arrayList;
            }

        } catch (CouchbaseLiteException e) {
            e.printStackTrace();
        }
        return arrayList;
    }

    public String getTitle(){
        if(this.subtype.equals(SUBTYPE.GPS.toString())){
           MS_GPS ms_gps = (MS_GPS)this;
            return ms_gps.getAddress();
        }else if (this.subtype.equals(SUBTYPE.temperature.toString())){
            MS_Temperature ms_temperature = (MS_Temperature)this;
            return ms_temperature.getValue()+" Temperature";
        }else if (this.subtype.equals(SUBTYPE.panic_button.toString())) {
            MS_PanicButton ms_panicButton = (MS_PanicButton)this;
            return ms_panicButton.isPressed()+" pressed";
        }
        return "unknow";
    }

    @Override
    public boolean isSection() {
        return false;
    }
}
