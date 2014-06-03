package mei.ricardo.pessoa.app.couchdb.modal;

import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;

import com.couchbase.lite.CouchbaseLiteException;
import com.couchbase.lite.Query;
import com.couchbase.lite.QueryEnumerator;
import com.couchbase.lite.QueryRow;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import mei.ricardo.pessoa.app.Application;

/**
 * Created by rpessoa on 03/06/14.
 */
public class MonitorSensor{

    private static String TAG = MonitorSensor.class.getName();

    public static ArrayList<String> getMonitoringSensorByMacAddressAndSubtype(String macAddress,String subType){
        com.couchbase.lite.View view = Application.getmCouchDBinstance().viewGetMonitorSensor;
        Query query = view.createQuery();
        ArrayList<String> arrayList = new ArrayList<String>();
        //List<Object> keyArray = new ArrayList<Object>();
        Log.d(MonitorSensor.class.getName(),"Find for keys:["+macAddress+","+subType+"]");

        List<Object> keyArray = new ArrayList<Object>();
        keyArray.add(macAddress);
        keyArray.add(subType);
        //query.setLimit(5);
        query.setDescending(true);
        //query.setKeys(keyArray);

        try {
            QueryEnumerator rowEnum = query.run();
            for (Iterator<QueryRow> it = rowEnum; it.hasNext();) {
                QueryRow row = it.next();
                if (row.getKey().equals(keyArray)){
                    Log.d(TAG, "Document ID:" + row.getDocumentId());
                    arrayList.add(row.toString());
                }
            }

        } catch (CouchbaseLiteException e) {
            e.printStackTrace();
        }
        return arrayList;

    }
}
