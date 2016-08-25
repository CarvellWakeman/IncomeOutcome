package carvellwakeman.incomeoutcome;


import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

public class CardExpenses extends Card
{
    int _profileID;

    public CardExpenses(int profileID, Context context, LayoutInflater inflater, int layout, View.OnClickListener clickListener){
        super(inflater, layout, clickListener);
        _profileID = profileID;

        //Set sources recyclerview
        RecyclerView sources = (RecyclerView) v.findViewById(R.id.recyclerView_overview_expenses_totals);

        NpaLinearLayoutManager linearLayoutManager = new NpaLinearLayoutManager(context);
        linearLayoutManager.setOrientation(NpaLinearLayoutManager.VERTICAL);
        linearLayoutManager.scrollToPosition(0);
        sources.setLayoutManager(linearLayoutManager);

        Profile _profile = ProfileManager.GetProfileByID(_profileID);
        if (_profile != null){
            _profile.CalculateTimeFrame(0);

            AdapterBasic totalsAdapter = new AdapterBasic(context, _profileID, _profile.CalculateTotalsInTimeFrame(0, 1));
            sources.setAdapter(totalsAdapter);
        }

    }
}
