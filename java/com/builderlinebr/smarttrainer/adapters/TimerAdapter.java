package com.builderlinebr.smarttrainer.adapters;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.builderlinebr.smarttrainer.R;

import java.util.List;


public class TimerAdapter extends BaseAdapter {

    private List<String> items;
    private Context context;
    private LayoutInflater inflater;

    public TimerAdapter(List<String> items, Context context, LayoutInflater inflater) {
        this.items = items;
        this.context = context;
        this.inflater = inflater;
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public Object getItem(int position) {
        return items.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View view = convertView;
        if (view == null){
            view = inflater.inflate(R.layout.timer_item, parent, false);
        }
        TextView textView = view.findViewById(R.id.timer_item_id);
        textView.setText(Integer.toString(position+1) + ".  " + items.get(position));

        return textView;
    }
}
