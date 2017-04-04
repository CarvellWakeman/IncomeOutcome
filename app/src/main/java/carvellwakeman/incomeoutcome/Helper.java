package carvellwakeman.incomeoutcome;

import android.app.Activity;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.v4.content.PermissionChecker;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.HashMap;

public class Helper
{
    //TODO: Rework sorting and filtering to be objects that are passed around in activities
    //Sort and filter methods
    enum SORT_METHODS
    {
        DATE_UP,
        COST_UP,
        PAIDBY_UP,
        CATEGORY_UP,
        SOURCE_UP,

        DATE_DOWN,
        COST_DOWN,
        PAIDBY_DOWN,
        CATEGORY_DOWN,
        SOURCE_DOWN
    }
    enum FILTER_METHODS
    {
        NONE,
        DATE,
        COST,
        PAIDBY,
        SPLITWITH,
        CATEGORY,
        SOURCE
    }


    //Sort and Filter methods string titles
    private static HashMap<SORT_METHODS, String> sortSubtitles;
    static HashMap<FILTER_METHODS, Integer> filterTitles;
    static HashMap<FILTER_METHODS, String> filterSubtitles;
    static {
        sortSubtitles = new HashMap<>();
        filterTitles = new HashMap<>();
        filterSubtitles = new HashMap<>();

        sortSubtitles.put(SORT_METHODS.DATE_UP, Helper.getString(R.string.sort) + ":" + Helper.getString(R.string.date));
        sortSubtitles.put(SORT_METHODS.DATE_DOWN, Helper.getString(R.string.sort) + ":" + Helper.getString(R.string.date));
        sortSubtitles.put(SORT_METHODS.COST_UP, Helper.getString(R.string.sort) + ":" + Helper.getString(R.string.cost));
        sortSubtitles.put(SORT_METHODS.COST_DOWN, Helper.getString(R.string.sort) + ":" + Helper.getString(R.string.cost));
        sortSubtitles.put(SORT_METHODS.PAIDBY_UP, Helper.getString(R.string.sort) + ":" + Helper.getString(R.string.paidby));
        sortSubtitles.put(SORT_METHODS.PAIDBY_DOWN, Helper.getString(R.string.sort) + ":" + Helper.getString(R.string.paidby));
        sortSubtitles.put(SORT_METHODS.CATEGORY_UP, Helper.getString(R.string.sort) + ":" + Helper.getString(R.string.category));
        sortSubtitles.put(SORT_METHODS.CATEGORY_DOWN, Helper.getString(R.string.sort) + ":" + Helper.getString(R.string.category));
        sortSubtitles.put(SORT_METHODS.SOURCE_UP, Helper.getString(R.string.sort) + ":" + Helper.getString(R.string.source));
        sortSubtitles.put(SORT_METHODS.SOURCE_DOWN, Helper.getString(R.string.sort) + ":" + Helper.getString(R.string.source));

        filterSubtitles.put(FILTER_METHODS.NONE, Helper.getString(R.string.filter) + ":");
        filterSubtitles.put(FILTER_METHODS.DATE, Helper.getString(R.string.filter) + ":");
        filterSubtitles.put(FILTER_METHODS.CATEGORY, Helper.getString(R.string.filter) + ":");
        filterSubtitles.put(FILTER_METHODS.PAIDBY, Helper.getString(R.string.filter) + ":");
        filterSubtitles.put(FILTER_METHODS.SPLITWITH, Helper.getString(R.string.filter) + ":");
        filterSubtitles.put(FILTER_METHODS.SOURCE, Helper.getString(R.string.filter) + ":");

        filterTitles.put(FILTER_METHODS.NONE, R.string.filter_none);
        filterTitles.put(FILTER_METHODS.DATE, R.string.filter_date);
        filterTitles.put(FILTER_METHODS.COST, R.string.filter_cost);
        filterTitles.put(FILTER_METHODS.CATEGORY, R.string.filter_category);
        filterTitles.put(FILTER_METHODS.PAIDBY, R.string.filter_paidby);
        filterTitles.put(FILTER_METHODS.SPLITWITH, R.string.filter_splitwith);
        filterTitles.put(FILTER_METHODS.SOURCE, R.string.filter_source);
    }

