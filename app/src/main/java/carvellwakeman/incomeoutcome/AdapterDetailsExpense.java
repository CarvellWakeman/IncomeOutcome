package carvellwakeman.incomeoutcome;

import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

public class AdapterDetailsExpense extends RecyclerView.Adapter<AdapterDetailsExpense.ExpenseViewHolder>
{
    //Calling activity context
    ActivityDetailsExpense activity;

    //ID strings
    int _profileID;
    Profile _profile;

    //Constructor
    public AdapterDetailsExpense(ActivityDetailsExpense activity, int profileID)
    {
        _profileID = profileID;
        _profile = ProfileManager.GetProfileByID(profileID);

        this.activity = activity;
    }

    //When creating a view holder
    @Override
    public ExpenseViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_layout_expense, parent, false);

        return new ExpenseViewHolder(itemView);
    }

    //Bind view holder to the recyclerView
    @Override
    public void onBindViewHolder(final ExpenseViewHolder holder, int position)
    {
        if (_profile != null) {
            Expense expense = _profile.GetExpenseAtIndexInTimeFrame(position);
            //Expense parent = _profile.GetParentExpenseFromTimeFrameExpense(expense);


            if (expense != null) {
                //Parent expense
                Expense parent = (expense.GetParentID()==0 ? expense : _profile.GetExpense(expense.GetParentID()));

                //Time Period
                TimePeriod tp = expense.GetTimePeriod();

                //Category
                if (expense.GetCategory().equals("")) {
                    holder.category.setText(R.string.info_nocategory);
                }
                else {
                    holder.category.setText(expense.GetCategory());
                }

                //Company
                if (expense.GetSourceName().equals("")) {
                    //holder.categoryCompanySplitter.setVisibility(View.INVISIBLE);

                    //holder.company.setTypeface(null, Typeface.ITALIC);
                    holder.company.setText(R.string.info_nocompany);
                } else {
                    //holder.categoryCompanySplitter.setVisibility(View.VISIBLE);

                    holder.company.setText(expense.GetSourceName());
                    //holder.company.setSelected(true);
                }

                //Color Bar
                Category cat = ProfileManager.GetCategory(expense.GetCategory());
                if (cat != null && cat.GetColor() != 0) {
                    holder.colorbar.setBackgroundColor(cat.GetColor());
                }
                else
                {
                    holder.colorbar.setBackgroundColor(Color.TRANSPARENT);
                }


                //Descripiton
                if (expense.GetDescription().equals("")) {
                    //holder.description.setTypeface(null, Typeface.ITALIC);
                    holder.description.setText(R.string.info_nodescription);
                }
                else {
                    //holder.description.setTypeface(null, Typeface.NORMAL);
                    holder.description.setText(expense.GetDescription());
                }

                //Split
                if (expense.GetSplitWith() == null) {
                    holder.split.setVisibility(View.GONE);
                }
                else {
                    holder.split.setVisibility(View.VISIBLE);
                    //Who owes who
                    if (expense.GetIPaid()){
                        holder.split.setText(activity.getString(R.string.format_ipaid, expense.GetSplitWith(), expense.GetSplitValueFormatted(), ProfileManager.decimalFormat.format(Math.round(expense.GetOtherSplitPercentage() * 100.00f))));
                        //holder.split.setText(expense.GetSplitWith().GetName() + R.string.format_owes + expense.GetSplitValueFormatted() + R.string.format_leftparen + ProfileManager.decimalFormat.format(Math.round(expense.GetOtherSplit() * 100.00f)) + R.string.format_rightparen_percent);
                    }
                    else{
                        //holder.split.setText(R.string.format_iowe + expense.GetSplitValueFormatted() + R.string.format_leftparen + ProfileManager.decimalFormat.format(Math.round(expense.GetOtherSplit() * 100.00f)) + R.string.format_rightparen_percent);
                        holder.split.setText(activity.getString(R.string.format_theypaid, expense.GetMySplitValueFormatted(), ProfileManager.decimalFormat.format(Math.round(expense.GetOtherSplitPercentage() * 100.00f))));
                    }

                    //Paid back
                    if (expense.IsPaidBack()){
                        holder.split.setPaintFlags(holder.split.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

                        holder.paidBack.setVisibility(View.VISIBLE);
                        holder.paidBack.setText(expense.GetPaidBackFormatted());
                    }
                    else{
                        holder.split.setPaintFlags(0);

                        holder.paidBack.setVisibility(View.GONE);
                    }

                    //holder.split.setText("Split " + ProfileManager.decimalFormat.format(Math.round(expense.GetMySplit() * 100.00f)) + "% / " + ProfileManager.decimalFormat.format(Math.round(expense.GetOtherSplit() * 100.00f)) + "%");
                }

                //Date
                holder.date.setText( tp.GetDateFormatted() );


                //PaidBy
                if (expense.GetSplitWith() != null) {
                    holder.paidBy.setText((expense.GetIPaid() ? activity.getString(R.string.format_me) : expense.GetSplitWith()));
                } else {
                    holder.paidBy.setText(R.string.format_me);
                }

                //Cost
                holder.cost.setText(expense.GetValueFormatted());


                if (parent != null) {
                    //Time Period
                    TimePeriod parent_tp = parent.GetTimePeriod();


                    //Repeat text && Repeat Expense Indenting
                    if (parent_tp.DoesRepeat() && parent_tp.GetFirstOccurrence() != null && tp.GetDate() != null) {
                        if (parent_tp.GetFirstOccurrence().compareTo(tp.GetDate()) == 0 || parent.GetID() == expense.GetID()) {
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
                    else {
                        holder.moreInfoOff();
                        holder.repeat.setText("");
                        holder.repeat.setVisibility(View.GONE);
                        holder.indent.setVisibility(View.GONE);
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
            return _profile.GetExpenseSourcesInTimeFrameSize();
        }
        return -1;
    }



    //View Holder class
    public class ExpenseViewHolder extends RecyclerView.ViewHolder implements View.OnLongClickListener, View.OnClickListener
    {
        LinearLayout colorbar;
        LinearLayout indent;

        CardView cv;
        TextView category;
        TextView company;
        TextView description;

        TextView split;
        TextView paidBack;

        TextView date;
        TextView repeat;

        TextView paidBy;
        TextView cost;

        Boolean moreInfo;


        public ExpenseViewHolder(View itemView)
        {
            super(itemView);

            moreInfo = false;

            colorbar = (LinearLayout) itemView.findViewById(R.id.expense_row_colorbar);
            indent = (LinearLayout) itemView.findViewById(R.id.expense_row_indent);

            cv = (CardView) itemView.findViewById(R.id.expense_row_cardView);

            category = (TextView) itemView.findViewById(R.id.expense_row_category);
            company = (TextView) itemView.findViewById(R.id.expense_row_company);
            description = (TextView) itemView.findViewById(R.id.expense_row_description);

            date = (TextView) itemView.findViewById(R.id.expense_row_date);
            repeat = (TextView) itemView.findViewById(R.id.expense_row_repeat);

            split = (TextView) itemView.findViewById(R.id.expense_row_split);
            paidBack = (TextView) itemView.findViewById(R.id.expense_row_paidback);
            paidBy = (TextView) itemView.findViewById(R.id.expense_row_paidby_who);
            cost = (TextView) itemView.findViewById(R.id.expense_row_cost);

            //Short and long click listeners for the expenses context menu
            cv.setOnClickListener(this);
            cv.setOnLongClickListener(this);
        }

        @Override
        public boolean onLongClick(View v) {
            final Expense ex = _profile.GetExpenseAtIndexInTimeFrame(getAdapterPosition());
            final Expense exp = _profile.GetParentExpenseFromTimeFrameExpense(ex);
            if (ex.GetTimePeriod().DoesRepeat() || exp.GetTimePeriod().DoesRepeat()) {
                String items[] = activity.getResources().getStringArray(R.array.RepeatingTransaction);
                new AlertDialog.Builder(activity).setItems(items, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int item) {
                        switch (item) {
                            case 0: //Edit (parent)
                                activity.editExpense(exp, _profileID);
                                break;
                            case 1: //Edit (instance)
                                //If the expense is not a ghost expense (only exists in the _timeframe array), then edit it normally, else clone it and blacklist the old date
                                if (_profile.GetExpense(ex.GetID()) != null) { //Child
                                    activity.editExpense(ex, _profileID);
                                }
                                else { //Ghost
                                    activity.cloneExpense(exp, _profileID, ex.GetTimePeriod().GetDate());
                                }

                                break;
                            case 2: //Delete (parent)
                                activity.deleteExpense(exp, true, true);
                                break;
                            case 3: //Delete (instance)
                                if (_profile.GetExpense(ex.GetID()) != null) { //Child
                                    activity.deleteExpense(ex, true, false);
                                }
                                else { //Ghost
                                    activity.deleteExpense(ex, false, false);
                                }
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
                                activity.editExpense(ex, _profileID);
                                break;
                            case 1: //Copy (this)
                                activity.copyExpense(ex, _profileID);
                                break;
                            case 2: //Delete (this)
                                activity.deleteExpense(ex, true, true);
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
            final Expense ex = _profile.GetExpenseAtIndexInTimeFrame(getAdapterPosition());

            //ProfileManager.Print("ID:" + ex.GetID());
            //ProfileManager.Print("ParentID:" + ex.GetParentID());

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
