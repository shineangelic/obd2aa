package uk.co.boconi.emil.obd2aa.ui.activity;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.RemoteException;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.TextView;

import com.google.android.apps.auto.sdk.CarActivity;
import com.google.android.apps.auto.sdk.CarUiController;
import com.google.android.apps.auto.sdk.MenuController;

import java.util.HashMap;
import java.util.Map;

import uk.co.boconi.emil.obd2aa.R;
import uk.co.boconi.emil.obd2aa.auto.AAMenu;
import uk.co.boconi.emil.obd2aa.service.AppService;
import uk.co.boconi.emil.obd2aa.util.UnitConversionUtil;

public class TPMSActivity extends CarActivity {

    private boolean isRunning;
    private String myunit;
    private boolean isDebugging;
    public final Handler handler = new Handler(Looper.getMainLooper()) {
        public void handleMessage(Message msg) {
            if (!isRunning)
                return;

            float[] tpmsvals = (float[]) msg.obj;
            if (isDebugging)
                Log.d("OBD2AA", "Received message float:" + +tpmsvals[0] + " " + tpmsvals[1] + myunit);

            TextView tv = (TextView) findViewById(R.id.front_left);
            tv.setText(String.format("%.2f", tpmsvals[0]) + " " + myunit.toUpperCase());
            tv = (TextView) findViewById(R.id.front_right);
            tv.setText(String.format("%.2f", tpmsvals[1]) + " " + myunit.toUpperCase());
            tv = (TextView) findViewById(R.id.rear_left);
            tv.setText(String.format("%.2f", tpmsvals[2]) + " " + myunit.toUpperCase());
            tv = (TextView) findViewById(R.id.rear_right);
            tv.setText(String.format("%.2f", tpmsvals[3]) + " " + myunit.toUpperCase());
        }
    };

    private boolean isDemoMode;
    private AppService mAppService;
    private ServiceConnection mConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName arg0, IBinder service) {
            Log.d("HU", "BACKGROUND SERVICE CONNECTED!");
            AppService.LocalBinder binder = (AppService.LocalBinder) service;
            mAppService = binder.getService();
            if (mAppService.ecuconnected)
                monitor_pressure();
        }

        public void onServiceDisconnected(ComponentName name) {
            mAppService = null;
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cartyreview);
    }

    @Override
    public void onPause() {
        super.onPause();
        isRunning = false;
        try {
            unbindService(mConnection);
        } catch (Exception E) {
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        isDebugging = PreferenceManager.getDefaultSharedPreferences(this).getBoolean("debugging", false);
        isDemoMode = PreferenceManager.getDefaultSharedPreferences(this).getBoolean("demomode", false);
        if (isDemoMode)
            myunit = " PSI";
        CarUiController paramMap = getCarUiController();
        paramMap.getStatusBarController().setDayNightStyle(2);
        Map<String, String> MenuMap = new HashMap<>();
        MenuMap.put("tpms", "TPMS");
        MenuMap.put("obd2", "OBD2");
        AAMenu xxx = new AAMenu();
        xxx.a(MenuMap);
        MenuController localMenuController = paramMap.getMenuController();
        localMenuController.setRootMenuAdapter(xxx);
        localMenuController.showMenuButton();
        xxx.updateActivity(TPMSActivity.this);
        Intent intent = new Intent(TPMSActivity.this, AppService.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(intent);
        }
        else {
            startService(intent);
        }
        bindService(intent, mConnection, 0);
        Log.d("OBD2AA", "TPMS View loaded.");
    }

    private void monitor_pressure() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        final String[] tpms_pids = {prefs.getString("front_left_pressure", ""), prefs.getString("front_right_pressure", ""), prefs.getString("rear_left_pressure", ""), prefs.getString("rear_right_pressure", "")};

        isRunning = true;
        Thread tpms_mon = new Thread() {
            String unit = null;
            Boolean needsconversion = false;

            public void run() {
                while (isRunning) {
                    if (isDemoMode) {
                        float[] tpmsvals = {33.2f, 33.1f, 32.8f, 32.4f};
                        Message msg = new Message();
                        msg.obj = tpmsvals;
                        handler.sendMessage(msg);
                    } else if (!isDemoMode && mAppService.torqueService != null) {
                        try {
                            if (unit == null) {
                                String[] desc = mAppService.torqueService.getPIDInformation(tpms_pids);
                                if (!desc[0].split(",")[2].equalsIgnoreCase(mAppService.torqueService.getPreferredUnit(desc[0].split(",")[2]))) {
                                    needsconversion = true;
                                }
                                unit = mAppService.torqueService.getPreferredUnit(desc[0].split(",")[2]);
                                myunit = unit;
                            }
                            float[] tpmsvals = mAppService.torqueService.getPIDValues(tpms_pids);
                            if (needsconversion)
                                for (int i = 0; i < 4; i++) {
                                    tpmsvals[i] = UnitConversionUtil.ConvertValue(tpmsvals[i], unit);
                                }
                            Message msg = new Message();
                            msg.obj = tpmsvals;
                            if (isDebugging)
                                Log.d("OBD2AA", "About to post message float:" + tpmsvals[0] + " " + tpmsvals[1] + tpms_pids[0] + " " + tpms_pids[1]);
                            handler.sendMessage(msg);


                        } catch (RemoteException e) {
                            e.printStackTrace();
                        }
                    }
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        tpms_mon.start();
    }

}
