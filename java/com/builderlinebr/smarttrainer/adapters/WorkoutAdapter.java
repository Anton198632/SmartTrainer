package com.builderlinebr.smarttrainer.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import com.builderlinebr.smarttrainer.MapActivity;
import com.builderlinebr.smarttrainer.MyWorkoutsActivity;
import com.builderlinebr.smarttrainer.R;
import com.builderlinebr.smarttrainer.WorkoutExercisesActivity;
import com.builderlinebr.smarttrainer.customviews.BubbleButton;
import com.builderlinebr.smarttrainer.customviews.ItemTouchHelperAdapter;
import com.builderlinebr.smarttrainer.database.Workouts;
import com.squareup.picasso.Picasso;
import de.hdodenhof.circleimageview.CircleImageView;

import java.io.File;
import java.util.List;

public class WorkoutAdapter extends RecyclerView.Adapter<WorkoutAdapter.ViewHolder> implements ItemTouchHelperAdapter {

    Context context;
    List<Workouts> itemList;


    public WorkoutAdapter(Context context, List<Workouts> itemList) {
        this.context = context;
        this.itemList = itemList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new WorkoutAdapter.ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.workout_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        holder.workoutNameText.setText(itemList.get(position).getWorkoutName());
        holder.workoutDescriptionText.setText(itemList.get(position).getWorkoutDescription());

        String photoFile = context.getExternalFilesDir("").getPath() + "/images/" + itemList.get(position).getWorkoutImage();
        File file = new File(photoFile);
        if (file.exists()) {
            if (!itemList.get(position).getWorkoutImage().equals("")) {
                Uri uri = Uri.fromFile(file);
                Picasso.get().load(uri).into(holder.photoImg);
                //holder.photoImg.setVisibility(View.VISIBLE);
            } else {
                //holder.photoImg.setVisibility(View.GONE);
                holder.photoImg.setImageResource(R.drawable.icon_400);
            }
        } else {
            holder.photoImg.setImageResource(R.drawable.icon_400);
        }

        View.OnClickListener clickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (((MyWorkoutsActivity) context).selectedPosition != position) {
                    if (((MyWorkoutsActivity) context).isMy != 2) {
                        Intent intent = new Intent(context, WorkoutExercisesActivity.class);
                        intent.putExtra("workoutId", itemList.get(position).getId());
                        intent.putExtra("forGirl", itemList.get(position).getForGirl());
                        intent.putExtra("workoutName", itemList.get(position).getWorkoutName());

                        ((MyWorkoutsActivity) context).startActivity(intent);
                    } else {
                        Intent intentMap = new Intent(context, MapActivity.class);
                        intentMap.putExtra("geoName", itemList.get(position).getWorkoutName());
                      //  intentMap.putExtra("image", itemList.get(position).getWorkoutImage());
                        intentMap.putExtra("workoutId", itemList.get(position).getId());
                        context.startActivity(intentMap);
                    }
                }
            }
        };

        holder.itemView.setOnClickListener(clickListener);
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                if (((MyWorkoutsActivity)context).isMy == 1){
                    ((MyWorkoutsActivity) context).selectedPosition = position;
                    ((MyWorkoutsActivity) context).toolbar.getMenu().clear();
                    ((MyWorkoutsActivity) context).getMenuInflater().inflate(R.menu.workout_edit_menu, ((MyWorkoutsActivity) context).toolbar.getMenu());
                    notifyDataSetChanged();
                }
                return false;
            }
        });

        holder.itemView.setOnTouchListener(new BubbleButton().onTouchListener);

        if (((MyWorkoutsActivity) context).selectedPosition == position) {
            holder.cardView.setBackgroundColor(context.getResources().getColor(R.color.colorSecond));
            holder.workoutNameText.setTextColor(context.getResources().getColor(R.color.colorLiteText));
            holder.workoutDescriptionText.setTextColor(context.getResources().getColor(R.color.colorTextDescriptionLite));
        } else {
            holder.cardView.setBackgroundColor(Color.WHITE);
            holder.workoutNameText.setTextColor(context.getResources().getColor(R.color.colorDarkText));
            holder.workoutDescriptionText.setTextColor(context.getResources().getColor(R.color.colorTextDescription));
        }


