package carvellwakeman.incomeoutcome;

public interface SortFilterActivity {

    void AddFilterMethod(Helper.FILTER_METHODS method, String data);

    void Refresh();

    void CheckShowFiltersNotice();
}
