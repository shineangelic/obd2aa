package uk.co.boconi.emil.obd2aa;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.constraint.ConstraintLayout;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.TableLayout;

import com.google.android.apps.auto.sdk.CarActivity;
import com.google.android.apps.auto.sdk.CarUiController;
import com.google.android.apps.auto.sdk.MenuController;
import com.google.android.gms.car.CarSensorManager;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import uk.co.boconi.emil.obd2aa.auto.AAMenu;
import uk.co.boconi.emil.obd2aa.service.AppService;
import uk.co.boconi.emil.obd2aa.ui.gauge.ArcAnimation;
import uk.co.boconi.emil.obd2aa.ui.gauge.ArcProgress;
import uk.co.boconi.emil.obd2aa.ui.gauge.DrawGauges;

import static java.lang.Integer.parseInt;

public class OBD2AA extends CarActivity implements CarSensorManager.CarSensorEventListener {

    private ArcProgress arcProgress;
    private SharedPreferences prefs;

    private int gauge_number;
    private AppService mAppService;
    private String[] pids;
    private long[] lastpiddraw;
    private boolean isshowing;

    public final Handler handler = new Handler(Looper.getMainLooper()) {
        public void handleMessage(Message msg) {
            if (!isshowing)
                return;
            // Log.d("OBD2APP","Handler: "+msg.arg2+ " value: "+msg.arg1);
            TableLayout tv = null;
            try {
                tv = (TableLayout) findViewById(R.id.tv_log);
            } catch (NullPointerException E) {
                throw E;
            }

            if (tv == null)
                return;
            float myvals = (float) msg.obj;

            int i = msg.arg1;
            arcProgress = (ArcProgress) tv.findViewWithTag("gauge_" + (i + 1));
            if (arcProgress == null)
                return;
            if (myvals > arcProgress.getMax()) {
                //Check if the given PID needs the MAX value to be updated.

                myvals = arcProgress.getMax();
            }

            ArcAnimation animation = new ArcAnimation(arcProgress, myvals);
            if (animation == null)
                return;
            animation.setDuration(170);
            arcProgress.startAnimation(animation);
        }
    };
    private boolean useDigital;
    private String[] units;
    private Boolean[] convertunits;
    private Boolean isdebugging;
    private MediaPlayer mediaPlayer;
    private boolean prepared;
    private ServiceConnection mConnection = new ServiceConnection() {

        public void onServiceConnected(ComponentName arg0, IBinder service) {
            Log.d("HU", "BACKGROUND SERVICE CONNECTED!");
            AppService.LocalBinder binder = (AppService.LocalBinder) service;
            mAppService = binder.getService();
            mAppService.OBD2AA_update(OBD2AA.this);
            if (prefs.getBoolean("custombg", false)) {
                File imgFile = new File(prefs.getString("custom_bg_path", ""));
                if (imgFile.exists()) {
                    Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                    ConstraintLayout cl = (ConstraintLayout) findViewById(R.id.activity_odb2_a);
                    Bitmap resized = Bitmap.createScaledBitmap(myBitmap, cl.getWidth(), cl.getHeight(), true);
                    BitmapDrawable bitmapDrawable = new BitmapDrawable(getApplicationContext().getResources(), resized);
                    cl.setBackground(bitmapDrawable);
                }
            }
            TableLayout mywrapper = (TableLayout) findViewById(R.id.tv_log);
            DrawGauges gauge = new DrawGauges();
            gauge.SetUpandDraw(OBD2AA.this, mywrapper.getWidth(), mywrapper.getHeight(), mywrapper, mAppService);

        }

        public void onServiceDisconnected(ComponentName name) {
            mAppService = null;
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_odb2_a);

        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        gauge_number = prefs.getInt("gauge_number", 0);
        useDigital = prefs.getBoolean("usedigital", false);
        pids = new String[gauge_number];
        lastpiddraw = new long[gauge_number];
        units = new String[gauge_number];
        convertunits = new Boolean[gauge_number];
        isdebugging = prefs.getBoolean("debugging", false);
        //d().findViewById();
        Log.d("OBD2AA", "OBD2AA APP STARTED, BEFORE BIND.");
    }

