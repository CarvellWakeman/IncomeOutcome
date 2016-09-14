package carvellwakeman.incomeoutcome;


import android.app.Activity;
import android.graphics.PorterDuff;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.SortedMap;

public class SortFilterOptions {

    static MenuItem lastMenuItem = null;

    static Activity _caller;
    static Profile _profile;
    static MenuItem _item;
    static int _activityType;
    static ProfileManager.CallBack _callBack;

    static {}

    public static void Run(Activity caller, Profile profile, MenuItem item, int activityType, ProfileManager.CallBack callBack){
        _caller = caller;
        _profile = profile;
        _item = item;
        _activityType = activityType;
        _callBack = callBack;

        boolean upDown = false;

        switch (item.getItemId())
        {
            case R.id.toolbar_sort_category:
                if (_profile.GetSortMethod() == ProfileManager.SORT_METHODS.CATEGORY_UP) {
                    //item.setIcon(R.drawable.ic_keyboard_arrow_down_white_24dp);
                    Sort(ProfileManager.SORT_METHODS.CATEGORY_DOWN);
                    upDown = true;
                } else {
                    upDown = false;
                    //item.setIcon(R.drawable.ic_keyboard_arrow_up_white_24dp);
                    Sort(ProfileManager.SORT_METHODS.CATEGORY_UP);
                }
                if (lastMenuItem!=null && lastMenuItem.getIcon()!=null && item != lastMenuItem) { lastMenuItem.setIcon(null); }
                lastMenuItem = item;
                //if (item.getIcon() != null){ item.getIcon().setColorFilter(ProfileManager.getColor(R.color.black), PorterDuff.Mode.MULTIPLY); }
                _profile.CalculateTimeFrame(activityType);
                if (_callBack!=null) {Call(ProfileManager.getString(R.string.sort) + ":" + ProfileManager.getString(R.string.category), upDown, "");}
                break;
            case R.id.toolbar_sort_source:
                if (_profile.GetSortMethod() == ProfileManager.SORT_METHODS.SOURCE_UP) {
                    upDown = true;
                    //item.setIcon(R.drawable.ic_keyboard_arrow_down_white_24dp);
                    Sort(ProfileManager.SORT_METHODS.SOURCE_DOWN);
                } else {
                    upDown = false;
                    //item.setIcon(R.drawable.ic_keyboard_arrow_up_white_24dp);
                    Sort(ProfileManager.SORT_METHODS.SOURCE_UP);
                }
                if (lastMenuItem!=null && lastMenuItem.getIcon()!=null && item != lastMenuItem) { lastMenuItem.setIcon(null); }
                lastMenuItem = item;
                //if (item.getIcon() != null){ item.getIcon().setColorFilter(ProfileManager.getColor(R.color.black), PorterDuff.Mode.MULTIPLY); }
                _profile.CalculateTimeFrame(activityType);
                if (_callBack!=null) {Call(ProfileManager.getString(R.string.sort) + ":" + ProfileManager.getString(R.string.source), upDown, "");}
                break;
            case R.id.toolbar_sort_cost:
                if (_profile.GetSortMethod() == ProfileManager.SORT_METHODS.COST_UP) {
                    upDown = true;
                    //item.setIcon(R.drawable.ic_keyboard_arrow_down_white_24dp);
                    Sort(ProfileManager.SORT_METHODS.COST_DOWN);
                } else {
                    upDown = false;
                    //item.setIcon(R.drawable.ic_keyboard_arrow_up_white_24dp);
                    Sort(ProfileManager.SORT_METHODS.COST_UP);
                }
                if (lastMenuItem!=null && lastMenuItem.getIcon()!=null && item != lastMenuItem) { lastMenuItem.setIcon(null); }
                lastMenuItem = item;
                //if (item.getIcon() != null){ item.getIcon().setColorFilter(ProfileManager.getColor(R.color.black), PorterDuff.Mode.MULTIPLY); }
                _profile.CalculateTimeFrame(activityType);
                if (_callBack!=null) {Call(ProfileManager.getString(R.string.sort) + ":" + ProfileManager.getString(R.string.cost), upDown, "");}
                break;
            case R.id.toolbar_sort_date:
                if (_profile.GetSortMethod() == ProfileManager.SORT_METHODS.DATE_UP) {
                    //item.setIcon(R.drawable.ic_keyboard_arrow_down_white_24dp);
                    upDown=true;
                    Sort(ProfileManager.SORT_METHODS.DATE_DOWN);
                } else {
                    upDown=false;
                    //item.setIcon(R.drawable.ic_keyboard_arrow_up_white_24dp);
                    Sort(ProfileManager.SORT_METHODS.DATE_UP);
                }
                if (_callBack!=null) {Call(ProfileManager.getString(R.string.sort) + ":" + ProfileManager.getString(R.string.date), upDown, "");}
                if (lastMenuItem!=null && lastMenuItem.getIcon()!=null && item != lastMenuItem) { lastMenuItem.setIcon(null); }
                lastMenuItem = item;
                //if (item.getIcon() != null){ item.getIcon().setColorFilter(ProfileManager.getColor(R.color.black), PorterDuff.Mode.MULTIPLY); }
                _profile.CalculateTimeFrame(activityType);
                break;
            case R.id.toolbar_sort_paidby:
                if (_profile.GetSortMethod() == ProfileManager.SORT_METHODS.PAIDBY_UP) {
                    upDown = true;
                    //item.setIcon(R.drawable.ic_keyboard_arrow_down_white_24dp);
                    Sort(ProfileManager.SORT_METHODS.PAIDBY_DOWN);
                } else {
                    upDown = false;
                    //item.setIcon(R.drawable.ic_keyboard_arrow_up_white_24dp);
                    Sort(ProfileManager.SORT_METHODS.PAIDBY_UP);
                }
                if (lastMenuItem!=null && lastMenuItem.getIcon()!=null && item != lastMenuItem) { lastMenuItem.setIcon(null); }
                lastMenuItem = item;
                //if (item.getIcon() != null){ item.getIcon().setColorFilter(ProfileManager.getColor(R.color.black), PorterDuff.Mode.MULTIPLY); }
                _profile.CalculateTimeFrame(activityType);
                if (_callBack!=null) {Call(ProfileManager.getString(R.string.sort) + ":" + ProfileManager.getString(R.string.whopaid), upDown, "");}
                break;


            case R.id.toolbar_filter_category:
                Filter(ProfileManager.FILTER_METHODS.CATEGORY);
                break;
            case R.id.toolbar_filter_source:
                Filter(ProfileManager.FILTER_METHODS.SOURCE);
                break;
            case R.id.toolbar_filter_paidby:
                Filter(ProfileManager.FILTER_METHODS.PAIDBY);
                break;
            case R.id.toolbar_filter_splitwith:
                Filter(ProfileManager.FILTER_METHODS.SPLITWITH);
                break;
        }


    }
    public static void Sort(ProfileManager.SORT_METHODS method){
        _profile.SetSortMethod(method);
    }
    public static void Filter(ProfileManager.FILTER_METHODS method){
        if (method == ProfileManager.FILTER_METHODS.NONE){
            _profile.SetFilterMethod(ProfileManager.FILTER_METHODS.NONE, null);
            if (_callBack!=null) {Call("", false, null);}
        } else {
            ProfileManager.OpenDialogFragment(_caller, DialogFragmentFilter.newInstance(_callBack, _profile, method), true);
        }
    }

