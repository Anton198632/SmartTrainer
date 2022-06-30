package com.builderlinebr.smarttrainer.adapters;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Outline;
import android.graphics.Rect;
import android.net.Uri;
import android.view.*;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;
import com.builderlinebr.smarttrainer.ExerciseDescriptionActivity;
import com.builderlinebr.smarttrainer.ExercisesActivity;
import com.builderlinebr.smarttrainer.R;
import com.builderlinebr.smarttrainer.WorkoutExercisesActivity;
import com.builderlinebr.smarttrainer.calculation.CalcCollections;
import com.builderlinebr.smarttrainer.calculation.CalcTime;
import com.builderlinebr.smarttrainer.customviews.BubbleButton;
import com.builderlinebr.smarttrainer.customviews.ItemTouchHelperAdapter;
import com.builderlinebr.smarttrainer.database.WorkoutExercises;
import com.builderlinebr.smarttrainer.dialogs.WorkoutExerciseInvkeDialogFragment;
import com.builderlinebr.smarttrainer.model.exercises.WorkoutExerciseEvent;
import com.builderlinebr.smarttrainer.model.exercises.WorkoutExerciseModel;
import com.builderlinebr.smarttrainer.viewmodel.WorkoutExercisesViewModel;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.*;

public class WorkoutExercisesAdapter extends RecyclerView.Adapter<WorkoutExercisesAdapter.ViewHolder> implements ItemTouchHelperAdapter {

    List<WorkoutExerciseModel> items;
    Context context;

    public static final int RESULT_FAVORITE = 5;

    private static final int VIEW_TYPE_PADDING = 1;
    private static final int VIEW_TYPE_ITEM = 2;
    private int paddingWidthDate = 0;

    private int selectedItem = -1;


    public WorkoutExercisesAdapter(List<WorkoutExerciseModel> items, Context context, int paddingWidthDate) {
        this.items = items;
        this.context = context;

        this.paddingWidthDate = paddingWidthDate;
    }

    public void removeItems(List<WorkoutExerciseModel> itemsForDelete) {
        items.removeAll(itemsForDelete);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_ITEM) {
            return new WorkoutExercisesAdapter.ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.workout_exercise_item, parent, false));
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_padding, parent, false);
            RecyclerView.LayoutParams layoutParams = (RecyclerView.LayoutParams) view.getLayoutParams();
            layoutParams.width = paddingWidthDate;
            view.setLayoutParams(layoutParams);
            return new WorkoutExercisesAdapter.ViewHolder(view);
        }

    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        if (items.get(position) == null) {
            //  holder.itemView.setVisibility(View.GONE);

        } else {
            holder.itemView.setVisibility(View.VISIBLE);
            if (items.get(position).typeItem == 0) {
                //    holder.cardView.setVisibility(View.VISIBLE);
                // holder.exerciseName.setText(items.get(position).exercise.getHeader());

                if (((WorkoutExercisesActivity) context).forGirl == 0)
                    getImageFromFile(items.get(position).exercise.getImageMale() + ".jpg", holder.exerciseImage);
                else getImageFromFile(items.get(position).exercise.getImageFemale() + ".jpg", holder.exerciseImage);

                // Увеличение масштаба элемента при долгом нажатии и уменьшение при отпускании
                holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {

                        if (((WorkoutExercisesActivity) context).thread == null && ((WorkoutExercisesActivity) context).isMy == 1) {

                            v.setScaleX(1.05f);
                            v.setScaleY(1.05f);

                            if (!new CalcCollections().findPosition(((WorkoutExercisesActivity) context).selectItems, position)) {
                                ((WorkoutExercisesActivity) context).selectItems.add(position);
                                Collections.sort(((WorkoutExercisesActivity) context).selectItems, new Comparator<Integer>() {
                                    @Override
                                    public int compare(Integer o1, Integer o2) {
                                        return o1 - o2;
                                    }
                                });
                                holder.linearLayout.setBackground(context.getDrawable(R.drawable.image_border));
                                holder.linearLayout.setElevation(10f);
                            } else {
                                List<Integer> p = new ArrayList<>();
                                p.add(position);
                                ((WorkoutExercisesActivity) context).selectItems.removeAll(p);
                                holder.linearLayout.setBackgroundResource(0);
                                holder.linearLayout.setElevation(context.getResources().getDimension(R.dimen.elevation));
                            }

                            ((WorkoutExercisesActivity) context).replaceMenu();
                        }

                        return false;
                    }
                });


                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent((WorkoutExercisesActivity) context, ExerciseDescriptionActivity.class);
                        intent.putExtra("exId", items.get(position).exercise.getId());
                        intent.putExtra("exercise", items.get(position).exercise);
                        intent.putExtra("currentPosition", position);
                        if (((WorkoutExercisesActivity) context).forGirl == 0)
                            ExercisesActivity.sex = "male";
                        else
                            ExercisesActivity.sex = "female";
                        ((WorkoutExercisesActivity) context).startActivityForResult(intent, RESULT_FAVORITE);
                    }
                });

                if (new CalcCollections().findPosition(((WorkoutExercisesActivity) context).selectItems, position)) {
                    holder.linearLayout.setBackground(context.getDrawable(R.drawable.image_border));
                    holder.linearLayout.setElevation(10f);
                } else {
                    holder.linearLayout.setBackgroundResource(0);
                    holder.linearLayout.setElevation(context.getResources().getDimension(R.dimen.elevation));
                }

