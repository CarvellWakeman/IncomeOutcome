package carvellwakeman.incomeoutcome;


import android.app.Application;
import android.content.res.Resources;

import java.util.Locale;

public class App extends Application {

    private static Resources resources;

    public void onCreate() {
        super.onCreate();
        resources = getResources();
    }

    public static Resources GetResources(){ return resources; }
    public static Locale GetLocale() { return GetResources().getConfiguration().locale; }
}