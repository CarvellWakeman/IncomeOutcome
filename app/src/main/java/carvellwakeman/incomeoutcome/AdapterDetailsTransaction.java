package carvellwakeman.incomeoutcome;

import android.animation.LayoutTransition;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.v7.widget.CardView;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.*;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class AdapterDetailsTransaction extends RecyclerView.Adapter<AdapterDetailsTransaction.TransactionViewHolder>
{
    //Calling activity context
    ActivityDetailsTransaction _activity;

    //Budget
    Budget _budget;

    //Transactions
    ArrayList<Transaction> _transactions;

    //Activity type (Expense or income) (0 or 1)
    int activityType = -1;

    //Constructor
    public AdapterDetailsTransaction(ActivityDetailsTransaction parent, int budgetID, int activityType)
    {
        _budget = BudgetManager.getInstance().GetBudget(budgetID);

        this._activity = parent;
        this.activityType = activityType;

        this._transactions = new ArrayList<>();

        //Load up transactions from budget
        GetTransactions();
    }


    //Custom transaction getters
    public void GetTransactions(){
        _transactions.clear();

        if (activityType == 0) { //Expense
            _transactions.addAll(_budget.GetTransactionsInTimeframe(Transaction.TRANSACTION_TYPE.Expense));
        }
        else if (activityType == 1) { //Income
            _transactions.addAll(_budget.GetTransactionsInTimeframe(Transaction.TRANSACTION_TYPE.Income ));
        }
    }


    @Override
    public TransactionViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_layout_transaction, parent, false);
        return new TransactionViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final TransactionViewHolder holder, int position)
    {
        if (_budget != null) {
            Transaction transaction = _transactions.get(position);
            holder.bind(transaction);
            //Helper.Print(App.GetContext(), "Bind ViewHolder" + String.valueOf(position));
        }

    }

    @Override
    public int getItemCount() {
        return _transactions.size();
    }


    // ViewHolder overflow actions
    public Transaction GetTransactionParent(Transaction transaction) {
        if (transaction.GetParentID() != 0) {
            return _budget.GetTransaction(transaction.GetParentID());
        }
        return transaction;
    }

    public void handleOverflowAction(Transaction tran, MenuItem action){
        //Find parent transaction
        final Transaction tranp = GetTransactionParent(tran);

        //Take action on it
        if (tranp != null) {
            Intent intent;

            switch (action.getItemId()) {

                case R.id.transaction_edit_instance: // Edit(instance)
                    Helper.Log(App.GetContext(), "AdaDetTran", "Edit(Instance) of repeating tran " + tranp.GetSource());

                    intent = new Intent(_activity, ActivityNewTransaction.class);
                    intent.putExtra("activitytype", activityType);
                    intent.putExtra("budget", _budget.GetID());
                    intent.putExtra("transaction", tran);
                    intent.putExtra("editstate", ActivityNewTransaction.EDIT_STATE.EditInstance.ordinal());
                    _activity.startActivityForResult(intent, 1);

                    break;

                case R.id.transaction_edit_all: // Edit(all / parent)
                    Helper.Log(App.GetContext(), "AdaDetTran", "Edit(All) of " + tranp.GetSource());

                    intent = new Intent(_activity, ActivityNewTransaction.class);
                    intent.putExtra("activitytype", activityType);
                    intent.putExtra("budget", _budget.GetID());
                    intent.putExtra("transaction", tranp);
                    intent.putExtra("editstate", ActivityNewTransaction.EDIT_STATE.Edit.ordinal());
                    _activity.startActivityForResult(intent, 2);

                    break;

                case R.id.transaction_delete_instance: // Delete(instance)
                    Helper.Log(App.GetContext(), "AdaDetTran", "Delete(instance) of Repeat tran " + tranp.GetSource());

                    tranp.GetTimePeriod().AddBlacklistDate(tran.GetTimePeriod().GetDate(), false);
                    DatabaseManager.getInstance().insert(tranp, true);

                    break;

                case R.id.transaction_delete_all: // Delete(all, parent)
                    Helper.Log(App.GetContext(), "AdaDetTran", "Delete(all) of " + tranp.GetSource());

                    ArrayList<Integer> children = tranp.GetChildren();
                    for (int i = 0; i < children.size(); i++){
                        Transaction child = _budget.GetTransaction(children.get(i));
                        DatabaseManager.getInstance().remove(child);
                        _budget.RemoveTransaction(child);
                    }

                    _budget.RemoveTransaction(tranp);
                    DatabaseManager.getInstance().remove(tranp);

                    break;
            }

            GetTransactions();
            notifyDataSetChanged();

        }
    }



    //View Holder class
    public class TransactionViewHolder extends RecyclerView.ViewHolder implements PopupMenu.OnMenuItemClickListener, View.OnClickListener
    {
        ImageView colorbar;
        LinearLayout indent;

        CardView cv;
        RelativeLayout base;
        RelativeLayout dateBox;

        TextView category;
        TextView sourceName;
        TextView description;

        LinearLayout split;
        TextView paidBack;

        TextView date;
        TextView repeat;
        TextView debug;

        TextView paidBy;
        TextView cost;

        ImageView overflow;
        ImageView expandCard;
        ImageView repeatIcon;

        int overflowMenu;
        boolean moreInfo;

        //int datebox_collapsed_height;


        public TransactionViewHolder(View itemView)
        {
            super(itemView);

            //Set moreinfo to true initially
            moreInfo = false;

            colorbar = (ImageView) itemView.findViewById(R.id.transaction_row_colorbar);
            indent = (LinearLayout) itemView.findViewById(R.id.transaction_row_indent);

            cv = (CardView) itemView.findViewById(R.id.transaction_row_cardView);
            base = (RelativeLayout) itemView.findViewById(R.id.transaction_row_relativelayout_base);
            dateBox = (RelativeLayout) itemView.findViewById(R.id.transaction_row_relativelayout_date) ;

            category = (TextView) itemView.findViewById(R.id.transaction_row_category);
            sourceName = (TextView) itemView.findViewById(R.id.transaction_row_source);
            description = (TextView) itemView.findViewById(R.id.transaction_row_description);

            date = (TextView) itemView.findViewById(R.id.transaction_row_date);
            repeat = (TextView) itemView.findViewById(R.id.transaction_row_repeat);
            debug = (TextView) itemView.findViewById(R.id.transaction_row_debug);

            repeatIcon = (ImageView) itemView.findViewById(R.id.transaction_row_repeaticon);

            cost = (TextView) itemView.findViewById(R.id.transaction_row_cost);

            //Expense only
            split = (LinearLayout) itemView.findViewById(R.id.transaction_row_linearlayout_split);
            paidBack = (TextView) itemView.findViewById(R.id.transaction_row_paidback);
            paidBack.setVisibility(View.GONE);
            paidBy = (TextView) itemView.findViewById(R.id.transaction_row_paidby);

            //Buttons
            overflow = (ImageView) itemView.findViewById(R.id.transaction_row_overflow);
            expandCard = (ImageView) itemView.findViewById(R.id.transaction_row_expand);

            //Animation info
            LayoutTransition lt = new LayoutTransition();
            lt.disableTransitionType(LayoutTransition.DISAPPEARING);
            base.setLayoutTransition(lt);

            //Short and long click listeners for the expenses context menu
            dateBox.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View view) { toggleMoreInfo(); }
            });

            //Overflow click listener set per instance (due to varying overflowMenu value)
            overflow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                   OpenOverflowMenu();
                }
            });
            cv.setOnClickListener(this);

        }

        public void bind(Transaction transaction){
            if (transaction != null) {
                //Parent
                Transaction parent = _budget.GetTransaction(transaction.GetParentID());

                //Value
                cost.setText(Helper.currencyFormat.format(transaction.GetValue()));

                //Activity specific differences
                if (activityType == 0) { //Expenses
                    //Category
                    if (transaction.GetCategory() == 0) {
                        category.setText(R.string.info_nocategory);
                    } else {
                        Category cat = CategoryManager.getInstance().GetCategory(transaction.GetCategory());

                        if (cat != null) {
                            category.setText(cat.GetTitle());

                            //Color circle
                            if (cat.GetColor() != 0) {
                                colorbar.setColorFilter(cat.GetColor());
                            } else {
                                colorbar.setColorFilter(Color.TRANSPARENT);
                            }
                        }
                    }

                    //Split
                    if (transaction.IsSplit()) {
                        //Clear previous split lines
                        split.removeAllViews();

                        split.setVisibility(View.VISIBLE);
                        paidBack.setVisibility(View.VISIBLE);

                        HashMap<Integer, Double> valueSplit = transaction.GetSplitArray();

                        for (Map.Entry<Integer, Double> entry : valueSplit.entrySet()) {
                            //Ignore the user if they paid
                            if (transaction.GetPaidBy() != entry.getKey() ) {
                                TextView tv = new TextView(_activity);

                                Person payer = PersonManager.getInstance().GetPerson(transaction.GetPaidBy());
                                Person payee = PersonManager.getInstance().GetPerson(entry.getKey());

                                if (payer != null && payee != null) {
                                    tv.setText(String.format(Helper.getString(R.string.format_paid),
                                            payee.GetName(),
                                            (entry.getKey()==0 ? "" : "s"), //Plurality (You OWE A / A OWES You)
                                            payer.GetName(),
                                            Helper.currencyFormat.format(entry.getValue()),
                                            transaction.GetSplitPercentage(entry.getKey())
                                    ));
                                }

                                split.addView(tv);
                            }

                        }


                        //Paid back
                        if (transaction.GetPaidBack() != null) {
                            for(int i=0; i < split.getChildCount(); i++){
                                TextView child = (TextView)split.getChildAt(i);
                                child.setPaintFlags(child.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                            }

                            paidBack.setVisibility(View.VISIBLE);
                            paidBack.setText(String.format(Helper.getString(R.string.info_paidback_format), transaction.GetPaidBack().toString(Helper.getString(R.string.date_format))));
                        }
                        else {
                            for(int i=0; i < split.getChildCount(); i++){
                                TextView child = (TextView)split.getChildAt(i);
                                child.setPaintFlags(0);
                            }

                            paidBack.setVisibility(View.GONE);
                            paidBack.setText("");
                        }

                    } else {
                        split.setVisibility(View.GONE);
                        paidBack.setVisibility(View.GONE);
                    }


                    //PaidBy
                    paidBy.setVisibility(View.VISIBLE);
                    //Helper.Log(App.GetContext(), "AdaDetTran", transaction.GetSource() + " paidby(" + String.valueOf(transaction.GetPaidBy()) + ")");
                    if (transaction.GetPaidBy() != Person.Me.GetID()) {
                        Person p = PersonManager.getInstance().GetPerson(transaction.GetPaidBy());
                        //Helper.Log(App.GetContext(), "AdaDetTran", transaction.GetSource() + " PaidBy Person:" + (p==null?"null":p.GetName()));
                        if (p != null){
                            paidBy.setText(String.format(Helper.getString(R.string.info_paidby), p.GetName()));
                        }
                    } else {
                        paidBy.setText(String.format(Helper.getString(R.string.info_paidby), Helper.getString(R.string.format_me)));
                    }

                }
                else if (activityType == 1){ //Income
                    //Category
                    category.setVisibility(View.GONE);

                    //Color circle
                    colorbar.setColorFilter(Helper.ColorFromString(transaction.GetSource()));
                }


                //Source (Company, person, etc)
                if (transaction.GetSource().equals("")) { sourceName.setText(R.string.info_nosource); }
                else { sourceName.setText(transaction.GetSource()); }

                //Descripiton
                description.setText(transaction.GetDescription());


                //Time Period
                TimePeriod tp = transaction.GetTimePeriod();

                if (tp != null) {
                    //Date
                    date.setText(tp.GetDateFormatted());

                    //Repeating
                    repeat.setText(tp.GetRepeatString());

                    //Children indenting
                    if (parent != null) {
                        TimePeriod parent_tp = parent.GetTimePeriod();
                        if (parent_tp != null) {
                            indent.setVisibility(View.VISIBLE);
                            //Repeating
                            repeat.setText(parent_tp.GetRepeatString());
                            moreInfoOff();
                        }
                    } else {
                        indent.setVisibility(View.GONE);
                        if (transaction.GetTimePeriod() != null && transaction.GetTimePeriod().DoesRepeat()){ moreInfoOn(); }
                    }

                }

                // Behavior
                if (!description.getText().equals("") || !repeat.getText().equals("")){
                    expandCard.setVisibility(View.VISIBLE);
                }

                // Debug
                debug.setText(transaction.GetTimePeriod().GetBlacklistDatesString());
                if (!debug.getText().toString().equals("")) { debug.setVisibility(View.VISIBLE); }
            }

        }

        //Open overflow menu
        @Override public void onClick(View v){ OpenOverflowMenu(); }


        //Overflow menu
        @Override
        public boolean onMenuItemClick(MenuItem item) {
            //handleOverflowAction(_transactions.get(getAdapterPosition()), item);
            Transaction tran = _transactions.get(getAdapterPosition());

            // Handle action
            handleOverflowAction(tran, item);

            return false;
        }


        public void OpenOverflowMenu(){
            //Get parent if it exists (if it doesn't, get the current transaction)
            Transaction tran = GetTransactionParent(_transactions.get(getAdapterPosition()));
            if (tran != null) {
                //Menu object
                PopupMenu popup = new PopupMenu(_activity, overflow);
                MenuInflater inflater = popup.getMenuInflater();

                overflowMenu = R.menu.transaction_overflow_single;

                //Repeating transaction vs non repeating transaction
                TimePeriod tp = tran.GetTimePeriod();
                if (tp != null && tp.DoesRepeat()){
                    overflowMenu = R.menu.transaction_overflow_repeat;
                }

                //Inflate menu with options
                inflater.inflate(overflowMenu, popup.getMenu());
                popup.setOnMenuItemClickListener(TransactionViewHolder.this);
                popup.show();
            } else { Helper.PrintUser(_activity, Helper.getString(R.string.error_transaction_not_found)); }
        }


        //More Info (expand card)
        public void toggleMoreInfo(){
            if (!moreInfo) { moreInfoOn(); } else { moreInfoOff(); }
        }
        public void moreInfoOn() {
            moreInfo = true;

            RotateAnimation rot = new RotateAnimation(0, 180, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
            rot.setDuration(200);
            rot.setFillAfter(true);
            expandCard.startAnimation(rot);

            if (!repeat.getText().toString().equals("")) {
                repeat.setVisibility(View.VISIBLE);
                repeatIcon.setVisibility(View.VISIBLE);
            }
            if (!description.getText().toString().equals("")) {
                description.setVisibility(View.VISIBLE);
            }
        }
        public void moreInfoOff() {
            moreInfo = false;

            RotateAnimation rot = new RotateAnimation(180, 0, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
            rot.setDuration(200);
            rot.setFillAfter(true);
            expandCard.startAnimation(rot);

            repeat.setVisibility(View.GONE);
            repeatIcon.setVisibility(View.GONE);
            description.setVisibility(View.GONE);
        }

    }
}
