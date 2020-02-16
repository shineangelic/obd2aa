package uk.co.boconi.emil.obd2aa.cameras;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.preference.PreferenceManager;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

import uk.co.boconi.emil.obd2aa.ui.activity.AppSettings;

public class DownloadHelper {
    private static String mobileURL = "http://data.blitzer.de/output/mobile.php?v=4&key=d83nv9dj38FQ";
    private static String staticURL = "http://data.blitzer.de/output/static.php?v=4";
    private static String MOBILE_DB_PATH = "/data/data/uk.co.boconi.emil.obd2aa/databases/mobilecamera";
    private static String STATIC_DB_PATH = "/data/data/uk.co.boconi.emil.obd2aa/databases/fixedcamera";

    public DownloadHelper(final int type, final Context context, final int freq) {
        Thread downloadThread = new Thread() {
            @Override
            public void run() {
                try {
                    URL url;
                    String dbName;
                    if (type == 1 && freq != 0) {
                        dbName = "mobilecamera";
                        File file = new File(MOBILE_DB_PATH);
                        if (file.exists() && ((System.currentTimeMillis() - file.lastModified()) / 1000) < freq) //Only download a new file every 5 minutes;
                            return;
                        url = new URL(mobileURL);
                    } else {
                        File file = new File(STATIC_DB_PATH);
                        dbName = "fixedcamera";
                        if (file.exists() && ((System.currentTimeMillis() - file.lastModified()) / 1000) < 604800 && freq != 1)  //Only download a new static database file every 7 days.
                            return;
                        url = new URL(staticURL);
                    }
                    HttpURLConnection urlConn = (HttpURLConnection)url.openConnection();
                    urlConn.setRequestMethod("GET");
                    urlConn.setDoOutput(true);
                    urlConn.connect();

                    int contentLength = urlConn.getContentLength();
                    DataInputStream stream = new DataInputStream(url.openStream());

                    byte[] buffer = new byte[contentLength];
                    stream.readFully(buffer);
                    stream.close();
                    CameraDataBaseHelper cdbh = new CameraDataBaseHelper(context, type, dbName);
                    cdbh.createDataBase();
                    cdbh.copyDataBase(buffer);
                    Log.d("OBD2AA", "Download has been completed!");
                    if (context instanceof AppSettings) {
                        ((AppSettings) context).showDownload_comp_message(type);
                    }
                } catch (Exception e) {
                    Log.e("OBD2AA", "exception", e);
                }
            }
        };

        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();
        boolean isWiFi = activeNetwork != null && activeNetwork.getType() == ConnectivityManager.TYPE_WIFI;
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        boolean use_mobile = prefs.getBoolean("use_mobile", true);
        if (isConnected && use_mobile)
            downloadThread.start();
        else if (isConnected && isWiFi)
            downloadThread.start();
    }
}
