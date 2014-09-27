package mei.ricardo.pessoa.app.utils.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Created by rpessoa on 27-09-2014.
 */
public class BootCompletedReceiver extends BroadcastReceiver {
    private static String TAG = BootCompletedReceiver.class.getCanonicalName();

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "start the service on boot device");
        Intent startServiceIntent = new Intent(context, AppService.class);
        context.startService(startServiceIntent);
    }
}
