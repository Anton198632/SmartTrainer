package com.builderlinebr.smarttrainer.adapters;

import android.animation.Animator;
import android.content.Context;
import android.os.Build;
import android.view.*;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import com.builderlinebr.smarttrainer.FragmentGeoStatisticMap;
import com.builderlinebr.smarttrainer.R;
import com.builderlinebr.smarttrainer.customviews.BubbleButton;

import java.util.List;

public class NumbersAdapter extends RecyclerView.Adapter<NumbersAdapter.ViewHolder> {

    List<Integer> items;
    Context context;
    Fragment fragment;

    public NumbersAdapter(List<Integer> items, Context context, Fragment fragment) {
        this.items = items;
        this.context = context;
        this.fragment = fragment;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.number_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        holder.numberTextView.setText(Integer.toString(position + 1));
        holder.numberTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((FragmentGeoStatisticMap)fragment).updateCoordinate(position);
            }
        });

        holder.itemView.setOnTouchListener(new BubbleButton().onTouchListener);

//        holder.numberTextView.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                switch (event.getAction()){
//                    case MotionEvent.ACTION_DOWN: // если нажата на view
//                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) { // Проверка версии андройд >= 5
//                            show(holder.numberTextView, (int) event.getX(), (int) event.getY());
//                        }
//                        break;
//                    case MotionEvent.ACTION_UP: // если отжата view
//                        holder.numberTextView.setVisibility(View.VISIBLE);
//
//
//                }
//                return false;
//            }
//        });

    }

    @Override
    public int getItemCount() {
        return items.size();
    }


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP) // Только для андрой 5 и выше
    public  void  show(View view, int cx, int cy){ // метод анимации расширения круга
        int maxRadius = view.getHeight();
        Animator animator = ViewAnimationUtils.createCircularReveal(view, cx, cy, 0, maxRadius);
        animator.setDuration(250);
        animator.start();

    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView numberTextView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            numberTextView = itemView.findViewById(R.id.number_text_view);
        }
    }
}
