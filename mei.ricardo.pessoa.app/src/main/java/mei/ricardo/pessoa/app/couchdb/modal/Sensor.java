package mei.ricardo.pessoa.app.couchdb.modal;

/**
 * Created by rpessoa on 06/05/14.
 */
public class Sensor {
    private String name_sensor;
    private String type;
    private boolean enable;

    public Sensor(String name_sensor, String type) {
        this.name_sensor = name_sensor;
        this.type = type;
    }

    public String getName_sensor() {
        return name_sensor;
    }

    public void setName_sensor(String name_sensor) {
        this.name_sensor = name_sensor;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }

    public boolean isEnable() {
        return enable;
    }
}
