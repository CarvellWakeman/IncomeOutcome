package carvellwakeman.incomeoutcome;


import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;

public class CardIncome extends Card
{
    int _profileID;

    public CardIncome(int profileID, Context context, LayoutInflater inflater, int layout, View.OnClickListener clickListener){
        super(inflater, layout, clickListener);
        _profileID = profileID;

        //Set sources recyclerview
        RecyclerView sources = (RecyclerView) v.findViewById(R.id.recyclerView_overview_income_totals);

        NpaLinearLayoutManager linearLayoutManager = new NpaLinearLayoutManager(context);
        linearLayoutManager.setOrientation(NpaLinearLayoutManager.VERTICAL);
        linearLayoutManager.scrollToPosition(0);
        sources.setLayoutManager(linearLayoutManager);

        Profile _profile = ProfileManager.GetProfileByID(_profileID);
        if (_profile != null){
            _profile.CalculateTimeFrame(1);

            AdapterBasic totalsAdapter = new AdapterBasic(context, _profileID, _profile.CalculateTotalsInTimeFrame(1, 1));
            sources.setAdapter(totalsAdapter);
        }

    }
}
