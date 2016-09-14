package carvellwakeman.incomeoutcome;


import android.app.Application;
import android.content.Context;
import android.content.res.Resources;

import java.util.Locale;

public class App extends Application {

    private static Resources resources;
    private static Context context;

    public void onCreate() {
        super.onCreate();
        resources = getResources();
        if (ProfileManager.isDebugMode(getApplicationContext())) { context = getApplicationContext(); }
    }

    public static Context GetContext() { return context; }
    public static Resources GetResources(){ return resources; }
    public static Locale GetLocale() { return GetResources().getConfiguration().locale; }
}