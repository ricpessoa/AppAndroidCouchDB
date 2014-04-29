package mei.ricardo.pessoa.app;

import android.util.Log;

/**
 * Created by rpessoa on 29/04/14.
 */
public class Application extends android.app.Application {
    private static String TAG = Application.class.getName();

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "Start Application ");
    }

}
