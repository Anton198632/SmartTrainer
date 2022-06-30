package com.builderlinebr.smarttrainer.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.builderlinebr.smarttrainer.R;

import java.util.List;

public class DateAdapter extends RecyclerView.Adapter<DateAdapter.ViewHolder> {

    private static final int VIEW_TYPE_PADDING = 1;
    private static final int VIEW_TYPE_ITEM = 2;

    List<String> items;
    Context context;
    private int paddingWidthDate = 0;

    public DateAdapter(List<String> items, Context context, int paddingWidthDate) {
        this.items = items;
        this.context = context;
        this.paddingWidthDate = paddingWidthDate;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_ITEM) {
            return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.date_item, parent, false));
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_padding, parent, false);
            RecyclerView.LayoutParams layoutParams = (RecyclerView.LayoutParams) view.getLayoutParams();
            layoutParams.width = paddingWidthDate;
            view.setLayoutParams(layoutParams);
            return new DateAdapter.ViewHolder(view);
        }

    }

    @Override
    public int getItemViewType(int position) {
        if (items.get(position) == null) return VIEW_TYPE_PADDING;
        else return VIEW_TYPE_ITEM;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        if (items.get(position) != null)
            holder.textView.setText(items.get(position));
    }


    @Override
    public int getItemCount() {
        return items.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        TextView textView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            textView = itemView.findViewById(R.id.date_item);

        }
    }

}
