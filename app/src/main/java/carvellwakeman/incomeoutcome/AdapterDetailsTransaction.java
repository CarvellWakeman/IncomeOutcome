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

public class AdapterDetailsTransaction extends RecyclerView.Adapter<AdapterDetailsTransaction.TransactionViewHolder>
{
    //Calling activity context
    ActivityDetailsTransaction activity;

    //ID strings
    int _profileID;
    Profile _profile;

    //Expensne vs income adapter
    int activityType = -1;

    //Constructor
    public AdapterDetailsTransaction(ActivityDetailsTransaction activity, int profileID, int activityType)
    {
        _profileID = profileID;
        _profile = ProfileManager.GetProfileByID(profileID);

        this.activity = activity;

        this.activityType = activityType;

        if (activityType == 0) { //Expense

        }
        else if (activityType == 1) { //Income

        }
    }

    //Custom transaction getters
    public Transaction GetTransaction(int position){
        if (_profile != null){
            return _profile.GetTransactionAtIndexInTimeFrame(position);
        }
        return null;
    }
    public Transaction GetTransactionByID(int id){
        if (_profile != null){
            return _profile.GetTransaction(id);
        }
        return null;
    }
    public Transaction GetTransactionParent(Transaction tran){
        if (_profile != null){
            return _profile.GetParentTransactionFromTimeFrameTransaction(tran);
        }
        return null;
    }

