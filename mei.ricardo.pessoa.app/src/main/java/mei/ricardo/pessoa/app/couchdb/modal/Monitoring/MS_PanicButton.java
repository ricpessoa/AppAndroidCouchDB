package mei.ricardo.pessoa.app.couchdb.modal.Monitoring;

import android.graphics.drawable.Drawable;

import com.couchbase.lite.Document;

import mei.ricardo.pessoa.app.Application;
import mei.ricardo.pessoa.app.R;

/**
 * Created by rpessoa on 05/06/14.
 */
public class MS_PanicButton extends MonitorSensor {
    public boolean pressed;

    public MS_PanicButton(String pressed, String mac_address, String subType, String timestamp) throws Exception {
        super(mac_address, subType, timestamp);
        this.pressed = Boolean.parseBoolean(pressed);
    }

    public MS_PanicButton(Document document) throws Exception {
        super(document.getProperty("mac_address").toString(), document.getProperty("subtype").toString(), document.getProperty("timestamp").toString());
        this.pressed = Boolean.parseBoolean(document.getProperty("pressed").toString());
    }

    public boolean isPressed() {
        return pressed;
    }

    public Drawable getImage() {
        if (pressed) {
            return Application.getmContext().getResources().getDrawable(R.drawable.panic_button);
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