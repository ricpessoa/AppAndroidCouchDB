package mei.ricardo.pessoa.app.couchdb.modal;

/**
 * Created by rpessoa on 27-09-2014.
 */
public class SensorShoe extends Sensor{
    private static String TAG = SensorPanicButton.class.getCanonicalName();

    public SensorShoe(){
        super("Sensor Shoe", Device.DEVICESTYPE.panic_button.toString());
    }
    public SensorShoe(boolean isEnable) {
        super("Sensor Shoe", Device.DEVICESTYPE.panic_button.toString(), isEnable);
    }

}
