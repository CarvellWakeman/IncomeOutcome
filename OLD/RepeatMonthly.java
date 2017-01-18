package carvellwakeman.incomeoutcome;


import android.app.Fragment;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

public class RepeatMonthly extends Fragment {
    int EveryN;
    int DayOfMonth;

    int noInfiniteLoopPlease = 0;

    EditText input;
    EditText input2;

    public RepeatMonthly() {
        EveryN = 0;
        DayOfMonth = 0;
    }

    //Accessors
    public int GetEveryN(){
        if (input != null){
            //Lower clamp of value to 1
            return Math.max(Integer.parseInt(input.getText().toString()), 1);
        }
        else { return 0; }
    }
    public int GetDayOfMonth(){
        if (input2 != null){
            int i = Integer.parseInt(input2.getText().toString());
            //Clamp between 1 and 31
            return Math.max(Math.min(i, 31), 1);
        }
        else { return 0; }
    }


    //Mutators
    public void SetEveryN(int n){
        EveryN = n;
        if (input != null) {
            //Lower clamp of value to 1
            input.setText(String.valueOf(Math.max(n, 1)));
        }
    }
    public void SetDayOfMonth(int n){
        DayOfMonth = n;
        if (input2 != null) {
            //Clamp between 1 and 31
            input2.setText(String.valueOf(Math.max(Math.min(n, 31), 1)));
        }
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View myFragmentView = inflater.inflate(R.layout.fragment_repeat_monthly, container, false);

        input = (EditText) myFragmentView.findViewById(R.id.editText_repeatMonthly);
        input2 = (EditText) myFragmentView.findViewById(R.id.editText_repeatMonthly2);

        input2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (noInfiniteLoopPlease == 0){
                    noInfiniteLoopPlease = 1;

                    int i = Integer.parseInt(input2.getText().toString());
                    //Clamp day of month to values between 1 and 31
                    if (i > 31){ input2.setText("31"); }
                    else if (i < 1) { input.setText("1"); }
                }
                else {
                    noInfiniteLoopPlease = 0;
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        SetEveryN(EveryN);
        SetDayOfMonth(DayOfMonth);

        // Inflate the layout for this fragment
        return myFragmentView;
    }

}
