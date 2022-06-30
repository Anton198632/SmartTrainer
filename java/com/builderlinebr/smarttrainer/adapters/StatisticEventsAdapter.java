package com.builderlinebr.smarttrainer.adapters;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.view.*;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;
import com.builderlinebr.smarttrainer.R;
import com.builderlinebr.smarttrainer.StatisticActivity;
import com.builderlinebr.smarttrainer.calculation.CalcTime;
import com.builderlinebr.smarttrainer.customviews.BubbleButton;
import com.builderlinebr.smarttrainer.customviews.ItemTouchHelperAdapter;
import com.builderlinebr.smarttrainer.dialogs.EventEditDialog;
import com.builderlinebr.smarttrainer.model.exercises.WorkoutExerciseEvent;
import com.builderlinebr.smarttrainer.viewmodel.EventsViewModel;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import de.hdodenhof.circleimageview.CircleImageView;

import java.io.File;
import java.util.List;

public class StatisticEventsAdapter extends RecyclerView.Adapter<StatisticEventsAdapter.ViewHolder> implements ItemTouchHelperAdapter {

    Context context;
    List<WorkoutExerciseEvent> events;

    public StatisticEventsAdapter(Context context, List<WorkoutExerciseEvent> events) {
        this.context = context;
        this.events = events;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new StatisticEventsAdapter.ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.workout_exercise_evet_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {

        holder.numberOfTextView.setText(Integer.toString(position + 1));
        if (events.get(position).exercise != null) {
            holder.exerciseNameTextView.setText(events.get(position).exercise.getHeader());
            getImageFromFile(events.get(position).exercise.getImageMale() + ".jpg", holder.imageView);
        }


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment dialogFragment = new EventEditDialog(
                        events.get(position).getCount(),
                        events.get(position).getValue(),
                        events.get(position).getInterval());
                FragmentManager fragmentManager = ((StatisticActivity) context).getSupportFragmentManager();
                dialogFragment.show(fragmentManager, "workExerciseDialog");

                fragmentManager.executePendingTransactions();
                Dialog dialog = dialogFragment.getDialog();
                dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        if (EventEditDialog.dialogResult == EventEditDialog.DIALOG_RESULT_OK) {

                            int count = EventEditDialog.c;
                            float value = EventEditDialog.v;
                            long interval = EventEditDialog.i;

                            EventsViewModel eventsViewModel = new ViewModelProvider((StatisticActivity) context).get(EventsViewModel.class);
                            eventsViewModel.updateEvent(count, value, interval, events.get(position).getPk());

                            events.get(position).setCount(count);
                            events.get(position).setValue(value);
                            events.get(position).setInterval(interval);
                            ((StatisticActivity) context).events.get(position).setCount(count);
                            ((StatisticActivity) context).events.get(position).setValue(value);
                            ((StatisticActivity) context).events.get(position).setInterval(interval);

                            // Пересчитываем статистику для других фрагментов (графического и табличного)
                            ((StatisticActivity) context).reCalcExerciseStatistic();

                            notifyDataSetChanged();
                        }
                    }
                });
            }
        });

        holder.itemView.setOnTouchListener(new BubbleButton().onTouchListener);

        holder.countTextView.setText(Integer.toString(events.get(position).getCount()));
        holder.valueTextView.setText(Float.toString(events.get(position).getValue()));
        holder.intervalTextView.setText(new CalcTime().ConvertLongToTimeString(events.get(position).getInterval()));


    }

    @Override
    public int getItemCount() {
        return events.size();
    }

    @Override
    public boolean onItemMove(int fromPosition, int toPosition) {
        return false;
    }

    @Override
    public void onItemDismiss(int position) {
        // Удаляем из БД
        EventsViewModel eventsViewModel = new ViewModelProvider((StatisticActivity) context).get(EventsViewModel.class);
        eventsViewModel.delete(events.get(position).getPk());

        // удаляем из списка
        events.remove(position);
        ((StatisticActivity) context).events.remove(position);
        // Пересчитываем статистику для других фрагментов (графического и табличного)
        ((StatisticActivity) context).reCalcExerciseStatistic();

        notifyItemRemoved(position);
        notifyDataSetChanged();


    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView numberOfTextView;
        TextView exerciseNameTextView;
        TextView countTextView;
        TextView valueTextView;
        TextView intervalTextView;
        CircleImageView imageView;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            numberOfTextView = itemView.findViewById(R.id.number_of_e_text_view);
            exerciseNameTextView = itemView.findViewById(R.id.exercise_name_e_text_view);
            countTextView = itemView.findViewById(R.id.count_e_text_view);
            valueTextView = itemView.findViewById(R.id.value_e_text_view);
            intervalTextView = itemView.findViewById(R.id.interval_e_text_view);
            imageView = itemView.findViewById(R.id.exercise_e_image);

        }
    }

    private void getImageFromFile(String imageName, ImageView imageView) {
        final String path = context.getExternalFilesDir("").getPath() + "/images/";
        if (!new File(path).exists()) {
            new File(path).mkdir();
        }
        File imgFile = new File(path + imageName);
        if (imgFile.exists()) {

            Uri uri = Uri.fromFile(imgFile);
            Picasso.get().load(uri).into(imageView, new Callback() {
                @Override
                public void onSuccess() {

                }

                @Override
                public void onError(Exception e) {

                }
            });
        }
    }

}
