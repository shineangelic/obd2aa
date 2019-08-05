package uk.co.boconi.emil.obd2aa.auto;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import uk.co.boconi.emil.obd2aa.service.AppService;

public class CarModeReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("OBD2AA", "receiver fired");
        if ("android.app.action.ENTER_CAR_MODE".equalsIgnoreCase(intent.getAction())) {
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
            Log.d("OBD2AA", "Should start the service now");
            Intent starts = new Intent(context, AppService.class);
            context.startService(starts);

            if (prefs.getBoolean("fartkontrol", false)) {
                Intent LaunchIntent = context.getPackageManager().getLaunchIntentForPackage("nu.fartkontrol.app");
                if (LaunchIntent != null) {
                    context.startActivity(LaunchIntent);
                }
            }


        } else if ("android.app.action.EXIT_CAR_MODE".equalsIgnoreCase(intent.getAction())){
            Log.d("OBD2AA", "Should stop the service now");
            Intent stopIntent = new Intent(context, AppService.class);
            context.stopService(stopIntent);
            //OBD2_Background.stop() = false;
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);

            if (prefs.getBoolean("fartkontrol", false)) {
                Intent paramIntent = context.getPackageManager().getLaunchIntentForPackage("nu.fartkontrol.app");
                if (paramIntent != null) {
                    paramIntent.putExtra("BLUETOOTH_END", true);
                    context.startActivity(paramIntent);
                }
            }

        } else if ("android.app.action.DOCK_EVENT".equalsIgnoreCase(intent.getAction())) {
            Log.w("OBD2AA", "unmanaged dock event");
        } else{
            Log.e("OBD2AA", "ERROR!! unknown event: " + intent.getAction());
        }
    }

}
