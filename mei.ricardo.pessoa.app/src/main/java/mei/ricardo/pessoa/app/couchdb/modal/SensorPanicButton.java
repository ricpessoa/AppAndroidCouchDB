package mei.ricardo.pessoa.app.couchdb.modal;

/**
 * Created by rpessoa on 12/07/14.
 */
public class SensorPanicButton extends Sensor {
    private static String TAG = SensorPanicButton.class.getCanonicalName();

    public SensorPanicButton() {
        super("SensorPanicButton", Device.DEVICESTYPE.panic_button.toString());
    }

    public SensorPanicButton(String name_sensor, String type) {
        super(name_sensor, type);
    }
}
