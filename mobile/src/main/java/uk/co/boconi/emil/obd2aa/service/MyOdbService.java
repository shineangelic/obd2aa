package uk.co.boconi.emil.obd2aa.service;

import com.google.android.apps.auto.sdk.CarActivity;
import com.google.android.apps.auto.sdk.CarActivityService;

import uk.co.boconi.emil.obd2aa.OBD2AA;


public class MyOdbService extends CarActivityService {

    @Override
    public Class<? extends CarActivity> getCarActivity() {
        return OBD2AA.class;
    }

}

