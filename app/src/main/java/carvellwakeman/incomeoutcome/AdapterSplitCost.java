package carvellwakeman.incomeoutcome;

import android.support.design.widget.TextInputLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import org.adw.library.widgets.discreteseekbar.DiscreteSeekBar;

import java.util.ArrayList;
import java.util.HashMap;

public class AdapterSplitCost extends RecyclerView.Adapter<AdapterSplitCost.SplitViewHolder>
{
    ActivityNewTransaction parent;

    ArrayList<Person> people;
    ArrayList<Person> active_people;

    HashMap<Person, Double> people_cost;

    HashMap<Person, Float> percentages;

    HashMap<Person, SplitViewHolder> holders;

    //Avoid infinite loop
    int modifyingPerson = -1;

    public AdapterSplitCost(ActivityNewTransaction _parent)
    {
        parent = _parent;

        //Make you a person
        Person you = new Person(Helper.getString(R.string.misc_your));
        you.SetID(0);

        //Add you to the list
        people = PersonManager.getInstance().GetPeople();

        //Person visibility
        active_people = new ArrayList<>();
        active_people.add(you);

        //Cost
        people_cost = new HashMap<>();

        //Split percentage
        percentages = new HashMap<>();
        percentages.put(you, 50.0f);

        for (Person p : people){
            percentages.put(p, 0.0f);
            people_cost.put(p, 0.0d);
        }

        //View holders
        holders = new HashMap<>();
    }

    @Override
    public SplitViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_layout_split, parent, false);
        return new SplitViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final SplitViewHolder holder, int position)
    {
        //Person
        Person person = active_people.get(position);

        holder.TIL.setHint(String.format(Helper.getString(R.string.tt_theircost), person.GetName()));

        holder.percentage.setProgress( Math.round(percentages.get(person)) );

        //Special case for you
        if (person.GetID() == 0){
            holder.paid.setChecked(true);
            holder.percentage.setProgress(100);
        }

        //Store holder
        holders.put(person, holder);
    }

    @Override public int getItemCount() {
        return active_people.size();
    }

    //Person activity
    public void SetActive(Person person, boolean active){
        if (active){ //Activate
            if (!active_people.contains(person)) {
                active_people.add(person);
            }
        } else { //Deactivate
            if (active_people.contains(person)) {
                active_people.remove(person);
                percentages.put(person, 0.0f);
            }
        }
        notifyDataSetChanged();
    }

    public Double GetSplit(Person person){
        return people_cost.get(person);
    }
    public boolean Getpaid(Person person){
        return holders.get(person).paid.isChecked();
    }

    public ArrayList<Person> GetPeople(){
        return active_people;
    }


    public class SplitViewHolder extends RecyclerView.ViewHolder //implements View.OnClickListener
    {
        TextInputLayout TIL;
        EditText cost;
        DiscreteSeekBar percentage;
        RadioButton paid;


        public SplitViewHolder(View itemView) {
            super(itemView);

            TIL = (TextInputLayout) itemView.findViewById(R.id.TIL_newTransaction_personCost);
            cost = TIL.getEditText();
            percentage = (DiscreteSeekBar) itemView.findViewById(R.id.seekBar_newTransaction_personSplit);
            paid = (RadioButton) itemView.findViewById(R.id.seekBar_newTransaction_personPaid);


            //View listeners
            /*
            //Seekbar editText_cost split percentage
            percentage.setOnProgressChangeListener(new DiscreteSeekBar.OnProgressChangeListener() {
                @Override public void onProgressChanged(DiscreteSeekBar discreteSeekBar, int i, boolean b) {
                    if (modifyingPerson == -1) {
                        modifyingPerson = getAdapterPosition();

                        //This percentage
                        int tp = percentage.getProgress();
                        for (Person p : active_people) {
                            if (holders.get(p) != null && holders.get(p) != SplitViewHolder.this) {
                                holders.get(p).percentage.setProgress( holders.get(p).percentage.getProgress() + i ); //((100 - tp) / (active_people.size() - 1))
                            }
                        }
                    }
                }
                @Override public void onStartTrackingTouch(DiscreteSeekBar discreteSeekBar) {}
                @Override public void onStopTrackingTouch(DiscreteSeekBar discreteSeekBar) { modifyingPerson = -1; }
            });
            */

            paid.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    if (modifyingPerson == -1) {
                        modifyingPerson = getAdapterPosition();

                        for (Person p : active_people) {
                            if (holders.get(p) != null && holders.get(p) != SplitViewHolder.this) {
                                holders.get(p).paid.setChecked(false);
                            }
                        }

                    }
                    modifyingPerson = -1;
                }
            });

        }

        public void SetProgress(int progress){

        }


        //@Override public void onClick(View v) {}
    }
}