//        holder.imageWorkoutDelete.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                AlertDialog alertDialog = new AlertDialog.Builder(context, R.style.DialogWorkout)
//                        .setMessage(context.getString(R.string.confirm_workout_delete_text) + "«" + itemList.get(position).getWorkoutName() + "»")
//                        .setNegativeButton("Нет", new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialog, int which) {
//
//                            }
//                        }).setPositiveButton("Да", new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialog, int which) {
//                                new ViewModelProvider((MyWorkoutsActivity) context).get(WorkoutsViewModel.class).delete(itemList.get(position));
//                                ((MyWorkoutsActivity) context).initWorkouts();
//                            }
//                        }).create();
//                alertDialog.requestWindowFeature(FEATURE_NO_TITLE);
//                alertDialog.show();
//            }
//        });
//
//        holder.imageWorkoutEdit.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                final DialogFragment dialogFragment = new WorkoutAddDialog(context, itemList.get(position));
//                FragmentManager fragmentManager = ((MyWorkoutsActivity) context).getSupportFragmentManager();
//                dialogFragment.show(fragmentManager, "addWorkoutDialog");
//
//                fragmentManager.executePendingTransactions();
//                dialogFragment.getDialog().setOnCancelListener(new DialogInterface.OnCancelListener() {
//                    @Override
//                    public void onCancel(DialogInterface dialog) {
//                        WorkoutAddDialog workoutAddDialog = (WorkoutAddDialog) dialogFragment;
//                        if (workoutAddDialog.result) {
//                            String workoutName = workoutAddDialog.editTextWorkoutName.getText().toString();
//                            String workoutDescription = workoutAddDialog.editTextWorkoutDescription.getText().toString();
//
//                            Workouts newWorkout = new Workouts();
//                            newWorkout.setWorkoutName(workoutName);
//                            newWorkout.setWorkoutDescription(workoutDescription);
//                            newWorkout.setWorkoutImage(workoutAddDialog.photoFileName);
//                            if (workoutAddDialog.workout != null) newWorkout.setId(workoutAddDialog.workout.getId());
//
//                            new ViewModelProvider((MyWorkoutsActivity) context).get(WorkoutsViewModel.class).addNewWorkout(newWorkout);
//
//                            ((MyWorkoutsActivity) context).initWorkouts();
//
//
//                        }
//                    }
//                });
//            }
//        });


    }




    @Override
    public int getItemCount() {
        return itemList.size();
    }

    @Override
    public boolean onItemMove(int fromPosition, int toPosition) {
        return false;
    }

    @Override
    public void onItemDismiss(final int position) {

        if (((MyWorkoutsActivity)context).isMy!=0)
        ((MyWorkoutsActivity) context).confirmDeleteWorkout(position);
//        AlertDialog alertDialog = new AlertDialog.Builder(context, R.style.DialogWorkout)
//                .setMessage(context.getString(R.string.confirm_workout_delete_text) + "«" + itemList.get(position).getWorkoutName() + "»")
//                .setNegativeButton("Нет", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        notifyDataSetChanged();
//                    }
//                }).setPositiveButton("Да", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        new ViewModelProvider((MyWorkoutsActivity) context).get(WorkoutsViewModel.class).delete(itemList.get(position));
//                        ((MyWorkoutsActivity) context).initWorkouts();
//                        itemList.remove(position);
//                        notifyDataSetChanged();
//                    }
//                }).create();
//        alertDialog.requestWindowFeature(FEATURE_NO_TITLE);
//        alertDialog.show();

    }


    public static class ViewHolder extends RecyclerView.ViewHolder {

        CardView cardView;
        TextView workoutNameText;
        TextView workoutDescriptionText;
        CircleImageView photoImg;
//        ImageView imageWorkoutDelete;
//        ImageView imageWorkoutEdit;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            workoutNameText = itemView.findViewById(R.id.workout_name_text);
            workoutDescriptionText = itemView.findViewById(R.id.workout_description_text);
            photoImg = itemView.findViewById(R.id.workout_photo_id);
            cardView = itemView.findViewById(R.id.workout_id);
//            imageWorkoutDelete = itemView.findViewById(R.id.workout_delete_id);
//            imageWorkoutEdit = itemView.findViewById(R.id.workout_edit_id);

        }
    }
}