    //Formatters
    static DecimalFormat decimalFormat = new DecimalFormat("#.###");
    static NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(); //new DecimalFormat("¤#.###");


    //Universal print
    public static void Print(Context c, String msg){ if (c!=null && isDebugMode(c)) { Toast.makeText(c, msg, Toast.LENGTH_SHORT).show(); } }
    public static void PrintLong(Context c, String msg){ if (c!=null && isDebugMode(c)) { Toast.makeText(c, msg, Toast.LENGTH_LONG).show(); } }
    public static void PrintUser(Context c, String msg){ if (c!=null){ Toast.makeText(c, msg, Toast.LENGTH_SHORT).show(); } }
    public static void PrintUserLong(Context c, String msg){ if (c!=null){ Toast.makeText(c, msg, Toast.LENGTH_LONG).show(); } }

    public static void Log(Context c, String cat, String msg){ if (c!=null && isDebugMode(c)) { Log.e(cat, msg); } }


    //Dialog fragment manager
    public static void OpenDialogFragment(Activity caller, DialogFragment fragment, boolean openAsDialogFragment){
        if (fragment != null) {
            if (openAsDialogFragment) { // The device is using a large layout, so show the fragment as a dialog
                FragmentTransaction ft = caller.getFragmentManager().beginTransaction();
                Fragment prev = caller.getFragmentManager().findFragmentByTag("dialog");
                if (prev != null) {
                    ft.remove(prev);
                }
                ft.addToBackStack(null);

                fragment.show(caller.getFragmentManager(), "dialog");
                //FragmentTransaction ft = fragmentManager.beginTransaction();
                //ft.add(fragment, null);
                //ft.commitAllowingStateLoss();
            }
            else { // The device is smaller, so show the fragment fullscreen
                FragmentTransaction transaction = caller.getFragmentManager().beginTransaction();
                transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                transaction.add(android.R.id.content, fragment).addToBackStack(null).commit();
            }
        }
    }


    //Getting string resources from static contexts
    public static String getString(int resourceID){ return App.GetResources().getString(resourceID); }
    public static int getColor(int resourceID){ return App.GetResources().getColor(resourceID); }
    public static Drawable getDrawable(int resourceID) { return App.GetResources().getDrawable(resourceID); }


    //Hide Soft Keyboard
    public static void hideSoftKeyboard(Activity act, View v)
    {
        if (v != null) {
            InputMethodManager imm = (InputMethodManager) act.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
        }
    }
    //Show Soft Keyboard
    public static void showSoftKeyboard(Activity act, View v)
    {
        if (v != null) {
            InputMethodManager imm = (InputMethodManager) act.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(v, InputMethodManager.SHOW_IMPLICIT);
        }
    }


    //Color parsing
    public static int ColorFromString(String str){
        //Hash the string
        str = String.format("%X", str.hashCode());
        //Minimum string length = 6
        while (str.length() < 6) { str = '0' + str; }

        //Convert Hex string into int
        int coli = (int)Long.parseLong(str, 16);
        int r = (coli >> 16) & 0xFF;
        int g = (coli >> 8) & 0xFF;
        int b = (coli) & 0xFF;

        return Color.argb(255,r,g,b);
    }


    //Permissions
    public static boolean isStoragePermissionGranted() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (PermissionChecker.checkSelfPermission(App.GetContext(), android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                //Log.v("PERMISSIONS","External Write Storage permission is granted");
                return true;
            } else {
                //Log.v("PERMISSIONS", "External Write Storage permission is not granted");
                return false;
            }
        }
        else { //permission is automatically granted on sdk<23 upon installation
            //Log.v("PERMISSIONS","Permission is granted by default");
            return true;
        }
    }

    //DEBUG
    public static boolean isDebugMode(Context ac){
        return (0 != (ac.getApplicationInfo().flags & ApplicationInfo.FLAG_DEBUGGABLE));
    }

}
