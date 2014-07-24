package mei.ricardo.pessoa.app.couchdb;

import android.content.Intent;
import android.widget.Toast;

import com.couchbase.lite.Database;
import com.couchbase.lite.Emitter;
import com.couchbase.lite.LiveQuery;
import com.couchbase.lite.Manager;
import com.couchbase.lite.Mapper;
import com.couchbase.lite.QueryEnumerator;
import com.couchbase.lite.QueryRow;
import com.couchbase.lite.View;
import com.couchbase.lite.replicator.Replication;
import com.couchbase.lite.support.CouchbaseLiteApplication;
import com.couchbase.lite.util.Log;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import mei.ricardo.pessoa.app.Application;
import mei.ricardo.pessoa.app.ui.Fragments.FragmentMyDevices;
import mei.ricardo.pessoa.app.ui.Sensor.Safezone.ActivityListSafezones;

/**
 * Created by rpessoa on 12/05/14.
 */
public class CouchDB implements Replication.ChangeListener {
    private static String TAG = CouchDB.class.getName();
    private static CouchDB mCouchDBinstance = null;

    private static Database database = null;
    private static Manager mCouchManager = null;

    //couch internals
    public static View viewGetDevices;
    public static View viewGetSafezonesToFilterWithMacAddress;
    public static View viewGetSafezones;
    public static View viewGetMonitoringSensors;
    public static View viewGetDevicesMonitoring;
    public static View viewGetMonitorSensor;
    private LiveQuery liveQueryDevice;
    private LiveQuery liveQueryGetSafezones;
    private LiveQuery liveQueryMonitoringSensors;

    private CouchDB() {
        try {
            startCBLite();
        } catch (Exception e) {
            Toast.makeText(Application.getmContext(), "Error Initializing CBLIte, see logs for details", Toast.LENGTH_LONG).show();
            Log.e(TAG, "Error initializing CBLite", e);
        }

    }

    public static CouchDB getmCouchDBinstance() {
        if (mCouchDBinstance != null) {
            return mCouchDBinstance;
        } else {
            mCouchDBinstance = new CouchDB();
            return mCouchDBinstance;
        }
    }

    public Database getDatabase() {
        return database;
    }

    protected void startCBLite() throws Exception {

        mCouchManager = new Manager(Application.getmContext().getFilesDir(), Manager.DEFAULT_OPTIONS);

        //install a view definition needed by the application
        database = mCouchManager.getDatabase(Application.getDbname());

        startViewsQueryLives(); //add views necessaries to queries and live queries

        CouchbaseLiteApplication application = (CouchbaseLiteApplication) Application.getmContext();
        application.setManager(mCouchManager);


        startSync();
    }

    private void startViewsQueryLives() throws Exception {
        String designDocName = "appView";

        String allDevicesViewName = "getAllDevices";
        viewGetDevices = database.getView(String.format("%s/%s", designDocName, allDevicesViewName));
        viewGetDevices.setMap(new Mapper() {
            @Override
            public void map(Map<String, Object> document, Emitter emitter) {
                Object objDevice = document.get("type");
                if (objDevice != null && objDevice.equals("device")) {
                    emitter.emit(objDevice.toString(), document);
                }
            }
        }, "1.0");

        startLiveQuery(liveQueryDevice, viewGetDevices);

        String getSafezonesToFilterWithMacAddressViewName = "getSafezonesToFilterWithMacAddressViewName";
        viewGetSafezonesToFilterWithMacAddress = database.getView(String.format("%s/%s", designDocName, getSafezonesToFilterWithMacAddressViewName));
        viewGetSafezonesToFilterWithMacAddress.setMap(new Mapper() {
            @Override
            public void map(Map<String, Object> document, Emitter emitter) {
                Object objtype = document.get("type");
                Object objDevice = document.get("device");

                if (objDevice != null && objtype.equals("safezone")) {
                    emitter.emit(objDevice.toString(), document);
                }
            }
        }, "1.0");


        String allSafezonesViewName = "getAllSafezones";
        viewGetSafezones = database.getView(String.format("%s/%s", designDocName, allSafezonesViewName));
        viewGetSafezones.setMap(new Mapper() {
            @Override
            public void map(Map<String, Object> document, Emitter emitter) {
                Object objtype = document.get("type");
                if (objtype != null && objtype.equals("safezone")) {
                    emitter.emit(objtype.toString(), document);
                }
            }
        }, "1.0");
        startLiveQuery(liveQueryGetSafezones, viewGetSafezones);


        String allMonitorSensorsViewName = "getAllMonitorSensors";
        viewGetMonitoringSensors = database.getView(String.format("%s/%s", designDocName, allMonitorSensorsViewName));
        viewGetMonitoringSensors.setMap(new Mapper() {
            @Override
            public void map(Map<String, Object> document, Emitter emitter) {
                Object objDevice = document.get("type");
                if (objDevice != null && objDevice.equals("monitoring_sensor")) {
                    emitter.emit(objDevice.toString(), document);
                }
            }
        }, "1.0");

        startLiveQuery(liveQueryMonitoringSensors, viewGetMonitoringSensors);

        String DevicesToMonitoringViewName = "getDevicesToMonitoring";
        viewGetDevicesMonitoring = database.getView(String.format("%s/%s", designDocName, DevicesToMonitoringViewName));
        viewGetDevicesMonitoring.setMap(new Mapper() {
            @Override
            public void map(Map<String, Object> document, Emitter emitter) {
                Object objDevice = document.get("type");
                Object monitoring = document.get("monitoring");
                if (objDevice != null && objDevice.equals("device") && monitoring != null && monitoring.equals(true)) {
                    emitter.emit(objDevice.toString(), document);
                }
            }
        }, "2.0");

        String MonitoringSensor = "getMonitorSensorByKeys";
        viewGetMonitorSensor = database.getView(String.format("%s/%s", designDocName, MonitoringSensor));
        viewGetMonitorSensor.setMap(new Mapper() {
            @Override
            public void map(Map<String, Object> document, Emitter emitter) {
                List<Object> compoundKey = new ArrayList<Object>(); //object to
                Object mac_address = document.get("mac_address");
                Object subtype = document.get("subtype");
                Object timestamp = document.get("timestamp");
                if (mac_address != null && subtype != null && timestamp != null) {
                    //compoundKey.add(document.get("mac_address"));
                    //compoundKey.add(document.get("subtype"));
                    Object[] objects = new Object[]{mac_address.toString(), subtype.toString(), timestamp.toString()};
                    //emitter.emit("["+mac_address+","+subtype+","+document.get("timestamp")+"]", document); //TODO need to fix to array but for some reason dont allow with key []
                    //emitter.emit(compoundKey,document);
                    emitter.emit(objects, document);
                }
            }
        }, "1.0");
    }

