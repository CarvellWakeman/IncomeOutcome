package carvellwakeman.incomeoutcome;

import android.graphics.Color;
import android.graphics.Paint;
import android.support.v7.widget.CardView;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.*;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.view.animation.Transformation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class AdapterDetailsTransaction extends RecyclerView.Adapter<AdapterDetailsTransaction.TransactionViewHolder>
{
    //Calling activity context
    ActivityDetailsTransaction activity;

    //ID strings
    int _profileID;
    Profile _profile;

    //Expense vs income adapter
    int activityType = -1;

    //Constructor
    public AdapterDetailsTransaction(ActivityDetailsTransaction activity, int profileID, int activityType)
    {
        _profileID = profileID;
        _profile = ProfileManager.getInstance().GetProfileByID(profileID);

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

    @Override
    public TransactionViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_layout_transaction, parent, false);

        return new TransactionViewHolder(itemView);
    }

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
                    if (transaction.GetSplitWith() == null || transaction.GetSplitWith().equals("")) {
                        holder.split.setVisibility(View.GONE);
                        holder.paidBack.setVisibility(View.GONE);
                    }
                    else {
                        holder.split.setVisibility(View.VISIBLE);
                        holder.paidBack.setVisibility(View.VISIBLE);

                        //Who owes who
                        if (transaction.GetIPaid()) {
                            holder.split.setText(activity.getString(R.string.format_ipaid, transaction.GetSplitWith(), transaction.GetSplitValueFormatted(), ProfileManager.decimalFormat.format(Math.round(transaction.GetOtherSplitPercentage() * 100.00f))));
                        }
                        else {
                            holder.split.setText(activity.getString(R.string.format_theypaid, transaction.GetMySplitValueFormatted(), ProfileManager.decimalFormat.format(Math.round(transaction.GetMySplitPercentage() * 100.00f))));
                        }

                        //Paid back
                        if (transaction.IsPaidBack()) {
                            holder.split.setPaintFlags(holder.split.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

                            holder.paidBack.setVisibility(View.VISIBLE);
                            holder.paidBack.setText(transaction.GetPaidBackFormatted());
                        }
                        else {
                            holder.split.setPaintFlags(0);
                            holder.paidBack.setText("");
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

                    //Color circle
                    Category cat = ProfileManager.getInstance().GetCategory(transaction.GetCategory());
                    if (cat != null && cat.GetColor() != 0) {
                        holder.colorbar.setColorFilter(cat.GetColor());
                    } else {
                        holder.colorbar.setColorFilter(Color.TRANSPARENT);
                    }

                }
                else if (activityType == 1){ //Income
                    //Source
                    if (transaction.GetSourceName().equals("")) { holder.sourceName.setText(R.string.info_nosource); }
                    else { holder.sourceName.setText(transaction.GetSourceName()); }

                    //Category
                    holder.category.setVisibility(View.GONE);

                    //Color Bar
                    holder.colorbar.setColorFilter(ProfileManager.ColorFromString(transaction.GetSourceName()));

                }


                //Descripiton
                //if (transaction.GetDescription().equals("")) { holder.description.setText(R.string.info_nodescription); }
                //else { holder.description.setText(transaction.GetDescription()); }
                holder.description.setText(transaction.GetDescription());


                //Children indenting
                if (parent != null && tp != null) {
                    //Parent Time Period
                    TimePeriod parent_tp = parent.GetTimePeriod();
                    //Repeat text && Repeat Expense Indenting
                    if (parent_tp != null && parent_tp.DoesRepeat() && parent_tp.GetFirstOccurrence() != null && tp.GetDate() != null) {
                        //Expand Card
                        holder.expandCard.setVisibility(View.VISIBLE);

                        //Repeat Text
                        holder.repeat.setText(parent_tp.GetRepeatString());

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
                        holder.description.setVisibility(View.GONE);
                        holder.indent.setVisibility(View.GONE);
                        if (transaction.GetDescription().equals("")){
                            holder.expandCard.setVisibility(View.GONE);
                        }
                    }
                }


            }
        }

    }

    @Override
    public int getItemCount()
    {
        if (_profile != null) {
            return _profile.GetTransactionsInTimeFrameSize();
        }
        return -1;
    }



    //View Holder class
    public class TransactionViewHolder extends RecyclerView.ViewHolder implements PopupMenu.OnMenuItemClickListener, View.OnLongClickListener
    {
        ImageView colorbar;
        LinearLayout indent;

        CardView cv;
        RelativeLayout dateBox;

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

        ImageView overflow;
        ImageView expandCard;
        ImageView repeatIcon;

        int overflowMenu;
        boolean moreInfo;


        public TransactionViewHolder(View itemView)
        {
            super(itemView);

            //Set moreinfo to true initially
            moreInfo = false;

            colorbar = (ImageView) itemView.findViewById(R.id.transaction_row_colorbar);
            indent = (LinearLayout) itemView.findViewById(R.id.transaction_row_indent);

            cv = (CardView) itemView.findViewById(R.id.transaction_row_cardView);
            dateBox = (RelativeLayout) itemView.findViewById(R.id.transaction_row_relativelayout_date) ;

            category = (TextView) itemView.findViewById(R.id.transaction_row_category);
            sourceName = (TextView) itemView.findViewById(R.id.transaction_row_source);
            description = (TextView) itemView.findViewById(R.id.transaction_row_description);

            date = (TextView) itemView.findViewById(R.id.transaction_row_date);
            repeat = (TextView) itemView.findViewById(R.id.transaction_row_repeat);

            repeatIcon = (ImageView) itemView.findViewById(R.id.transaction_row_repeaticon);

            cost = (TextView) itemView.findViewById(R.id.transaction_row_cost);

            //Expense only
            split = (TextView) itemView.findViewById(R.id.transaction_row_split);
            paidBack = (TextView) itemView.findViewById(R.id.transaction_row_paidback);
            paidBack.setVisibility(View.GONE);
            paidBy = (TextView) itemView.findViewById(R.id.transaction_row_paidby);
            paidByWho = (TextView) itemView.findViewById(R.id.transaction_row_paidby_who);

            //Buttons
            overflow = (ImageView) itemView.findViewById(R.id.transaction_row_overflow);
            expandCard = (ImageView) itemView.findViewById(R.id.transaction_row_expand);


            //Short and long click listeners for the expenses context menu
            dateBox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    toggleMoreInfo();
                }
            });

            //Overflow click listener set per instance (due to varying overflowMenu value)
            overflow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                   OpenOverflowMenu();
                }
            });
            cv.setOnLongClickListener(this);

            moreInfoOff();
        }

        //Overflow menu
        @Override
        public boolean onMenuItemClick(MenuItem item) {
            final Transaction tran = GetTransaction(getAdapterPosition());
            final Transaction tranp = GetTransactionParent(tran);

            if (tran != null && tranp != null) {

                //If the expense is not a ghost expense (only exists in the _timeframe array), then edit it normally, else clone it and blacklist the old date
                switch (item.getItemId()) {
                    case R.id.transaction_edit_instance: //Edit(instance)
                        if (GetTransactionByID(tran.GetID()) != null) {
                            if (tran.GetID() == tranp.GetID() && tran.GetTimePeriod()!=null && (tran.GetTimePeriod().DoesRepeat() || tranp.GetTimePeriod().DoesRepeat()) ){ //Editing parent as an instance
                                activity.cloneTransaction(tranp, _profileID, tran.GetTimePeriod().GetDate()); //ProfileManager.Print("EditChildButActuallyParent");
                            } else { //Child
                                activity.editTransaction(tran, _profileID); //ProfileManager.Print("EditChild");
                            }
                        } else { //Ghost
                            activity.cloneTransaction(tranp, _profileID, tran.GetTimePeriod().GetDate()); //ProfileManager.Print("EditGhost");
                        }
                        return true;
                    case R.id.transaction_edit_all: //Edit(all, parent)
                        //ProfileManager.Print("EditParent");
                        activity.editTransaction(tranp, _profileID);
                        return true;
                    case R.id.transaction_delete_instance: //Delte(instance)
                        if (GetTransactionByID(tran.GetID()) != null) { //Child
                            if (tran.GetID() == tranp.GetID() && tran.GetTimePeriod()!=null && (tran.GetTimePeriod().DoesRepeat() || tranp.GetTimePeriod().DoesRepeat()) ){ //Deleting parent as an instance
                                activity.deleteTransaction(tran, true, false); //ProfileManager.Print("DeleteChildButActuallyParent");
                            } else { //Child
                                activity.deleteTransaction(tran, true, false); //ProfileManager.Print("DeleteChild");
                            }
                        } else { //Ghost
                            activity.deleteTransaction(tran, false, false); //ProfileManager.Print("DeleteGhost");
                        }
                        return true;
                    case R.id.transaction_delete_all: //Delete(all, parent)
                        //ProfileManager.Print("DeleteParent");
                        activity.deleteTransaction(tranp, true, true);
                        return true;
                    case R.id.transaction_duplicate: //Duplicate
                        //ProfileManager.Print("Duplicate");
                        activity.duplicateTransaction(tran, _profileID);
                        return true;
                }
            }

            return false;
        }

        @Override
        public boolean onLongClick(View v){
            OpenOverflowMenu();
            return true;
        }

        public void OpenOverflowMenu(){
            PopupMenu popup = new PopupMenu(activity, overflow);
            MenuInflater inflater = popup.getMenuInflater();

            Transaction tran = GetTransactionParent(GetTransaction(getAdapterPosition()));
            if (tran != null) {
                TimePeriod tp = tran.GetTimePeriod();
                if (tp != null){
                    overflowMenu = (tp.DoesRepeat() ? R.menu.transaction_overflow_repeat : R.menu.transaction_overflow_single);
                } else { overflowMenu = R.menu.transaction_overflow_single; }

                inflater.inflate(overflowMenu, popup.getMenu());
                popup.setOnMenuItemClickListener(TransactionViewHolder.this);
                popup.show();
            }
            else {
                ProfileManager.PrintUser(activity, ProfileManager.getString(R.string.error_transaction_not_found));
            }
        }

        //More Info (expand card)
        public void toggleMoreInfo(){
            //moreInfo = !moreInfo;
            if (!moreInfo) { moreInfoOn(); } else { moreInfoOff(); }
        }
        public void moreInfoOn() {
            if (!moreInfo) {
                moreInfo = true;

                RotateAnimation rot = new RotateAnimation(0, 180, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
                rot.setDuration(200);
                rot.setFillAfter(true);
                expandCard.startAnimation(rot);

                //int initialHeight = dateBox.getHeight();

                if (!repeat.getText().toString().equals("")) {
                    repeat.setVisibility(View.VISIBLE);
                    repeatIcon.setVisibility(View.VISIBLE);
                }
                if (!description.getText().toString().equals("")) {
                    description.setVisibility(View.VISIBLE);
                }
            }

            //expand(initialHeight, dateBox);
        }
        public void moreInfoOff() {
            if (moreInfo) {
                moreInfo = false;

                RotateAnimation rot = new RotateAnimation(180, 0, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
                rot.setDuration(200);
                //rot.setFillAfter(true);
                expandCard.startAnimation(rot);

                //int initialHeight = dateBox.getHeight();

                repeat.setVisibility(View.GONE);
                repeatIcon.setVisibility(View.GONE);
                description.setVisibility(View.GONE);
            }

            //collapse(initialHeight, dateBox);
        }
        public void expand(final int initialHeight, final View v) {
            v.measure(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
            final int targetHeight = v.getMeasuredHeight();

            v.getLayoutParams().height = initialHeight;

            Animation a = new Animation() {
                @Override
                protected void applyTransformation(float interpolatedTime, Transformation t) {
                    v.getLayoutParams().height = initialHeight + (int)( (targetHeight-initialHeight) * interpolatedTime);
                    v.requestLayout();
                }
            };

            a.setDuration(1000);
            v.startAnimation(a);
        }
        public void collapse(final int initialHeight, final View v) {
            //Set initial height (This call is made after the view has changed size)
            //v.getLayoutParams().height = initialHeight;

            //Set the target size
            final int targetHeight = v.getMeasuredHeight();
            //ProfileManager.Print(App.GetContext(), "targetHeight:" + targetHeight);

            Animation a = new Animation() {
                @Override
                protected void applyTransformation(float interpolatedTime, Transformation t) {
                    //v.getLayoutParams().height = initialHeight - (int)((targetHeight - initialHeight) * (1-interpolatedTime));
                    //v.requestLayout();
                }
            };
            if (targetHeight > 0) {
                a.setDuration(1000);
                //v.startAnimation(a);
            }
        }
    }
}
