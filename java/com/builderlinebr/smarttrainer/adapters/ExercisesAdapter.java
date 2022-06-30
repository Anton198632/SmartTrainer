package com.builderlinebr.smarttrainer.adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;
import androidx.recyclerview.widget.RecyclerView;
import com.builderlinebr.smarttrainer.ExerciseDescriptionActivity;
import com.builderlinebr.smarttrainer.ExercisesActivity;
import com.builderlinebr.smarttrainer.R;
import com.builderlinebr.smarttrainer.customviews.BubbleButton;
import com.builderlinebr.smarttrainer.database.Exercises;
import com.builderlinebr.smarttrainer.viewmodel.ExercisesViewModel;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.List;

public class ExercisesAdapter extends RecyclerView.Adapter<ExercisesAdapter.ViewHolder> {

    List<Exercises> itemList;
    Context context;

    public static final int RESULT_FAVORITE = 5;


    String url;

    public ExercisesAdapter(List<Exercises> itemList, Context context) {
        this.itemList = itemList;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.exercise_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        holder.setIsRecyclable(false);

        String header = itemList.get(position).getHeader();
        holder.exerciseHeader.setText(header);
        String description = itemList.get(position).getProperties();
        holder.exerciseDescription.setText(description);

        holder.exerciseImage.setVisibility(View.VISIBLE);

        final int id = itemList.get(position).getId();

        if (ExercisesActivity.sex.equals("male")) {
            getImageFromFile(itemList.get(position).getImageMale() + ".jpg", holder.exerciseImage);
        } else {

            getImageFromFile(itemList.get(position).getImageFemale() + ".jpg", holder.exerciseImage);

        }


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!((ExercisesActivity) context).forAdd) { // Если не для добавления упражнения в тренировку

                    Intent intent = new Intent((ExercisesActivity)context, ExerciseDescriptionActivity.class);
                    intent.putExtra("exId", id);
                    intent.putExtra("exercise", itemList.get(position));
                    intent.putExtra("currentPosition", position);
                    ((ExercisesActivity)context).startActivityForResult(intent, RESULT_FAVORITE);

                } else { // Если для добавления упражнения в тренировку

                    Intent intent = new Intent();
                    intent.putExtra("exerciseId", itemList.get(position).getId());
                    ((ExercisesActivity) context).setResult(1, intent);
                    ((ExercisesActivity) context).finish();
                }
            }
        });


        holder.itemView.setOnTouchListener(new BubbleButton().onTouchListener);


        if (itemList.get(position).getFavorites() == 0) {
            holder.starImage.setVisibility(View.GONE);
        } else
            holder.starImage.setVisibility(View.VISIBLE);

        holder.starImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                itemList.get(position).setFavorites(0);
                ExercisesViewModel viewModel = new ViewModelProvider((ViewModelStoreOwner) context).get(ExercisesViewModel.class);
                viewModel.updateExercise(itemList.get(position));
                holder.starImage.setVisibility(View.GONE);
            }
        });


    }




    @Override
    public int getItemCount() {
        return itemList.size();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView exerciseHeader;
        private TextView exerciseDescription;
        private ImageView exerciseImage;
        private ImageView starImage;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            exerciseHeader = itemView.findViewById(R.id.exercise_header_id);
            exerciseDescription = itemView.findViewById(R.id.exercise_description_id);
            exerciseImage = itemView.findViewById(R.id.exercise_image_id);
            starImage = itemView.findViewById(R.id.star);
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
            Picasso.get().load(uri).into(imageView);
        }
    }

}