    private void startSync() {

        URL syncUrl;
        try {
            syncUrl = new URL(Application.couchDBHostUrl + "/" + Application.getDbname());
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }

        Replication pullReplication = database.createPullReplication(syncUrl);
        pullReplication.setContinuous(true);

        Replication pushReplication = database.createPushReplication(syncUrl);
        pushReplication.setContinuous(true);

        pullReplication.start();
        pushReplication.start();

        pullReplication.addChangeListener(this);
        pushReplication.addChangeListener(this);

    }

    private void startLiveQuery(LiveQuery liveQuery, com.couchbase.lite.View view) throws Exception {

        if (liveQuery == null) {

            liveQuery = view.createQuery().toLiveQuery();
            liveQuery.addChangeListener(new LiveQuery.ChangeListener() {
                @Override
                public void changed(LiveQuery.ChangeEvent event) {
                    sendNotificationBasedInType(event.getRows());
                }
            });

            liveQuery.start();
        }

    }

    private void sendNotificationBasedInType(QueryEnumerator queryEnumerator) {

        Intent intent = new Intent();

        boolean notifyDevices = false;
        boolean notifySafezones = false;
        boolean notifyMonitorSensors = false;

        for (Iterator<QueryRow> it = queryEnumerator; it.hasNext(); ) {
            QueryRow row = it.next();
            android.util.Log.d("Document ID:", row.getDocumentId());
            if (row.getKey().toString().equals("device")) {
                notifyDevices = true;
                break;
            } else if (row.getKey().toString().equals("safezone")) {
                notifySafezones = true;
                break;
            } else if (row.getKey().toString().equals("monitoring_sensor")) {
                notifyMonitorSensors = true;
                break;
            }
        }

        if (notifyDevices) {
            intent.setAction(FragmentMyDevices.notify);
            //intent.putExtra(TemperatureActivity.VAR_NAME, finalX);
            Application.getmContext().sendBroadcast(intent);
            android.util.Log.d(TAG, "_Notify -> " + FragmentMyDevices.class.getCanonicalName());
        } else if (notifySafezones) {
            Log.d(TAG, "_Notify -> " + ActivityListSafezones.class.getCanonicalName());
            //TODO send broadcast to Safezone Activity

            intent.setAction(ActivityListSafezones.notify);
            Application.getmContext().sendBroadcast(intent);

        } else if (notifyMonitorSensors) {
            //TODO: send broadcast to monitor activity_sensors view
            android.util.Log.d(TAG, "_Notify -> Monitoring activity_sensors");
        }
    }

    @Override
    public void changed(Replication.ChangeEvent event) {
        Replication replication = event.getSource();
        android.util.Log.d(TAG, "Replication : " + replication + " changed.");
        if (!replication.isRunning()) {
            String msg = String.format("Replicator %s not running", replication);
            android.util.Log.d(TAG, msg);
        } else {
            int processed = replication.getCompletedChangesCount();
            int total = replication.getChangesCount();
            String msg = String.format("Replicator processed %d / %d", processed, total);
            android.util.Log.d(TAG, msg);
        }

    }


}
