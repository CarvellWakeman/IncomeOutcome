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

    //static MenuItem lastMenuItem = null;

    static Profile _profile;
    static MenuItem _item;

    static {}

    //DisplayFilterSort(ProfileManager.getString(R.string.sort) + ":" + ProfileManager.getString(R.string.category), upDown, "");
    public static void Run(Activity caller, Profile profile, MenuItem item, ProfileManager.CallBack callBack){
        _profile = profile;
        _item = item;

        switch (item.getItemId())
        {
            case R.id.toolbar_sort_category:
                if (_profile.GetSortMethod() == ProfileManager.SORT_METHODS.CATEGORY_UP) {
                    Sort(caller, ProfileManager.SORT_METHODS.CATEGORY_DOWN, callBack);
                } else {
                    Sort(caller, ProfileManager.SORT_METHODS.CATEGORY_UP, callBack);
                }
                if (callBack != null) { callBack.call(); }
                break;
            case R.id.toolbar_sort_source:
                if (_profile.GetSortMethod() == ProfileManager.SORT_METHODS.SOURCE_UP) {
                    Sort(caller, ProfileManager.SORT_METHODS.SOURCE_DOWN, callBack);
                } else {
                    Sort(caller, ProfileManager.SORT_METHODS.SOURCE_UP, callBack);
                }
                if (callBack != null) { callBack.call(); }
                break;
            case R.id.toolbar_sort_cost:
                if (_profile.GetSortMethod() == ProfileManager.SORT_METHODS.COST_UP) {
                    Sort(caller, ProfileManager.SORT_METHODS.COST_DOWN, callBack);
                } else {
                    Sort(caller, ProfileManager.SORT_METHODS.COST_UP, callBack);
                }
                if (callBack != null) { callBack.call(); }
                break;
            case R.id.toolbar_sort_date:
                if (_profile.GetSortMethod() == ProfileManager.SORT_METHODS.DATE_UP) {
                    Sort(caller, ProfileManager.SORT_METHODS.DATE_DOWN, callBack);
                } else {
                    Sort(caller, ProfileManager.SORT_METHODS.DATE_UP, callBack);
                }
                if (callBack != null) { callBack.call(); }
                break;
            case R.id.toolbar_sort_paidby:
                if (_profile.GetSortMethod() == ProfileManager.SORT_METHODS.PAIDBY_UP) {
                    Sort(caller, ProfileManager.SORT_METHODS.PAIDBY_DOWN, callBack);
                } else {
                    Sort(caller, ProfileManager.SORT_METHODS.PAIDBY_UP, callBack);
                }
                if (callBack != null) { callBack.call(); }
                break;


            case R.id.toolbar_filter_category:
                //DisplayFilter(ProfileManager.FILTER_METHODS.CATEGORY);
                Filter(caller, ProfileManager.FILTER_METHODS.CATEGORY, callBack);
                break;
            case R.id.toolbar_filter_source:
                //DisplayFilter(ProfileManager.FILTER_METHODS.SOURCE);
                Filter(caller, ProfileManager.FILTER_METHODS.SOURCE, callBack);
                break;
            case R.id.toolbar_filter_paidby:
                //DisplayFilter(ProfileManager.FILTER_METHODS.PAIDBY);
                Filter(caller, ProfileManager.FILTER_METHODS.PAIDBY, callBack);
                break;
            case R.id.toolbar_filter_splitwith:
                //DisplayFilter(ProfileManager.FILTER_METHODS.SPLITWITH);
                Filter(caller, ProfileManager.FILTER_METHODS.SPLITWITH, callBack);
                break;
        }


    }
    public static void Sort(Activity caller, ProfileManager.SORT_METHODS method, ProfileManager.CallBack callBack){
        _profile.SetSortMethod(method);

        DisplaySort(caller, method, callBack);
    }
    public static void Filter(Activity caller, ProfileManager.FILTER_METHODS method, ProfileManager.CallBack callBack){
        if (method == ProfileManager.FILTER_METHODS.NONE){
            _profile.SetFilterMethod(ProfileManager.FILTER_METHODS.NONE, null);
        } else {
            ProfileManager.OpenDialogFragment(caller, DialogFragmentFilter.newInstance(caller, _profile, method, ProfileManager.filterTitles.get(method), callBack), true);
        }
    }

    //public static void DisplayFilterSort(String sort, boolean sortUp, String filter){
    public static void DisplaySort(Activity caller, ProfileManager.SORT_METHODS sort, final ProfileManager.CallBack callBack) {
        if (caller != null) {
            final FrameLayout FL_SORT = (FrameLayout) caller.findViewById(R.id.frameLayout_sort);
            if (FL_SORT != null) {
                final TextView TV_SORT = (TextView) FL_SORT.findViewById(R.id.textView_sort);
                ImageView IV_SORT = (ImageView) FL_SORT.findViewById(R.id.imageView_sortUpDown);

                if (TV_SORT != null && IV_SORT != null) {
                    FL_SORT.setVisibility(View.VISIBLE);

                    FL_SORT.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            TV_SORT.setText("");
                            FL_SORT.setVisibility(View.GONE);
                            _profile.SetSortMethod(ProfileManager.SORT_METHODS.DATE_DOWN);
                            callBack.call();
                        }
                    });

                    if (sort != null && sort != ProfileManager.SORT_METHODS.DATE_DOWN) {
                        FL_SORT.setVisibility(View.VISIBLE);
                        TV_SORT.setText(ProfileManager.sortSubtitles.get(sort));

                        if (sort.toString().contains("_DOWN")) {
                            IV_SORT.setImageResource(R.drawable.ic_keyboard_arrow_down_white_24dp);
                        }
                        else if (sort.toString().contains("_UP")) {
                            IV_SORT.setImageResource(R.drawable.ic_keyboard_arrow_up_white_24dp);
                        }
                    }
                    else {
                        FL_SORT.setVisibility(View.GONE);
                    }
                }
            }
        }
    }
    public static void DisplayFilter(Activity caller, ProfileManager.FILTER_METHODS filter, Object data, final ProfileManager.CallBack callBack) {
        if (caller != null) {
            final FrameLayout FL_FILTER = (FrameLayout) caller.findViewById(R.id.frameLayout_filter);
            if (FL_FILTER != null) {
                final TextView TV_FILTER = (TextView) FL_FILTER.findViewById(R.id.textView_filter);
                if (TV_FILTER != null) {
                    FL_FILTER.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            TV_FILTER.setText("");
                            FL_FILTER.setVisibility(View.GONE);
                            _profile.SetFilterMethod(ProfileManager.FILTER_METHODS.NONE, null);
                            callBack.call();
                        }
                    });

                    if (filter != null && filter != ProfileManager.FILTER_METHODS.NONE) {
                        FL_FILTER.setVisibility(View.VISIBLE);
                        String info = "";
                        switch (filter) {
                            case PAIDBY:
                                info = ProfileManager.getString(R.string.info_paidby) + " " + String.valueOf(data);
                                break;
                            case SPLITWITH:
                                info = ProfileManager.getString(R.string.splitwith) + " " + String.valueOf(data);
                                break;
                            default:
                                info = String.valueOf(data);
                        }
                        TV_FILTER.setText(ProfileManager.filterSubtitles.get(filter) + info);
                    }
                    else {
                        FL_FILTER.setVisibility(View.GONE);
                    }
                }
            }
        }
    }

}