    //When creating a view holder
    @Override
    public TransactionViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_layout_transaction, parent, false);

        return new TransactionViewHolder(itemView);
    }

    //Bind view holder to the recyclerView
    @Override
    public void onBindViewHolder(final TransactionViewHolder holder, int position)
    {
        if (_profile != null) {
            Transaction transaction = GetTransaction(position);

            if (transaction != null) {
                //Parent
                Transaction parent = (transaction.GetParentID()==0 ? transaction : GetTransactionByID(transaction.GetParentID()) );

                //Time Period
                TimePeriod tp = transaction.GetTimePeriod();


                //Date
                if (tp != null) {
                    holder.date.setText(tp.GetDateFormatted());
                }

                //Value
                holder.cost.setText(transaction.GetValueFormatted());


                //Activity specific differences
                if (activityType == 0) { //Expenses
                    //Split
                    if (transaction.GetSplitWith() == null) {
                        holder.split.setVisibility(View.GONE);
                    }
                    else {
                        holder.split.setVisibility(View.VISIBLE);
                        //Who owes who
                        if (transaction.GetIPaid()) {
                            holder.split.setText(activity.getString(R.string.format_ipaid, transaction.GetSplitWith(), transaction.GetSplitValueFormatted(), ProfileManager.decimalFormat.format(Math.round(transaction.GetOtherSplitPercentage() * 100.00f))));
                        }
                        else {
                            holder.split.setText(activity.getString(R.string.format_theypaid, transaction.GetMySplitValueFormatted(), ProfileManager.decimalFormat.format(Math.round(transaction.GetOtherSplitPercentage() * 100.00f))));
                        }

                        //Paid back
                        if (transaction.IsPaidBack()) {
                            holder.split.setPaintFlags(holder.split.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

                            holder.paidBack.setVisibility(View.VISIBLE);
                            holder.paidBack.setText(transaction.GetPaidBackFormatted());
                        }
                        else {
                            holder.split.setPaintFlags(0);
                            holder.paidBack.setVisibility(View.GONE);
                        }

                    }

                    //PaidBy
                    holder.paidBy.setVisibility(View.VISIBLE);
                    holder.paidByWho.setVisibility(View.VISIBLE);
                    if (transaction.GetSplitWith() != null) { holder.paidByWho.setText((transaction.GetIPaid() ? activity.getString(R.string.format_me) : transaction.GetSplitWith())); }
                    else { holder.paidByWho.setText(R.string.format_me); }


                    //Category
                    if (transaction.GetCategory().equals("")) { holder.category.setText(R.string.info_nocategory); }
                    else { holder.category.setText(transaction.GetCategory()); }

                    //Source (Company, person, etc)
                    if (transaction.GetSourceName().equals("")) { holder.sourceName.setText(R.string.info_nosource); }
                    else { holder.sourceName.setText(transaction.GetSourceName()); }

                    //Color Bar
                    Category cat = ProfileManager.GetCategory(transaction.GetCategory());
                    if (cat != null && cat.GetColor() != 0) { holder.colorbar.setBackgroundColor(cat.GetColor()); }
                    else { holder.colorbar.setBackgroundColor(Color.TRANSPARENT); }


                }
                else if (activityType == 1){ //Income
                    //Category
                    if (transaction.GetSourceName().equals("")) { holder.category.setText(R.string.info_nosource); }
                    else { holder.category.setText(transaction.GetSourceName()); }

                    //Source
                    holder.sourceName.setVisibility(View.GONE);

                    //Color Bar
                    holder.colorbar.setBackgroundColor(ProfileManager.ColorFromString(transaction.GetSourceName()));

                }


                //Descripiton
                if (transaction.GetDescription().equals("")) { holder.description.setText(R.string.info_nodescription); }
                else { holder.description.setText(transaction.GetDescription()); }


                //Children indenting
                if (parent != null && tp != null) {
                    //Parent Time Period
                    TimePeriod parent_tp = parent.GetTimePeriod();

                    //ProfileManager.Print("ParentID:" + parent.GetID());
                    //ProfileManager.Print("ExpenseID:" + expense.GetID());
                    //ProfileManager.Print("ExpenseParentID:" + expense.GetParentID());
                    //Repeat text && Repeat Expense Indenting
                    if (parent_tp != null && parent_tp.DoesRepeat() && parent_tp.GetFirstOccurrence() != null && tp.GetDate() != null) {

                        //Repeat Text
                        holder.repeat.setText(parent_tp.GetRepeatString(parent_tp.GetRepeatFrequency(), parent_tp.GetRepeatUntil()));

                        //ProfileManager.Print("Arg1:" + (parent_tp.GetFirstOccurrence().compareTo(tp.GetDate()) == 0));
                        //ProfileManager.Print("Arg2:" + (parent.GetID() == expense.GetID()));
                        //ProfileManager.Print("Parent First Occurrence:" + parent_tp.GetDate().toString(ProfileManager.simpleDateFormat));
                        //ProfileManager.Print("Expense Occurrence:" + tp.GetDate().toString(ProfileManager.simpleDateFormat));

                        if (parent_tp.GetFirstOccurrence().compareTo(tp.GetDate()) == 0 || parent.GetID() == transaction.GetID()) {
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
            return _profile.GetTransactionsInTimeFrameSize();
        }
        return -1;
    }



    //View Holder class
    public class TransactionViewHolder extends RecyclerView.ViewHolder implements View.OnLongClickListener, View.OnClickListener
    {
        LinearLayout colorbar;
        LinearLayout indent;

        CardView cv;
        TextView category;
        TextView sourceName;
        TextView description;

        TextView split;
        TextView paidBack;

        TextView date;
        TextView repeat;

        TextView paidBy;
        TextView paidByWho;
        TextView cost;

        Boolean moreInfo;


        public TransactionViewHolder(View itemView)
        {
            super(itemView);

            moreInfo = false;

            colorbar = (LinearLayout) itemView.findViewById(R.id.transaction_row_colorbar);
            indent = (LinearLayout) itemView.findViewById(R.id.transaction_row_indent);

            cv = (CardView) itemView.findViewById(R.id.transaction_row_cardView);

            category = (TextView) itemView.findViewById(R.id.transaction_row_category);
            sourceName = (TextView) itemView.findViewById(R.id.transaction_row_source);
            description = (TextView) itemView.findViewById(R.id.transaction_row_description);

            date = (TextView) itemView.findViewById(R.id.transaction_row_date);
            repeat = (TextView) itemView.findViewById(R.id.transaction_row_repeat);

            cost = (TextView) itemView.findViewById(R.id.transaction_row_cost);

            //Expense only
            split = (TextView) itemView.findViewById(R.id.expense_row_split);
            paidBack = (TextView) itemView.findViewById(R.id.expense_row_paidback);
            paidBy = (TextView) itemView.findViewById(R.id.expense_row_paidby);
            paidByWho = (TextView) itemView.findViewById(R.id.expense_row_paidby_who);


            //Short and long click listeners for the expenses context menu
            cv.setOnClickListener(this);
            cv.setOnLongClickListener(this);
        }

        @Override
        public boolean onLongClick(View v) {
            final Transaction tran = GetTransaction(getAdapterPosition());
            final Transaction tranp = GetTransactionParent(tran);

            if (tran != null && tranp != null && tran.GetTimePeriod() != null && tranp.GetTimePeriod() != null) {
                if (tran.GetTimePeriod().DoesRepeat() || tranp.GetTimePeriod().DoesRepeat()) {
                    String items[] = activity.getResources().getStringArray(R.array.RepeatingTransaction);
                    new AlertDialog.Builder(activity).setItems(items, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int item) {
                            switch (item) {
                                case 0: //Edit (parent)
                                    activity.editTransaction(tranp, _profileID);
                                    break;
                                case 1: //Edit (instance)
                                    //If the expense is not a ghost expense (only exists in the _timeframe array), then edit it normally, else clone it and blacklist the old date
                                    if (GetTransactionByID(tran.GetID()) != null) { //Child
                                        activity.editTransaction(tran, _profileID);
                                    }
                                    else { //Ghost
                                        activity.cloneTransaction(tranp, _profileID, tran.GetTimePeriod().GetDate());
                                    }

                                    break;
                                case 2: //Delete (parent)
                                    activity.deleteTransaction(tranp, true, true);
                                    break;
                                case 3: //Delete (instance)
                                    if (GetTransactionByID(tran.GetID()) != null) { //Child
                                        activity.deleteTransaction(tran, true, false);
                                    }
                                    else { //Ghost
                                        activity.deleteTransaction(tran, false, false);
                                    }
                                    break;
                                default:
                                    dialog.cancel();
                                    break;
                            }
                        }
                    }).create().show();
                }
                else {
                    String items[] = activity.getResources().getStringArray(R.array.SingleTransaction);

                    new AlertDialog.Builder(activity).setItems(items, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int item) {
                            switch (item) {
                                case 0: //Edit (this)
                                    activity.editTransaction(tran, _profileID);
                                    break;
                                case 1: //Duplicate (this)
                                    activity.duplicateTransaction(tran, _profileID);
                                    break;
                                case 2: //Delete (this)
                                    activity.deleteTransaction(tran, true, true);
                                    break;
                                default:
                                    dialog.cancel();
                                    break;
                            }
                        }
                    }).create().show();
                }
            }


            return true;
        }

        @Override
        public void onClick(View v) {
            //final Expense ex = _profile.GetExpenseAtIndexInTimeFrame(getAdapterPosition());
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
