package mei.ricardo.pessoa.app.couchdb.modal;

/**
 * Created by rpessoa on 12/07/14.
 */
public class SensorPanicButton extends Sensor {
    private static String TAG = SensorPanicButton.class.getCanonicalName();

    public SensorPanicButton(){
        super("Sensor Panic Button", Device.DEVICESTYPE.panic_button.toString());
    }
    public SensorPanicButton(boolean isEnable) {
        super("Sensor Panic Button", Device.DEVICESTYPE.panic_button.toString(), isEnable);
    }

}
