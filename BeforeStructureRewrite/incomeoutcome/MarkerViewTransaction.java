package carvellwakeman.incomeoutcome;


import android.content.Context;
import android.widget.TextView;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.MarkerView;
import com.github.mikephil.charting.data.ChartData;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.IPieDataSet;

public class MarkerViewTransaction extends MarkerView {
    private TextView tvContent;
    private PieChart chart;

    public MarkerViewTransaction (Context context, int layoutResource, PieChart chart) {
        super(context, layoutResource);
        // this markerview only displays a textview
        tvContent = (TextView) findViewById(R.id.tvContent);

        this.chart = chart;
    }

    // callbacks everytime the MarkerView is redrawn, can be used to update the
    // content (user-interface)
    @Override
    public void refreshContent(Entry e, Highlight highlight) {
        if (chart != null && chart.getData() != null) {
            IPieDataSet data = chart.getData().getDataSet();
            if (data != null){
                tvContent.setText(data.toString()); //TODO: Labels on Click
            }
            else {
                tvContent.setText("no");
            }
        }
    }

    @Override
    public int getXOffset(float offset) {
        // this will center the marker-view horizontally
        return -(getWidth() / 2);
    }

    @Override
    public int getYOffset(float offset) {
        // this will cause the marker-view to be above the selected value
        return -getHeight();
    }
}
