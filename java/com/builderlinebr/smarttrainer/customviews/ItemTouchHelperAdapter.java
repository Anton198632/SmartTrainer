package com.builderlinebr.smarttrainer.customviews;

public interface ItemTouchHelperAdapter {
    boolean onItemMove(int fromPosition, int toPosition); // перемещение
    void onItemDismiss(int position); // смахивание
}
