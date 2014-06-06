package mei.ricardo.pessoa.app.couchdb.modal.Monitoring;

/**
 * Created by rpessoa on 05/06/14.
 */
public class MS_PanicButton extends MonitorSensor{
    public boolean pressed;

    public MS_PanicButton(String pressed, String subType, String timestamp) throws Exception{
        super(subType,timestamp);
        this.pressed = Boolean.parseBoolean(pressed);
    }

    public boolean isPressed() {
        return pressed;
    }
}
