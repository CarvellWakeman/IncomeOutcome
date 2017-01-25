package carvellwakeman.incomeoutcome;


import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.res.Resources;

import java.util.Locale;

public class App extends Application {

    //Data
    private static SharedPreferences prefs;

    //Resources
    private static Resources resources;
    private static Context context;

    //Version
    private static String lastVersion = "";
    private static String versionKey = "lastVersion";

    public void onCreate() {
        super.onCreate();
        resources = getResources();
        if (ProfileManager.isDebugMode(getApplicationContext())) { context = getApplicationContext(); }

        //Load lastVersion
        prefs = this.getSharedPreferences(getPackageName(), Context.MODE_PRIVATE);
        lastVersion = prefs.getString(versionKey, "");
    }

    //Resources
    public static Context GetContext() { return context; }
    public static Resources GetResources(){ return resources; }
    public static Locale GetLocale() { return GetResources().getConfiguration().locale; }

    //Version
    public static String GetVersion(Context context) {
        try {
            PackageInfo pInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            return pInfo.versionName;
        } catch (Exception ex){ return "Unknown"; }
    }
    public static int GetVersionCode(Context context){
        try {
            PackageInfo pInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            return pInfo.versionCode;
        } catch (Exception ex){ return -1; }
    }
    public static String GetLastVersion(){ return lastVersion; }
    public static void SetLastVersion(Context ac){ prefs.edit().putString(versionKey, GetVersion(ac)).apply(); }
}