package mei.ricardo.pessoa.app.couchdb.modal.Monitoring;

/**
 * Created by rpessoa on 05/06/14.
 */
public class MS_Temperature extends MonitorSensor{
    private float value;


    public MS_Temperature(String value, String subType, String timestamp) throws Exception{
        super(subType,timestamp);
        this.value = Float.parseFloat(value);
    }

    public float getValue() {
        return value;
    }
}
