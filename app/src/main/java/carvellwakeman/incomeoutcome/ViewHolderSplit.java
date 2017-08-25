package carvellwakeman.incomeoutcome;

import android.support.design.widget.TextInputLayout;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import org.adw.library.widgets.discreteseekbar.DiscreteSeekBar;

import java.util.Map;


public class ViewHolderSplit //implements View.OnClickListener
{
    LinearLayout base;
    TextInputLayout TIL;
    EditText cost;
    DiscreteSeekBar percentage;
    RadioButton paid;

    //Properties
    boolean edited = false;

    //Data
    ActivityNewTransaction _parent;
    Person _person;

    public ViewHolderSplit(ActivityNewTransaction parent, final Person person, LayoutInflater inflater, ViewGroup root) {
        base = (LinearLayout) inflater.inflate(R.layout.row_layout_split, null);
        root.addView(base);

        this._parent = parent;
        this._person = person;

        //Find views
        TIL = (TextInputLayout) base.findViewById(R.id.TIL_newTransaction_personCost);
        cost = TIL.getEditText();
        percentage = (DiscreteSeekBar) base.findViewById(R.id.seekBar_newTransaction_personSplit);
        paid = (RadioButton) base.findViewById(R.id.seekBar_newTransaction_personPaid);

        //Inflate views with data
        TIL.setHint(String.format(parent.getString(R.string.tt_theircost), person.GetName()));

        percentage.setProgress(0);


        //View listeners

        //Seekbar editText_cost split percentage
        percentage.setOnProgressChangeListener(new DiscreteSeekBar.OnProgressChangeListener() {
            //Temporary values
            int initialProgress;
            int numOtherVH;
            double mainCost;

            @Override public void onProgressChanged(DiscreteSeekBar discreteSeekBar, int i, boolean b) {
                if (_parent.modifyingSplitHolder == null) {
                    _parent.modifyingSplitHolder = ViewHolderSplit.this;

                    numOtherVH = _parent.active_people.size() - 1;

                    //percentages
                    int tp = percentage.getProgress();
                    int diff = (100 - tp);

                    //Update this viewHolder's cost
                    double tv = (tp / 100.0f) * mainCost;
                    tv = (double) Math.round(tv * 100) / 100; //Round
                    cost.setText(Helper.decimalFormat.format(tv));

                    //Update all other viewholders
                    for (Map.Entry<Person, ViewHolderSplit> entry : _parent.active_people.entrySet()) {
                        ViewHolderSplit svh = entry.getValue();

                        //Mark svh as not edited
                        svh.edited = false;

                        if (entry.getKey().GetID() != _person.GetID()) { //Comment out to update THTS cost editText

                            //Update percentage slider
                            svh.percentage.setProgress(diff / numOtherVH);

                            //Calculate value
                            double v = ((100 - tp) / 100.0f) * mainCost;
                            v = v / numOtherVH; //Get this portion
                            v = (double) Math.round(v * 100) / 100; //Round

                            svh.cost.setText(Helper.decimalFormat.format(v));
                        }
                    }

                    _parent.modifyingSplitHolder = null;
                }
            }
            @Override public void onStartTrackingTouch(DiscreteSeekBar discreteSeekBar) {
                initialProgress = percentage.getProgress();
                mainCost = _parent.GetCost();
            }
            @Override public void onStopTrackingTouch(DiscreteSeekBar discreteSeekBar) {}
        });

        cost.addTextChangedListener(new TextWatcher() {
            //Temporary variables
            int numOtherVH;
            double totalCost;
            double localCost;
            double diff;
            int prog;

            @Override public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (_parent.modifyingSplitHolder == null) {
                    _parent.modifyingSplitHolder = ViewHolderSplit.this;

                    numOtherVH = 0;
                    for(ViewHolderSplit svh : _parent.active_people.values()){ if (!svh.edited) { numOtherVH++; } }

                    //Cost
                    totalCost = _parent.GetCost();
                    for (Map.Entry<Person, ViewHolderSplit> entry : _parent.active_people.entrySet()) {
                        if (entry.getValue().edited && entry.getKey().GetID() != _person.GetID()) { totalCost -= entry.getValue().GetCost(); }
                    }
                    localCost = GetCost();
                    diff = (totalCost - localCost);

                    //Update this viewHolder's percentage
                    percentage.setProgress( (int)( (localCost / _parent.GetCost()) * 100 ) );

                    //Update all other viewHolders
                    for (Map.Entry<Person, ViewHolderSplit> entry : _parent.active_people.entrySet()) {
                        if (entry != null && entry.getKey().GetID() != _person.GetID()) { // Ignore THTS cost editText
                            //Other splitviewholder
                            ViewHolderSplit svh = entry.getValue();

                            if (!svh.edited) {
                                //Update cost textbox
                                svh.cost.setText(Helper.decimalFormat.format(diff / numOtherVH));

                                //Update percentage slider
                                svh.percentage.setProgress( (int)( (svh.GetCost() / _parent.GetCost()) * 100 ) );
                            }
                        }
                    }

                    // Set this view holder as edited
                    edited = true;

                    _parent.modifyingSplitHolder = null;
                }
            }

            @Override public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
            @Override public void afterTextChanged(Editable editable) {}
        });

        paid.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (_parent.modifyingSplitHolder == null) {
                    _parent.modifyingSplitHolder = ViewHolderSplit.this;

                    for (Map.Entry<Person, ViewHolderSplit> entry : _parent.active_people.entrySet()) {
                        if (entry != null && entry.getKey().GetID() != _person.GetID()) {
                            entry.getValue().paid.setChecked(false);
                        }
                    }

                    _parent.modifyingSplitHolder = null;
                }

            }
        });

    }

    public double GetCost(){
        if (!cost.getText().toString().equals("")) {
            try {
                return Double.valueOf(cost.getText().toString());
            } catch (Exception ex){ return 0.0d; }
        }
        return 0.0d;
    }

    public boolean GetPaid() { return paid.isChecked(); }


    public void SetEnabled(boolean enabled){
        base.setEnabled(enabled);
        cost.setEnabled(enabled);
        percentage.setEnabled(enabled);
        TIL.setEnabled(enabled);
        paid.setEnabled(enabled);

        int percentColor = (enabled ? Helper.getColor(R.color.colorAccent) : Helper.getColor(R.color.ltgray));
        percentage.setTrackColor( percentColor );
        percentage.setThumbColor( percentColor, percentColor );
        percentage.setScrubberColor( percentColor );

    }

    //@Override public void onClick(View v) {}
}