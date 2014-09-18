package mei.ricardo.pessoa.app.couchdb;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.widget.Toast;

import com.couchbase.lite.Database;
import com.couchbase.lite.Document;
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
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import mei.ricardo.pessoa.app.Application;
import mei.ricardo.pessoa.app.R;
import mei.ricardo.pessoa.app.couchdb.modal.Device;
import mei.ricardo.pessoa.app.couchdb.modal.MS_Notification;
import mei.ricardo.pessoa.app.couchdb.modal.Monitoring.MS_Battery;
import mei.ricardo.pessoa.app.couchdb.modal.Monitoring.MS_GPS;
import mei.ricardo.pessoa.app.couchdb.modal.Monitoring.MS_PanicButton;
import mei.ricardo.pessoa.app.couchdb.modal.Monitoring.MS_Temperature;
import mei.ricardo.pessoa.app.couchdb.modal.Monitoring.MonitorSensor;
import mei.ricardo.pessoa.app.couchdb.modal.Safezone;
import mei.ricardo.pessoa.app.couchdb.modal.Settings;
import mei.ricardo.pessoa.app.ui.Fragments.FragmentMyDashboard;
import mei.ricardo.pessoa.app.ui.Fragments.FragmentMyDevices;
import mei.ricardo.pessoa.app.ui.Fragments.FragmentNotification;
import mei.ricardo.pessoa.app.ui.MainActivity;
import mei.ricardo.pessoa.app.ui.Sensor.Safezone.ActivityListSafezones;
import mei.ricardo.pessoa.app.ui.SettingsActivity;

/**
 * Created by rpessoa on 12/05/14.
 */
public class CouchDB extends Service implements Replication.ChangeListener {
    private static String TAG = CouchDB.class.getName();
    private static final int NOTIFICATION_ID = 12743;

    private static CouchDB mCouchDBinstance = null;

    private static Database database = null;
    private static Manager mCouchManager = null;

    //couch internals
    public static View viewGetDevices;
    public static View viewGetSafezonesToFilterWithMacAddress;
    public static View viewGetSafezones;
    public static View viewGetMonitoringSensorsFromLastDay;
    public static View viewGetDevicesMonitoring;
    public static View viewGetMonitorSensor;
    public static View viewGetSettingApplication;
    private LiveQuery liveQueryDevice;
    private LiveQuery liveQueryGetSafezones;
    private LiveQuery liveQueryMonitoringSensors;
    private LiveQuery liveQuerySettings;

    public CouchDB() {
        try {
            startCBLite();
        } catch (Exception e) {
            Toast.makeText(Application.getmContext(), "Error Initializing CBLIte, see logs for details", Toast.LENGTH_LONG).show();
            Log.e(TAG, "Error initializing CBLite", e);
        }

    }

