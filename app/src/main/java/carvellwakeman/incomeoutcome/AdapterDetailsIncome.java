package carvellwakeman.incomeoutcome;

import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

public class AdapterDetailsIncome extends RecyclerView.Adapter<AdapterDetailsIncome.IncomeViewHolder>
{
    //MainActivity context
    ActivityDetailsIncome activity;
    //Click listener for items inside tab layout
    //private OnItemClickListener onItemClickListener;

    int _profileID;
    Profile _profile;

    //Constructor
    public AdapterDetailsIncome(ActivityDetailsIncome activity, int profileID)
    {
        _profileID = profileID;
        _profile = ProfileManager.GetProfileByID(profileID);

        this.activity = activity;
    }


    //When creating a view holder
    @Override
    public IncomeViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_layout_income, parent, false);

        return new IncomeViewHolder(itemView);
    }

    //Bind view holder to the recyclerView
    @Override
    public void onBindViewHolder(final IncomeViewHolder holder, int position)
    {
        if (_profile != null) {
            Income income = _profile.GetIncomeAtIndexInTimeFrame(position);
            Income parent = _profile.GetParentIncomeFromTimeFrameIncome(income);

            if (income != null) {
                //Time Period
                TimePeriod tp = income.GetTimePeriod();
                TimePeriod parent_tp = parent.GetTimePeriod();


                //Category
                if (!income.GetCategory().equals("")) {
                    holder.category.setText(income.GetCategory());
                }

                //Name of income source
                if (!income.GetSourceName().equals("")) {
                    holder.sourcename.setText(income.GetSourceName());
                }


                //Color Bar
                //if (ProfileManager.categoryColors.get(income.GetCategory()) != null) {
                    //holder.colorbar.setBackgroundColor(ProfileManager.categoryColors.get(income.GetCategory()));


                //holder.colorbar.setBackgroundColor(Color.parseColor(String.format("#%X", income.GetSourceName().hashCode())));
                holder.colorbar.setBackgroundColor(ProfileManager.ColorFromString(income.GetSourceName()));
                //}
                //else
                //{
                    //TODO Remove
                    //holder.colorbar.setBackgroundColor(Color.TRANSPARENT);
                //}


                //Descripiton
                if (!income.GetDescription().equals("")) {
                    holder.description.setText(income.GetDescription());
                }
                

                //Date
                holder.date.setText( tp.GetDateFormatted() );
                

                //Cost
                holder.cost.setText(income.GetValueFormatted());


                //Repeat text && Repeat Income Indenting
                if (parent_tp.DoesRepeat() && parent_tp.GetFirstOccurrence() != null && tp.GetDate() != null){
                    if (parent_tp.GetFirstOccurrence().compareTo(tp.GetDate()) == 0){
                        //Repeat Text
                        holder.repeat.setText(parent_tp.GetRepeatString(parent_tp.GetRepeatFrequency(), parent_tp.GetRepeatUntil()));

                        //Indent
                        holder.indent.setVisibility(View.GONE);
                        holder.moreInfoOn();
                    }
                    else {
                        holder.moreInfoOff();
                        holder.indent.setVisibility(View.VISIBLE);
                    }
                }


            }
        }
    }



    //How many items are there
    @Override
    public int getItemCount()
    {
        if (_profile != null) {
            return _profile.GetIncomeSourcesInTimeFrameSize();
        }
        return -1;
    }



    //View Holder class
    public class IncomeViewHolder extends RecyclerView.ViewHolder implements View.OnLongClickListener, View.OnClickListener
    {
        LinearLayout colorbar;
        LinearLayout indent;

        CardView cv;
        TextView category;
        TextView sourcename;
        TextView description;


        TextView date;
        TextView repeat;

        TextView cost;

        Boolean moreInfo;


        public IncomeViewHolder(View itemView)
        {
            super(itemView);

            moreInfo = false;

            colorbar = (LinearLayout) itemView.findViewById(R.id.income_row_colorbar);
            indent = (LinearLayout) itemView.findViewById(R.id.income_row_indent);

            cv = (CardView) itemView.findViewById(R.id.income_row_cardView);

            category = (TextView) itemView.findViewById(R.id.expense_row_category);
            sourcename = (TextView) itemView.findViewById(R.id.income_row_paidBy_who);
            description = (TextView) itemView.findViewById(R.id.income_row_description);

            date = (TextView) itemView.findViewById(R.id.income_row_date);
            repeat = (TextView) itemView.findViewById(R.id.income_row_repeat);

            cost = (TextView) itemView.findViewById(R.id.income_row_cost);

            //Short and long click listeners for the incomes context menu
            cv.setOnClickListener(this);
            cv.setOnLongClickListener(this);
        }

        @Override
        public boolean onLongClick(View v) {
            final Income in = _profile.GetIncomeAtIndexInTimeFrame(getAdapterPosition());
            final Income inp = _profile.GetParentIncomeFromTimeFrameIncome(in);
            if (in.GetTimePeriod().DoesRepeat() || inp.GetTimePeriod().DoesRepeat()) {
                String items[] = activity.getResources().getStringArray(R.array.RepeatingTransaction);

                new AlertDialog.Builder(activity).setItems(items, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int item) {
                        switch (item) {
                            case 0: //Edit (parent)
                                activity.editIncome(inp, _profileID);
                                break;
                            case 1: //Edit (instance)
                                activity.cloneIncome(inp, _profileID, in.GetTimePeriod().GetDate());
                                break;
                            case 2: //Delete (parent)
                                activity.deleteIncome(inp, true);
                                break;
                            case 3: //Delete (instance)
                                activity.deleteIncome(in, false);
                                break;
                            default:
                                dialog.cancel();
                                break;
                        }
                    }
                }).create().show();
            }
            else
            {
                String items[] = activity.getResources().getStringArray(R.array.SingleTransaction);

                new AlertDialog.Builder(activity).setItems(items, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int item) {
                        switch (item) {
                            case 0: //Edit (this)
                                activity.editIncome(in, _profileID);
                                break;
                            case 1: //Copy (this)
                                activity.copyIncome(in, _profileID);
                                break;
                            case 2: //Delete (this)
                                activity.deleteIncome(in, true);
                                break;
                            default:
                                dialog.cancel();
                                break;
                        }
                    }
                }).create().show();
            }


            return true;
        }

        @Override
        public void onClick(View v) {
            toggleMoreInfo();
        }


        //More Info
        public void toggleMoreInfo(){
            moreInfo = !moreInfo;
            if (moreInfo) { moreInfoOn(); } else { moreInfoOff(); }
        }
        public void moreInfoOn() { moreInfo = true; if (!repeat.getText().toString().equals("")) { repeat.setVisibility(View.VISIBLE); } }
        public void moreInfoOff() { moreInfo = false; repeat.setVisibility(View.GONE); }
    }
}
