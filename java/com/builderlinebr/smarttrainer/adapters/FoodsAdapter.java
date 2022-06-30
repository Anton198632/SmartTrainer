package com.builderlinebr.smarttrainer.adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.view.*;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;
import com.builderlinebr.smarttrainer.FoodCalcAtivity;
import com.builderlinebr.smarttrainer.R;
import com.builderlinebr.smarttrainer.customviews.BubbleButton;
import com.builderlinebr.smarttrainer.database.Food;

import java.io.File;
import java.util.List;

public class FoodsAdapter extends RecyclerView.Adapter<FoodsAdapter.ViewHolder> {

    Context context;
    List<Food> foods;

    public FoodsAdapter(Context context, List<Food> foods) {
        this.context = context;
        this.foods = foods;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new FoodsAdapter.ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.food_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {

        final String path = context.getExternalFilesDir("").getPath() + "/images/" + foods.get(position).getImage() + ".jpg";

        File imgFile = new File(path);
        if (imgFile.exists()) {
            Bitmap bitmap = BitmapFactory.decodeFile(path);
            holder.imageFood.setImageBitmap(bitmap);
        }

        holder.foodNameTextView.setText(foods.get(position).getFoodName());
        holder.proteinsTextView.setText(foods.get(position).getProteins());
        holder.fatsTextView.setText(foods.get(position).getFats());
        holder.carbohydratesTextView.setText(foods.get(position).getCarbohydrates());
        holder.caloriesTextView.setText(foods.get(position).getCalories());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                Drawable drawable = Drawable.createFromPath(path);
                builder.setMessage(
                        context.getString(R.string.dialog_add_message_one)
                                + " " + foods.get(position).getFoodName()
                                + context.getString(R.string.dialog_add_message_two))
                        .setIcon(drawable)
                        .setNegativeButton(context.getString(R.string.food_dialog_negative), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        })
                        .setPositiveButton(context.getString(R.string.food_dialog_positive), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                boolean find = false;
                                for (Food selectedFood : ((FoodCalcAtivity) context).selectedFoods) {
                                    if (selectedFood.getPk() == foods.get(position).getPk())
                                        find = true;
                                }

                                if (!find)
                                ((FoodCalcAtivity) context).selectedFoods.add(foods.get(position));

                                ((FoodCalcAtivity) context).updateSelectedFoodsList();
                            }
                        }).getContext().setTheme(R.style.DialogFoodStyle);


                AlertDialog dialog = builder.create();
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.show();

            }
        });

        holder.itemView.setOnTouchListener(new BubbleButton().onTouchListener);


    }

    @Override
    public int getItemCount() {
        return foods.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView imageFood;
        TextView foodNameTextView;
        TextView proteinsTextView;
        TextView fatsTextView;
        TextView carbohydratesTextView;
        TextView caloriesTextView;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            imageFood = itemView.findViewById(R.id.image_food);
            foodNameTextView = itemView.findViewById(R.id.food_name_id);
            proteinsTextView = itemView.findViewById(R.id.proteins_text);
            fatsTextView = itemView.findViewById(R.id.fats_text);
            carbohydratesTextView = itemView.findViewById(R.id.carbohydrates_text);
            caloriesTextView = itemView.findViewById(R.id.calories_text);
        }
    }
}
