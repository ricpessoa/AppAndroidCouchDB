package mei.ricardo.pessoa.app.couchdb.modal;

/**
 * Created by rpessoa on 13/05/14.
 */
public class SensorTemperature extends Sensor{
    private float min_temperature;
    private float max_temperature;

    public SensorTemperature(String name_sensor, String type) {
        super(name_sensor,type);
    }


    public SensorTemperature(String name, String type, float min_temperature,float max_temperature){
        super(name,type);
        this.min_temperature = min_temperature;
        this.max_temperature = max_temperature;
    }

    public float getMax_temperature() {
        return max_temperature;
    }

    public void setMax_temperature(float max_temperature) {
        this.max_temperature = max_temperature;
    }

    public float getMin_temperature() {
        return min_temperature;
    }

    public void setMin_temperature(float min_temperature) {
        this.min_temperature = min_temperature;
    }
}
