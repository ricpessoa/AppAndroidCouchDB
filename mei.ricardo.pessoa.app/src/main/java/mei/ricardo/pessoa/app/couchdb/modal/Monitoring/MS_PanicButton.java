package mei.ricardo.pessoa.app.couchdb.modal.Monitoring;

import android.graphics.drawable.Drawable;

import com.couchbase.lite.Document;
import com.couchbase.lite.QueryEnumerator;
import com.couchbase.lite.QueryRow;

import java.util.Iterator;

import mei.ricardo.pessoa.app.Application;
import mei.ricardo.pessoa.app.R;
import mei.ricardo.pessoa.app.couchdb.modal.Device;

/**
 * Created by rpessoa on 05/06/14.
 */
public class MS_PanicButton extends MonitorSensor {
    public boolean pressed;

    public MS_PanicButton(String pressed, String mac_address, String subType, String timestamp) throws Exception {
        super(mac_address, subType, timestamp);
        this.pressed = Boolean.parseBoolean(pressed);
    }

    public boolean isPressed() {
        return pressed;
    }

    public Drawable getImage() {
        if (pressed) {
            return Application.getmContext().getResources().getDrawable(R.drawable.panic_button_small);
        }
        return null;
    }

    /**Don't need already know that button panic was pressed and the time*/
//    public static void getSensorPanicButtonByMacAddressAndSubtype(String macAddress, String timestamp, int numberResults) {
//        QueryEnumerator rowEnum = MonitorSensor.getMonitorSensorByMacAddressSubtypeTimestamp(macAddress, Device.DEVICESTYPE.panic_button.toString(), timestamp, numberResults);
//        for (Iterator<QueryRow> it = rowEnum; it.hasNext(); ) {
//                QueryRow row = it.next();
//                    Document document = row.getDocument();
//                    MS_PanicButton ms_panicButton = new MS_PanicButton(String pressed,String mac_address, String subType, String timestamp);
//            }
//    }
}