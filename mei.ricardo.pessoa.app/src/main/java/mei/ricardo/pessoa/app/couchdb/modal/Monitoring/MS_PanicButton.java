package mei.ricardo.pessoa.app.couchdb.modal.Monitoring;

import android.graphics.drawable.Drawable;

import mei.ricardo.pessoa.app.Application;
import mei.ricardo.pessoa.app.R;

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

    public Drawable getImage() {
        if (pressed){
            return Application.getmContext().getResources().getDrawable(R.drawable.panic_button_small);
        }
        return null;
    }
}
