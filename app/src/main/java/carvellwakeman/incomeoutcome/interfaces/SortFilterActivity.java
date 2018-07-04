package carvellwakeman.incomeoutcome.interfaces;

import carvellwakeman.incomeoutcome.helpers.Helper;

public interface SortFilterActivity {

    void AddFilterMethod(Helper.FILTER_METHODS method, String data);

    void Refresh();

    void CheckShowFiltersNotice();
}