    @Override
    public void onPause() {
        super.onPause();
        isshowing = false;
        Log.d("OBD2-APP", "On Pause");
        if (mAppService != null)
            mAppService.OBD2AA_update(null);
        if (mConnection != null)
            try {
                unbindService(mConnection);
            } catch (Exception E) {
                Log.e("OBD2AA", "Cannot unbind something which is not registered.");
            }
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d("OBD2AA", "OBD2AA On resume");
        final ConstraintLayout layout = (ConstraintLayout) findViewById(R.id.activity_odb2_a);
        ViewTreeObserver vto = layout.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if (mAppService != null)
                    mAppService.OBD2AA_update(OBD2AA.this);
                else {
                    Intent intent = new Intent(OBD2AA.this, AppService.class);
                    intent.putExtra("muststartTorque", true);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        startForegroundService(intent);
                    }
                    else {
                        startService(intent);
                    }
                    bindService(intent, mConnection, 0);
                }
                CarUiController paramMap = getCarUiController();

                /*
                try {
                    CarInfoManager.CarInfo localCarInfo = ((CarInfoManager) a("info")).loadCarInfo();
                    CarInfoManager.CarUiInfo localCarUIInfo = ((CarInfoManager) a("info")).loadCarUiInfo();
                    int[] xyz = ((CarSensorManager) a("sensor")).getSupportedSensors();
                    //((CarSensorManager) a("sensor")).registerListener(OBD2AA.this,9,0);
                    Log.d("OBD2AA","Car packaged: " + (a("package")).toString());

                    Log.d("OBD2AA","Car info: " + localCarInfo.getHeadUnitMake() + " " + localCarInfo.getHeadUnitSoftwareVersion() + "UI: " + localCarUIInfo.getTouchscreenType() + "Supported sensor: " +xyz[0] + "," + xyz[1] );
                } catch (CarNotConnectedException e) {
                    e.printStackTrace();
                } catch (CarNotSupportedException e) {
                    e.printStackTrace();
                }
                */
                paramMap.getStatusBarController().setDayNightStyle(2);
                Map<String, String> MenuMap = new HashMap<>();
                MenuMap.put("tpms", "TPMS");
                MenuMap.put("obd2", "OBD2");
                AAMenu xxx = new AAMenu();
                xxx.a(MenuMap);
                MenuController localMenuController = paramMap.getMenuController();
                localMenuController.setRootMenuAdapter(xxx);
                localMenuController.showMenuButton();
                xxx.updateActivity(OBD2AA.this);


                isshowing = true;

                if (mAppService != null) {
                    if (prefs.getBoolean("custombg", false)) {
                        File imgFile = new File(prefs.getString("custom_bg_path", ""));
                        if (imgFile.exists()) {
                            Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                            ConstraintLayout cl = (ConstraintLayout) findViewById(R.id.activity_odb2_a);
                            Bitmap resized = Bitmap.createScaledBitmap(myBitmap, cl.getWidth(), cl.getHeight(), true);
                            BitmapDrawable bitmapDrawable = new BitmapDrawable(getApplicationContext().getResources(), resized);
                            cl.setBackground(bitmapDrawable);
                        }
                    }
                    TableLayout mywrapper = (TableLayout) findViewById(R.id.tv_log);
                    mywrapper.removeAllViews();
                    DrawGauges gauge = new DrawGauges();
                    gauge.SetUpandDraw(OBD2AA.this, mywrapper.getWidth(), mywrapper.getHeight(), mywrapper, mAppService);
                }

                if (!prefs.contains("def_color_selector"))
                    do_nosettings();