    @Override
    public void onCreate() {
        super.onCreate();
        android.util.Log.d(TAG, "onCreate");

        Notification notification = new Notification(R.drawable.ic_launcher, "Service GPS status", System.currentTimeMillis());

        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, MainActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP), 0);

        notification.setLatestEventInfo(this, getString(R.string.str_service_on_running_title), getString(R.string.str_service_on_running_description), contentIntent);

        startForeground(NOTIFICATION_ID, notification);

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return Service.START_NOT_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
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

    /**
     * When logout user need delete instance
     */
    public void setCouchDBToNull() {
        mCouchDBinstance = null;
        mCouchManager = null;
        database = null;
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
        final String designDocName = "appView";


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

        String DevicesToMonitoringViewName = "getDevicesToMonitoring";
        viewGetDevicesMonitoring = database.getView(String.format("%s/%s", designDocName, DevicesToMonitoringViewName));
        viewGetDevicesMonitoring.setMap(new Mapper() {
            @Override
            public void map(Map<String, Object> document, Emitter emitter) {
                Object objDevice = document.get("type");
                Object monitoring = document.get("monitoring");
                Object objDeviceDeleted = document.get("deleted");
                if (objDeviceDeleted == null) {
                    objDeviceDeleted = false;
                }
                if (objDevice != null && objDevice.equals("device") && monitoring != null && monitoring.equals(true) && objDeviceDeleted.equals(false)) {
                    emitter.emit(objDevice.toString(), document);
                }
            }
        }, "3.0");

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
                    Object[] objects = new Object[]{mac_address.toString(), subtype.toString(), timestamp.toString()};
                    emitter.emit(objects, document);
                }
            }
        }, "1.0");

        /**this views is to live querys receiving changes on devices*/
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

        /** this views is to receiving changes on safezones*/
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

        /** View get monitoring sensors from the previous day to now and not seen*/
        String getMonitorSensorsFromDay = "getMonitorSensorsFromDay";
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, -1);
        final long actualTimestamp = calendar.getTimeInMillis() / 1000;

        viewGetMonitoringSensorsFromLastDay = database.getView(String.format("%s/%s", designDocName, getMonitorSensorsFromDay));
        viewGetMonitoringSensorsFromLastDay.setMap(new Mapper() {
            @Override
            public void map(Map<String, Object> document, Emitter emitter) {
                Object objType = document.get("type");
                Object objTimestamp = document.get("timestamp");
                Object objectSeen = document.get("seen");
                long timestamp = Long.parseLong(objTimestamp.toString());
                if (objType != null && objType.equals("monitoring_sensor")) {
                    if (timestamp > actualTimestamp) {
                        if (objectSeen == null) {
                            emitter.emit(objTimestamp.toString(), document);
                        }
                    }
                }
            }
        }, "1.0");

        startLiveQueryMonitoringSensor(liveQueryMonitoringSensors, viewGetMonitoringSensorsFromLastDay);

        String getSettingsOfApp = "getSettingsOfApp";
        viewGetSettingApplication = database.getView(String.format("%s/%s", designDocName, getSettingsOfApp));
        viewGetSettingApplication.setMap(new Mapper() {
            @Override
            public void map(Map<String, Object> document, Emitter emitter) {
                Object objType = document.get("type");
                if (objType != null && objType.equals("settings")) {
                    emitter.emit(objType.toString(), document);
                }
            }
        }, "1.0");
        startLiveQuery(liveQuerySettings, viewGetSettingApplication);


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

    private void startLiveQueryMonitoringSensor(LiveQuery liveQuery, com.couchbase.lite.View view) throws Exception {
        if (liveQuery == null) {
            liveQuery = view.createQuery().toLiveQuery();
            liveQuery.setDescending(true);
            liveQuery.addChangeListener(new LiveQuery.ChangeListener() {
                @Override
                public void changed(LiveQuery.ChangeEvent event) {
                    sendNotificationOfMonitoringSensor(event.getRows());
                }
            });

            liveQuery.start();
        }

    }

    private Device actualDevice = null;

