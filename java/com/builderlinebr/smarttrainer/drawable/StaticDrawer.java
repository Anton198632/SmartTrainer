package com.builderlinebr.smarttrainer.drawable;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.view.View;
import com.builderlinebr.smarttrainer.model.exercises.ExerciseStatistic;

import java.util.List;

public class StaticDrawer {

    Paint p;
    int width;
    int height;


    public StaticDrawer(View view) {

        p = new Paint();

        p.setColor(Color.GREEN); // цвет рисования
        p.setStyle(Paint.Style.STROKE); // очертание (нет заливки)
        p.setStrokeWidth(2f); // толщина линии
        p.setAntiAlias(true); // автосглаживание


        // Получаем параметры холста
        width = view.getWidth();
        height = view.getHeight();

    }

    public Canvas drawGraphicExercises(String header, Canvas canvas, List<ExerciseStatistic> statistics) {
        // Рисуем ось х
        Path path = new Path();
        path.moveTo(20, height - height / 2);
        path.lineTo(width - 20, height - height / 2);
        path.close();
        p.setColor(Color.BLACK);
        canvas.drawPath(path, p);

        int maxCount = 0;
        for (ExerciseStatistic statistic : statistics) {
            if (statistic.countAll > maxCount) maxCount = statistic.countAll;
        }
        maxCount = (int) (maxCount * 1.4f);


        int countExercise = statistics.size();
        // countExercise += 2;

        //
        int i = 0;
        for (ExerciseStatistic statistic : statistics) {
            p.setStrokeWidth(2f); // толщина линии
            int currentHeight = statistic.countAll * (height / 2) / maxCount;
            int currentWidth = ((width - 80) / countExercise);
            int x = i * currentWidth + 40;
            int y = height - height / 2 - 2;
            int step = 16711680 / (statistics.size());
            int currentColorInt = -16711680 + step * (i);
            p.setStyle(Paint.Style.FILL_AND_STROKE);
            p.setColor(currentColorInt);
            canvas.drawRect(x, y, x + currentWidth - 5, y - currentHeight, p);

            // маленкий прямоугольник
            p.setTextSize(14f);
            p.setStrokeWidth(1f); // толщина линии
            canvas.drawRect(40, y + 30 * i + 20, 60, y + 30 * i + 40, p);

            // название упражнения
            p.setColor(Color.BLACK);
            canvas.drawText(statistic.exercise.getHeader(), 80, y + 30 * i + 40, p);

            // подпись количества раз над столбцами
            canvas.drawText(Integer.toString(statistic.countAll), x + currentWidth / 2, y - currentHeight - 10, p);

            // заготовок графика
            p.setTextSize(24f);
            canvas.drawText(header, 20, 30, p);


            i++;
        }


        return canvas;


    }


}
