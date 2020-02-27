package uk.co.boconi.emil.obd2aa.auto;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.support.v4.app.NotificationCompat;

import com.google.android.apps.auto.sdk.notification.CarNotificationExtender;

import uk.co.boconi.emil.obd2aa.R;

/**
 * Notification listener. This class will show CamSam and Blitzer.De notifications inside Android Auto
 * Can be future extended to show notifications from other apps.
 */
public class CarNotificationListener extends NotificationListenerService {

    private Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
    }

    @Override
    public void onNotificationRemoved(StatusBarNotification sbn) {
        //Nothing to do here
    }

    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {
        // if (!AppService.isrunning)
        //  return;
        int icon = 0;
        String pack = sbn.getPackageName();
        if (pack.equalsIgnoreCase("nu.fartkontrol.app")) {
            icon = R.drawable.speed_camera_sam;
        } else {
            return;
        }

        Bundle extras = sbn.getNotification().extras;
        if (extras.getString("android.title") == null || extras.getString("android.title").equalsIgnoreCase("Fartkontrol.nu")) {
            //Log.d("ODB2AA","Title null or Only CamSam");
            return;
        }

        final NotificationManager mNotifyMgr = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        String title = "Speed Camera ahead";
        String text = extras.getString("android.title");
        Bitmap bmp = BitmapFactory.decodeResource(getResources(), icon);
        if (pack.equalsIgnoreCase("nu.fartkontrol.app")) {
            title = extras.getString("android.title");
            text = extras.getString("android.text");
            bmp = extras.getParcelable(Notification.EXTRA_LARGE_ICON);
        }

        CarNotificationExtender paramString2 = new CarNotificationExtender.Builder()
                .setTitle(title)
                .setSubtitle(text)
                .setShouldShowAsHeadsUp(true)
                .setActionIconResId(R.drawable.ic_danger_r)
                .setBackgroundColor(Color.WHITE)
                .setNightBackgroundColor(Color.DKGRAY)
                .setThumbnail(bmp)
                .build();

        NotificationCompat.Builder notification = new NotificationCompat.Builder(context)
                .setContentTitle(title)
                .setContentText(text)
                .setLargeIcon(bmp)
                .setSmallIcon(R.drawable.ic_danger_r)
                .setColor(Color.GRAY)
                .extend(paramString2);


        mNotifyMgr.notify(1983, notification.build());
    }

}