//                holder.itemView.setOnTouchListener(new View.OnTouchListener() {
//                    @Override
//                    public boolean onTouch(View v, MotionEvent event) {
//                        if (event.getAction() == MotionEvent.ACTION_UP) {
//                            v.setScaleX(1f);
//                            v.setScaleY(1f);
//                        }
//                        return false;
//                    }
//                });
                //------------------------------

                // --------- Выполнено/Отменено ----------------------------
                //  holder.invokeButton.setOutlineProvider(((WorkoutExercisesActivity)context).shadowProvider);
                holder.invokeButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(final View v) {
                        String buttonText = ((TextView) v).getText().toString();
                        if (buttonText.equals(context.getResources().getString(R.string.invoke))) {

                            int count = 0;
                            float value = 0;
                            WorkoutExerciseEvent oldEvent = new CalcCollections().getWorkoutExerciseEvent(((WorkoutExercisesActivity) context).workoutEventsOld, items.get(position).pk);
                            if (oldEvent != null) {
                                count = oldEvent.getCount();
                                value = oldEvent.getValue();
                            }

                            DialogFragment dialogFragment = new WorkoutExerciseInvkeDialogFragment(count, value);
                            FragmentManager fragmentManager = ((WorkoutExercisesActivity) context).getSupportFragmentManager();
                            dialogFragment.show(fragmentManager, "workExerciseDialog");

                            fragmentManager.executePendingTransactions();
                            Dialog dialog = dialogFragment.getDialog();
                            dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                                @Override
                                public void onCancel(DialogInterface dialog) {
                                    if (WorkoutExerciseInvkeDialogFragment.dialogResult == WorkoutExerciseInvkeDialogFragment.DIALOG_RESULT_OK) {

                                        WorkoutExerciseEvent event = new WorkoutExerciseEvent();
                                        event.setDateTime(System.currentTimeMillis() / 1000);
                                        event.setInterval(((WorkoutExercisesActivity) context).interval);
                                        event.dateTimeString = ((WorkoutExercisesActivity) context).timerTextId.getText().toString();
                                        event.setCount(WorkoutExerciseInvkeDialogFragment.c);
                                        event.setValue(WorkoutExerciseInvkeDialogFragment.v);
                                        event.setWorkoutId(items.get(position).workoutId);
                                        event.setWorkoutExercisePk(items.get(position).pk);
                                        ((WorkoutExercisesActivity) context).workoutEvents.add(event);

//                                        ((TextView) v).setText(context.getResources().getString(R.string.uninvoke));
//                                        ((TextView) v).setBackground(context.getDrawable(R.drawable.dialog_button_light));
                                        v.setVisibility(View.INVISIBLE);


                                        if (((WorkoutExercisesActivity) context).setInvokeData(items.get(position).pk)) {
                                            ((WorkoutExercisesActivity) context).setVisibilityData(View.VISIBLE);
                                            holder.linearLayout.setBackground(context.getDrawable(R.drawable.image_border_green));

                                        }

                                    }
                                }
                            });


                        } else {
//                            ((TextView) v).setText(context.getResources().getString(R.string.invoke));
//                            ((TextView) v).setBackground(context.getDrawable(R.drawable.dialog_button));

//                            WorkoutExerciseEvent delWE = new WorkoutExerciseEvent();
//                            delWE.setWorkoutExercisePk(-1);
//                            for (WorkoutExerciseEvent workoutEvent : ((WorkoutExercisesActivity) context).workoutEvents) {
//                                if (workoutEvent.getWorkoutExercisePk() == items.get(position).pk) {
//                                    delWE = workoutEvent;
//                                }
//                            }
//                            if (delWE.getWorkoutExercisePk() != -1)
//                                ((WorkoutExercisesActivity) context).workoutEvents.remove(delWE);
                        }
                    }
                });

                holder.invokeButton.setOnTouchListener(new BubbleButton().onTouchListener);

                if (findInEvents(items.get(position).pk)) {
//                    holder.invokeButton.setText(context.getResources().getString(R.string.uninvoke));
//                    holder.invokeButton.setBackground(context.getDrawable(R.drawable.dialog_button_light));

                    holder.invokeButton.setVisibility(View.INVISIBLE);
                    holder.invokeButtonBack.setVisibility(View.INVISIBLE);
                    holder.linearLayout.setBackground(context.getDrawable(R.drawable.image_border_green));
                } else {

                    holder.invokeButton.setVisibility(View.VISIBLE);
                    holder.invokeButtonBack.setVisibility(View.VISIBLE);
                    holder.invokeButton.setText(context.getResources().getString(R.string.invoke));
                    holder.invokeButton.setBackground(context.getDrawable(R.drawable.dialog_button));
                    holder.linearLayout.setBackground(null);

                }

                // -------------------------------------------------------

                // ------ Если тренировка запущена/остановлена ------------------------
                if (((WorkoutExercisesActivity) context).thread == null) {
                    holder.invokeButton.setVisibility(View.INVISIBLE);
                    holder.invokeButtonBack.setVisibility(View.INVISIBLE);
                } else {
                    if (!findInEvents(items.get(position).pk)) // если упражнение не выполнено
                        holder.invokeButton.setVisibility(View.VISIBLE);
                    holder.invokeButtonBack.setVisibility(View.VISIBLE);
                }
                // --------------------------------------------------------

                holder.counterExercise.setText(Integer.toString(position));

            } else {
                //    holder.cardView.setVisibility(View.GONE);
            }

            WorkoutExerciseEvent oldEvent = new CalcCollections().getWorkoutExerciseEvent(((WorkoutExercisesActivity) context).workoutEventsOld, items.get(position).pk);
            if (oldEvent != null) {
                StringBuilder sb = new StringBuilder();
                String interval = new CalcTime().ConvertLongToTimeString(oldEvent.getInterval());
                sb.append(oldEvent.getCount() + " / " + oldEvent.getValue() + " / " + interval);
                holder.prestatisticTextView.setText(sb.toString());
                holder.prestatisticTextView.setVisibility(View.VISIBLE);
            } else {
                holder.prestatisticTextView.setVisibility(View.INVISIBLE);
            }
        }
    }


    @Override
    public int getItemViewType(int position) {
        if (items.get(position) == null) return VIEW_TYPE_PADDING;
        else return VIEW_TYPE_ITEM;
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    @Override
    public boolean onItemMove(int fromPosition, int toPosition) { // Для перемещения items между собой

        if (((WorkoutExercisesActivity) context).isMy != 0)
            if (items.get(fromPosition) != null
                    && items.get(toPosition) != null
                    && items.get(fromPosition).exercise != null
                    && items.get(toPosition).exercise != null) {

                if (fromPosition < toPosition) {
                    for (int i = fromPosition; i < toPosition; i++) {
                        Collections.swap(items, i, i + 1);
                    }
                } else {
                    for (int i = fromPosition; i > toPosition; i--) {
                        Collections.swap(items, i, i - 1);
                    }
                }
                notifyItemMoved(fromPosition, toPosition);
                notifyDataSetChanged();

                // производим изменения в БД
                WorkoutExercisesViewModel workoutExercisesViewModel = new ViewModelProvider((WorkoutExercisesActivity) context).get(WorkoutExercisesViewModel.class);

                WorkoutExercises workoutExercises = new WorkoutExercises(
                        items.get(fromPosition).pk,
                        items.get(fromPosition).workoutId,
                        items.get(fromPosition).exercise.getId(),
                        items.get(toPosition).order);
                workoutExercisesViewModel.updateWorkoutExercise(workoutExercises);
                workoutExercises = new WorkoutExercises(
                        items.get(toPosition).pk,
                        items.get(toPosition).workoutId,
                        items.get(toPosition).exercise.getId(),
                        items.get(fromPosition).order);
                workoutExercisesViewModel.updateWorkoutExercise(workoutExercises);
            }
        return true;
    }

    @Override
    public void onItemDismiss(int position) { // для смахивания
        items.remove(position);
        notifyItemRemoved(position);
    }


    public class ViewHolder extends RecyclerView.ViewHolder {

        // TextView exerciseName;
        //   CardView cardView;

        ImageView exerciseImage;
        LinearLayout linearLayout;

        TextView counterExercise;
        TextView invokeButton;
        TextView invokeButtonBack;
        TextView prestatisticTextView;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            //  exerciseName = itemView.findViewById(R.id.workout_exercise_name);
            //    cardView = itemView.findViewById(R.id.exercise_item_id);

            linearLayout = itemView.findViewById(R.id.workout_exercise_image_layout);

            exerciseImage = itemView.findViewById(R.id.workout_exercise_image);
            if (exerciseImage != null) {
                linearLayout.setOutlineProvider(shadowProvider);
            }


            invokeButton = itemView.findViewById(R.id.invoke_button);
            invokeButtonBack = itemView.findViewById(R.id.invoke_button_back);
            counterExercise = itemView.findViewById(R.id.counter_exercise_text);
            prestatisticTextView = itemView.findViewById(R.id.prestatistic_text_view);


        }

        ViewOutlineProvider shadowProvider = new ViewOutlineProvider() {
            @Override
            public void getOutline(View view, Outline outline) {
                Rect rect = new Rect(0, 2, view.getWidth(), view.getHeight());
                float dpi = ((WorkoutExercisesActivity) context).getResources().getDisplayMetrics().densityDpi; // вычисляем плотность пикселей (dpi)
                float px = 20f * (dpi / 160);    // вычислем радиус углов прямоугольника
                outline.setRoundRect(rect, px);
            }
        };
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

    private boolean findInEvents(int pk) {
        boolean find = false;
        for (WorkoutExerciseEvent item : ((WorkoutExercisesActivity) context).workoutEvents) {
            if (item.getWorkoutExercisePk() == pk) {
                find = true;
                break;
            }
        }
        return find;
    }
}