//    private void sendNotificationOfMonitoringSensor(QueryEnumerator queryEnumerator) {
//        List<MS_Notification> MSNotificationList = new ArrayList<MS_Notification>();
//
//        for (Iterator<QueryRow> it = queryEnumerator; it.hasNext(); ) {
//            QueryRow row = it.next();
//
//            Document document = row.getDocument();
//            Object objectSubType = document.getProperty("subtype");
//            Object objectMacAddress = document.getProperty("mac_address");
//            Object actualTimestamp = document.getProperty("timestamp");
//        }
//    }


    private void sendNotificationOfMonitoringSensor(QueryEnumerator queryEnumerator) {
        List<MS_Notification> MSNotificationList = new ArrayList<MS_Notification>();
        String monitorSensorMacAddress = "";
        MS_Notification MSNotification = new MS_Notification();
        int sensorCount = 0;
        String previousTimestamp = null; //this timestamp is to know if ms_monitoring is about the same
        for (Iterator<QueryRow> it = queryEnumerator; it.hasNext(); ) {
            QueryRow row = it.next();

            Document document = row.getDocument();
            Object objectSubType = document.getProperty(MonitorSensor.doc_subtype);
            Object objectMacAddress = document.getProperty(MonitorSensor.doc_mac_adress);
            Object actualTimestamp = document.getProperty(MonitorSensor.doc_timestamp);
            //Object actualNotification = document.getProperty(MonitorSensor.doc_notification);

            if (previousTimestamp == null) {
                previousTimestamp = actualTimestamp.toString();
            }

            if (objectSubType != null && objectMacAddress != null) {
                //actualDevice.testeDecideIfNeedNotification(objectSubType.toString());
                MonitorSensor.setSeenToDocument(document); // to update monitoring seen = true
                if (actualDevice == null) {
                    actualDevice = Device.getDeviceByID(objectMacAddress.toString());
                    actualDevice.getArrayListSensors();
                } else {
                    if (!actualDevice.getMac_address().equals(objectMacAddress)) {
                        actualDevice = Device.getDeviceByID(objectMacAddress.toString());
                        MSNotificationList.add(MSNotification);
                        MSNotification = new MS_Notification();
                        sensorCount = 0;
                    }
                    if (!actualTimestamp.toString().equals(previousTimestamp)) {
                        if (sensorCount > 0) {
                            MSNotificationList.add(MSNotification);
                            MSNotification = new MS_Notification();
                        }
                        sensorCount = 0;
                        previousTimestamp = actualTimestamp.toString();
                    }
                }
                monitorSensorMacAddress = objectMacAddress.toString();
                if (objectSubType.equals(MonitorSensor.SUBTYPE.GPS.toString())) {
                    try {
                        MS_GPS ms_gps = new MS_GPS(document);
                        MSNotification.setGpsNotification(ms_gps);
                        sensorCount++;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else if (objectSubType.equals(MonitorSensor.SUBTYPE.panic_button.toString())) {
                    try {
                        MS_PanicButton ms_panicButton = new MS_PanicButton(document);
                        MSNotification.setPanicButtonNotification(ms_panicButton);
                        sensorCount++;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else if (objectSubType.equals(MonitorSensor.SUBTYPE.battery.toString())) {
                    try {
                        MS_Battery ms_battery = new MS_Battery(document);
                        if(ms_battery.isNecessaryNotify()) {
                            MSNotification.setBatteryNotification(ms_battery);
                        }
                        sensorCount++;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else if (objectSubType.equals(MonitorSensor.SUBTYPE.temperature.toString())) {
                    try {
                        MS_Temperature ms_temperature = new MS_Temperature(document);
                        if (ms_temperature.isNecessaryNotify()) {
                            MSNotification.setTemperatureNotification(ms_temperature);
                        }
                        sensorCount++;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
                if (actualDevice.getArrayListSensors().size() == sensorCount) {
                    MSNotificationList.add(MSNotification);
                    MSNotification = new MS_Notification();
                    sensorCount = 0;
                    previousTimestamp = actualTimestamp.toString();
                } else if(!it.hasNext()){ //case device dont send all sensors
                    MSNotificationList.add(MSNotification);
                    MSNotification = new MS_Notification();
                    sensorCount = 0;
                    previousTimestamp = actualTimestamp.toString();
                }

            }
        }

        if (MSNotificationList.size() > 0) {
            MS_Notification.sendNotificationToUser(Application.getmContext(), MSNotificationList); //send to class Notification

            Intent intent = new Intent();
            intent.setAction(MainActivity.notifyMonitorSensor);
            intent.putExtra(FragmentMyDashboard.passVariableMacAddress, monitorSensorMacAddress);
            Application.getmContext().sendBroadcast(intent);
        }

    }

    private void sendNotificationBasedInType(QueryEnumerator queryEnumerator) {

        Intent intent = new Intent();

        boolean notifyDevices = false;
        boolean notifySafezones = false;
        boolean notifyMonitorSensors = false;
        boolean notifySettings = false;

        for (Iterator<QueryRow> it = queryEnumerator; it.hasNext(); ) {
            QueryRow row = it.next();

            if (row.getKey().toString().equals(MonitorSensor.type)) {
                notifyMonitorSensors = true;
                break;
            } else if (row.getKey().toString().equals(Device.type)) {
                notifyDevices = true;
                break;
            } else if (row.getKey().toString().equals(Safezone.type)) {
                notifySafezones = true;
                break;
            } else if (row.getKey().toString().equals(Settings.type)) {
                notifySettings = true;
                break;
            }
        }

        if (notifyDevices) {
            intent.setAction(FragmentMyDevices.notify);
            Application.getmContext().sendBroadcast(intent);
            android.util.Log.d(TAG, "_Notify -> " + FragmentMyDevices.class.getCanonicalName());
        } else if (notifySafezones) {
            Log.d(TAG, "_Notify -> " + ActivityListSafezones.class.getCanonicalName());
            intent.setAction(ActivityListSafezones.notify);
            Application.getmContext().sendBroadcast(intent);
        } else if (notifyMonitorSensors) {
            Log.d(TAG, "_Notify -> Monitoring ");
            intent.setAction(FragmentNotification.notify);
            Application.getmContext().sendBroadcast(intent);
        } else if (notifySettings) {
            Log.d(TAG, "_Notify -> Settings ");
            intent.setAction(SettingsActivity.notify);
            Application.getmContext().sendBroadcast(intent);
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
