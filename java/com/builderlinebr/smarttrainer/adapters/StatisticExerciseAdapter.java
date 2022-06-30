package com.builderlinebr.smarttrainer.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.builderlinebr.smarttrainer.R;

import java.util.List;

public class StatisticExerciseAdapter extends RecyclerView.Adapter<StatisticExerciseAdapter.ViewHolder> {


    List<Float> items;
    List<Float> itemsOld;
    Context context;

    String[] header;

    public StatisticExerciseAdapter(List<Float> items, List<Float> itemsOld, Context context) {
        this.items = items;
        this.items.add(0,0f);
        this.itemsOld = itemsOld;
        this.itemsOld.add(0, 0f);
        this.context = context;
    }



    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.statistic_table_exercise_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if (position>0) {
            holder.setNumberTV.setText(Integer.toString(position));

            float oldF = 0f;
            float currentF = 0f;

            if (items.size() > position)
                currentF = items.get(position);


            if (itemsOld.size() > position)
                oldF = itemsOld.get(position);

            holder.countTV.setText(Float.toString(currentF));
            holder.oldCountTV.setText(Float.toString(oldF));

            if (oldF>currentF){
              //  holder.countTV.setTextColor(context.getResources().getColor(R.color.colorLiteText));
                holder.countTV.setBackgroundColor(Color.parseColor("#40ff0000")); // красный с прозрачностью 40
            } else if (currentF>oldF){
               // holder.countTV.setTextColor(context.getResources().getColor(R.color.colorLiteText));
                holder.countTV.setBackgroundColor(Color.parseColor("#4003ff00")); // зеленый с прозрачностью 40
            }

        } else{
            holder.itemView.setBackgroundColor(Color.parseColor("#40D0D0D0"));
            holder.setNumberTV.setText(context.getResources().getString(R.string.set));
            holder.setNumberTV.setTextSize(10f);
            holder.setNumberTV.setTextColor(context.getResources().getColor(R.color.colorDarkText));
            holder.oldCountTV.setText(context.getResources().getString(R.string.pre_workout));
            holder.oldCountTV.setTextSize(10f);
            holder.oldCountTV.setTextColor(context.getResources().getColor(R.color.colorDarkText));
            holder.countTV.setText(context.getResources().getString(R.string.current_workout));
            holder.countTV.setTextSize(10f);
            holder.countTV.setTextColor(context.getResources().getColor(R.color.colorDarkText));
        }


    }

    @Override
    public int getItemCount() {
        return items.size() >= itemsOld.size() ? items.size() : itemsOld.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView setNumberTV;
        TextView oldCountTV;
        TextView countTV;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            setNumberTV = itemView.findViewById(R.id.set_number_tv);
            oldCountTV = itemView.findViewById(R.id.old_count_tv);
            countTV = itemView.findViewById(R.id.count_tv);
        }
    }
}
