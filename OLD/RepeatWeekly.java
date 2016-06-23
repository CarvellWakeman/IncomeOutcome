package carvellwakeman.incomeoutcome;


import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;

public class RepeatWeekly extends Fragment {
    int EveryN;
    Boolean MonVal, TuesVal, WedVal, ThurVal, FriVal, SatVal, SunVal;

    EditText input;
    CheckBox mon, tues, wed, thur, fri, sat, sun;

    public RepeatWeekly() {
        EveryN = 0;
        MonVal = TuesVal = WedVal = ThurVal = FriVal = SatVal = SunVal = false;
    }

    //Accessors
    public int GetEveryN(){
        if (input != null){
            //Lower clamp of value to 1
            return Math.max(Integer.parseInt(input.getText().toString()), 1);
        }
        else { return 0; }
    }

    public Boolean AllDaysOff() { if ( !GetValMon() && !GetValTues() && !GetValWed() && !GetValThur() && !GetValFri() && !GetValSat() && !GetValSun() ) { return true; } else { return false; } }
    public Boolean GetValMon(){ if (mon!=null) { return mon.isChecked(); } else { return false; } }
    public Boolean GetValTues(){ if (tues!=null) { return tues.isChecked(); } else { return false; } }
    public Boolean GetValWed(){ if (wed!=null) { return wed.isChecked(); } else { return false; } }
    public Boolean GetValThur(){ if (thur!=null) { return thur.isChecked(); } else { return false; } }
    public Boolean GetValFri(){ if (fri!=null) { return fri.isChecked(); } else { return false; } }
    public Boolean GetValSat(){ if (sat!=null) { return sat.isChecked(); } else { return false; } }
    public Boolean GetValSun(){ if (sun!=null) { return sun.isChecked(); } else { return false; } }


    //Mutators
    public void SetEveryN(int n){
        EveryN = n;
        if (input != null) {
            //Lower clamp of value to 1
            input.setText(String.valueOf(Math.max(n, 1)));
        }
    }

    public void SetValMon(Boolean val){ MonVal = val; if (mon!=null) { mon.setChecked(val); } }
    public void SetValTues(Boolean val){ TuesVal = val; if (tues!=null) { tues.setChecked(val); } }
    public void SetValWed(Boolean val){ WedVal = val; if (wed!=null) { wed.setChecked(val); } }
    public void SetValThur(Boolean val){ ThurVal = val; if (thur!=null) { thur.setChecked(val); } }
    public void SetValFri(Boolean val){ FriVal = val; if (fri!=null) { fri.setChecked(val); } }
    public void SetValSat(Boolean val){ SatVal = val; if (sat!=null) { sat.setChecked(val); } }
    public void SetValSun(Boolean val){ SunVal = val; if (sun!=null) { sun.setChecked(val); } }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View myFragmentView = inflater.inflate(R.layout.fragment_repeat_weekly, container, false);

        input = (EditText) myFragmentView.findViewById(R.id.editText_repeatWeekly);
        mon = (CheckBox) myFragmentView.findViewById(R.id.checkBox_repeatWeekly_mon);
        tues = (CheckBox) myFragmentView.findViewById(R.id.checkBox_repeatWeekly_tues);
        wed = (CheckBox) myFragmentView.findViewById(R.id.checkBox_repeatWeekly_wed);
        thur = (CheckBox) myFragmentView.findViewById(R.id.checkBox_repeatWeekly_thur);
        fri = (CheckBox) myFragmentView.findViewById(R.id.checkBox_repeatWeekly_fri);
        sat = (CheckBox) myFragmentView.findViewById(R.id.checkBox_repeatWeekly_sat);
        sun = (CheckBox) myFragmentView.findViewById(R.id.checkBox_repeatWeekly_sun);

        SetEveryN(EveryN);
        SetValMon(MonVal);
        SetValTues(TuesVal);
        SetValWed(WedVal);
        SetValThur(ThurVal);
        SetValFri(FriVal);
        SetValSat(SatVal);
        SetValSun(SunVal);

        // Inflate the layout for this fragment
        return myFragmentView;
    }

}