    public static void Call(String sort, boolean sortUp, String filter){
        final FrameLayout FL_SORT = (FrameLayout) _caller.findViewById(R.id.frameLayout_sort);;
        final FrameLayout FL_FILTER = (FrameLayout) _caller.findViewById(R.id.frameLayout_filter);
        TextView TV_SORT = null;
        TextView TV_FILTER = null;
        ImageView IV_SORT = null;


        if (FL_SORT!=null){
            FL_SORT.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    FL_SORT.setVisibility(View.GONE);
                    Sort(ProfileManager.SORT_METHODS.DATE_DOWN);
                    _callBack.call();
                }
            });
            TV_SORT = (TextView) FL_SORT.findViewById(R.id.textView_sort);
            if (TV_SORT!=null){
                if (sort != null) {
                    if (!sort.equals("")) {
                        FL_SORT.setVisibility(View.VISIBLE);
                        TV_SORT.setText(sort);
                    }
                }
                else {
                    FL_SORT.setVisibility(View.GONE);
                }
            }
            IV_SORT = (ImageView) FL_SORT.findViewById(R.id.imageView_sortUpDown);
            if (IV_SORT!=null){
                if (sortUp) { IV_SORT.setImageResource(R.drawable.ic_keyboard_arrow_down_white_24dp); }
                else { IV_SORT.setImageResource(R.drawable.ic_keyboard_arrow_up_white_24dp); }
            }
        }
        if (FL_FILTER!=null){
            FL_FILTER.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    FL_FILTER.setVisibility(View.GONE);
                    Filter(ProfileManager.FILTER_METHODS.NONE);
                    _callBack.call();
                }
            });
            TV_FILTER = (TextView) FL_FILTER.findViewById(R.id.textView_filter);
            if (TV_FILTER!=null){
                if (filter!=null) {
                    if (!filter.equals("")) {
                        FL_FILTER.setVisibility(View.VISIBLE);
                        TV_FILTER.setText(filter);
                    }
                }
                else {
                    FL_FILTER.setVisibility(View.GONE);
                }
            }
        }

        _callBack.call();

    }

}
