package com.pawnua.weathermap;

import android.app.Application;
import org.acra.*;
import org.acra.annotation.*;

/**
 * Created by nickpeshkov on 02.11.2014.
 */


@ReportsCrashes(formKey = "", // will not be used
        mailTo = "pawnua@gmail.com",
        mode = ReportingInteractionMode.TOAST,
        resToastText = R.string.crash_toast_text)
public class MainApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        ACRA.init(this);
    }
}
