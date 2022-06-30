package com.builderlinebr.smarttrainer.customviews;

import android.content.Context;
import android.widget.TextView;
import com.builderlinebr.smarttrainer.R;
import com.builderlinebr.smarttrainer.calculation.CalcTime;
import com.github.mikephil.charting.components.MarkerView;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;

public class MarkerInGeoGraph extends MarkerView {

    TextView textView;
    boolean isTime;

    public MarkerInGeoGraph(Context context, int layoutResource, boolean isTime) {
        super(context, layoutResource);
        textView = findViewById(R.id.marker_text_id);
        this.isTime = isTime;
    }

    @Override
    public void refreshContent(Entry e, Highlight highlight) {
        textView.setText(
                !isTime?
                        Float.toString(e.getY())
                        :new CalcTime().convertIntervalToTimeString(e.getY()));
        //Entry e0 = e;
       // e0.setX(e.getX()-20);
        super.refreshContent(e, highlight);



    }
}