                layout.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        });

    }

    public void do_nosettings() {
        handler.post(new Runnable() {
            @Override
            public void run() {
                Log.d("OBD2AA", "DO ECU CONNECTED....");
                TableLayout mywrapper = (TableLayout) findViewById(R.id.tv_log);
                FrameLayout ecunot = (FrameLayout) findViewById(R.id.NoECU);
                ecunot.setVisibility(View.VISIBLE);
                mywrapper.setVisibility(View.GONE);
            }
        });
    }

    public void update_gauge_max(final int i, final float i1) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                TableLayout tv = (TableLayout) findViewById(R.id.tv_log);
                if (tv == null)
                    return;
                arcProgress = (ArcProgress) tv.findViewWithTag("gauge_" + i);
                arcProgress.setMax(i1);
                float min;
                //prefs.getInt("maxval_"+i,0);
                try {
                    min = prefs.getFloat("maxval_" + i, 0);
                } catch (Exception E) {
                    min = prefs.getInt("maxval_" + i, 0);
                }
                int warn1 = 0;
                int warn2 = 0;
                if (prefs.getString("warn1level_" + i, "0").isEmpty())
                    warn1 = 100;
                else
                    warn1 = parseInt(prefs.getString("warn1level_" + i, "0"));
                if (prefs.getString("warn2level_" + i, "0").isEmpty())
                    warn2 = 100;
                else
                    warn2 = parseInt(prefs.getString("warn2level_" + i, "0"));
                if (isdebugging) {

                    Log.d("OBD2", "Gauge : " + i + " Max Level: " + i1 + " Warning level 1: " + Math.round(warn1 * i1 / 100) + "Warning level 2: " + Math.round(warn2 * i1 / 100));
                    Log.d("OBD2", "Max Level: " + i1 + " Warning 1 stored value: " + prefs.getString("warn1level_" + i, "0") + " Warning 2 stored value: " + prefs.getString("warn2level_" + i, "0"));
                }
                arcProgress.setWarn1(Math.round(warn1 * (i1 - min) / 100));
                arcProgress.setWarn2(Math.round(warn2 * (i1 - min) / 100));
            }
        });
    }

    public void update_gauge_min(final int i, final int i1) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                TableLayout tv = (TableLayout) findViewById(R.id.tv_log);
                if (tv == null)
                    return;
                arcProgress = (ArcProgress) tv.findViewWithTag("gauge_" + i);
                arcProgress.setMin(i1);
                float max;
                //prefs.getInt("maxval_"+i,0);
                try {
                    max = prefs.getFloat("maxval_" + i, 0);
                } catch (Exception E) {
                    max = prefs.getInt("maxval_" + i, 0);
                }

                int warn1 = 0;
                int warn2 = 0;
                if (prefs.getString("warn1level_" + i, "0").isEmpty())
                    warn1 = 100;
                else
                    warn1 = parseInt(prefs.getString("warn1level_" + i, "0"));
                if (prefs.getString("warn2level_" + i, "0").isEmpty())
                    warn2 = 100;
                else
                    warn2 = parseInt(prefs.getString("warn2level_" + i, "0"));
                if (isdebugging) {
                    Log.d("OBD2", "Gauge : " + i + " Min Level: " + i1 + " Warning level 1: " + Math.round(warn1 * i1 / 100) + "Warning level 2: " + Math.round(warn2 * i1 / 100));
                    Log.d("OBD2", "Min Level: " + i1 + " Warning 1 stored value: " + prefs.getString("warn1level_" + i, "0") + " Warning 2 stored value: " + prefs.getString("warn2level_" + i, "0"));
                }
                arcProgress.setWarn1(Math.round(warn1 * (max - i1) / 100));
                arcProgress.setWarn2(Math.round(warn2 * (max - i1) / 100));
            }
        });
    }

    @Override
    public void onSensorChanged(int i, long l, float[] floats, byte[] bytes) {
        Log.d("OBD2AA", "Sensor changed: " + i + " timestamp: " + l + ", byte: " + bytes[0]);
    }

}
