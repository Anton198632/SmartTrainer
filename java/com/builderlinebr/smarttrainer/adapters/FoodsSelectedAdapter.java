package com.builderlinebr.smarttrainer.adapters;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;
import com.builderlinebr.smarttrainer.FoodCalcAtivity;
import com.builderlinebr.smarttrainer.R;
import com.builderlinebr.smarttrainer.customviews.BubbleButton;
import com.builderlinebr.smarttrainer.database.Food;
import com.builderlinebr.smarttrainer.dialogs.FoodsTableDialogFragment;

import java.text.DecimalFormat;

public class FoodsSelectedAdapter extends RecyclerView.Adapter<FoodsSelectedAdapter.ViewHolder> {


    Context context;

    public FoodsSelectedAdapter(Context context) {
        this.context = context;

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new FoodsSelectedAdapter.ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.food_table_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        final Food food = ((FoodCalcAtivity) context).selectedFoods.get(position);
        holder.textView.setText(food.getFoodName());
        holder.foodsCountTextView.setText(food.count + " г");

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final DialogFragment dialogFragment = new FoodsTableDialogFragment(holder.foodsCountTextView.getText().toString());
                final FragmentManager fragmentManager = ((FoodCalcAtivity) context).getSupportFragmentManager();
                dialogFragment.show(fragmentManager, "foodsDialog");

                fragmentManager.executePendingTransactions();
                Dialog dialog = dialogFragment.getDialog();
                dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        ((FoodCalcAtivity) context).selectedFoods.get(position).count = ((FoodsTableDialogFragment) dialogFragment).value;
                        holder.foodsCountTextView.setText(((FoodsTableDialogFragment) dialogFragment).value + " г");
                        calc();
                    }
                });
            }
        });

        holder.deleteImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ((FoodCalcAtivity) context).selectedFoods.remove(position);
                calc();
                ((FoodCalcAtivity) context).updateSelectedFoodsList();

           }
        });
        holder.deleteImage.setOnTouchListener(new BubbleButton().onTouchListener);

        holder.itemView.setOnTouchListener(new BubbleButton().onTouchListener);

        calc();

    }

    private void calc() {
        double pr = 0;
        double f = 0;
        double car = 0;
        double cal = 0;
        for (Food item : ((FoodCalcAtivity) context).selectedFoods) {
            pr += Double.parseDouble(!item.getProteins().equals("")?item.getProteins():"0") * item.count / 100;
            f += Double.parseDouble(!item.getFats().equals("")?item.getFats():"0") * item.count / 100;
            car += Double.parseDouble(!item.getCarbohydrates().equals("")?item.getCarbohydrates():"0") * item.count / 100;
            cal += Double.parseDouble(!item.getCalories().equals("")?item.getCalories():"0") * item.count / 100;
        }
        DecimalFormat df = new DecimalFormat("####.##");
        ((FoodCalcAtivity) context).foodsResult.setText("Бел. - " + df.format(pr) + "  Жир. - " + df.format(f) + "  Угл. - " + df.format(car) + "  Ккал. - " + df.format(cal));
    }


    @Override
    public int getItemCount() {
        return ((FoodCalcAtivity) context).selectedFoods.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView textView;
        TextView foodsCountTextView;
        ImageView deleteImage;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.food_selected_name);
            foodsCountTextView = itemView.findViewById(R.id.foods_count);
            deleteImage = itemView.findViewById(R.id.food_selected_delete_image);

        }
    }
}
