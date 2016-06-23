package carvellwakeman.incomeoutcome;


import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

public class RepeatDaily extends Fragment {
    int EveryN;

    EditText input;

    public RepeatDaily() {
        EveryN = 0;
    }

    //Accessor
    public int GetEveryN(){
        if (input != null){
            //Lower clamp of value to 1
            return Math.max(Integer.parseInt(input.getText().toString()), 1);
        }
        else { return 0; }
    }

    //Mutator
    public void SetEveryN(int n){
        EveryN = n;
        if (input != null) {
            //Lower clamp of value to 1
            input.setText(String.valueOf(Math.max(n, 1)));
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View myFragmentView = inflater.inflate(R.layout.fragment_repeat_daily, container, false);
        input = (EditText) myFragmentView.findViewById(R.id.editText_repeatFrequency);

        SetEveryN(EveryN);

        // Inflate the layout for this fragment
        return myFragmentView;
    }

}
