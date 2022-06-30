package com.builderlinebr.smarttrainer;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.text.method.ScrollingMovementMethod;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;
import com.builderlinebr.smarttrainer.adapters.ExercisesAdapter;
import com.builderlinebr.smarttrainer.customviews.CustomImageViewRadius;
import com.builderlinebr.smarttrainer.database.Exercises;
import com.builderlinebr.smarttrainer.database.ImagesFemale;
import com.builderlinebr.smarttrainer.database.ImagesM;
import com.builderlinebr.smarttrainer.database.ImagesMale;
import com.builderlinebr.smarttrainer.viewmodel.ImagesFemaleViewModel;
import com.builderlinebr.smarttrainer.viewmodel.ImagesMViewModel;
import com.builderlinebr.smarttrainer.viewmodel.ImagesMaleViewModel;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import java.util.ArrayList;
import java.util.List;

public class ExerciseDescriptionActivity extends AppCompatActivity {

    Exercises exercise;
    List<String> images;

    private AdView mAdView;

    String imgM;
    Runnable imageSlider;
    Handler handler;


    int exId;
    int currentPosition;

    LinearLayout waitLayout;

    Toolbar toolbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exercise_description);


        exId = getIntent().getIntExtra("exId", -1);
        exercise = getIntent().getParcelableExtra("exercise");
        currentPosition = getIntent().getIntExtra("currentPosition", -1);

        initData();

        // Показ рекламы
        mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        if (getResources().getBoolean(R.bool.ad_use))
            mAdView.loadAd(adRequest);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.exercise_menu, menu);
        if (exercise.getFavorites() == 0) { // Если упр. не избрано, рисуем незакрашенную звездочку в меню
            menu.getItem(0).setIcon(getDrawable(R.drawable.ic_star_border_black));
        } else { // иначе закрашенную
            menu.getItem(0).setIcon(getDrawable(R.drawable.ic_star));
        }

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home: // при нажатии назад
                Intent intent = new Intent(); // формируем интент для передачи данных в родит. активити о значении избрано или нет упражн.
                intent.putExtra("isFavorite", exercise.getFavorites() == 1 ? true : false);
                intent.putExtra("exId", exId);
                intent.putExtra("currentPosition", currentPosition);
                setResult(ExercisesAdapter.RESULT_FAVORITE, intent); // отправляем результат
                finish();
                break;
            case R.id.favorite_item: // если нажать на звездочку меню
                if (exercise.getFavorites() == 0) { // если было не избрано
                    exercise.setFavorites(1); // делаем избранным
                    item.setIcon(getDrawable(R.drawable.ic_star)); // устан. иконку закрашенной звезды
                } else { // иначе
                    item.setIcon(getDrawable(R.drawable.ic_star_border_black)); // рисуем незакрашенную иконку
                    exercise.setFavorites(0);   // делаем неизбранным
                }
                break;
        }

        return super.onOptionsItemSelected(item);


    }

    public void initData() {

        waitLayout = findViewById(R.id.wait_layout);
        waitLayout.setVisibility(View.VISIBLE);

        new Handler().post(new Runnable() {
            @Override
            public void run() {

                toolbar = findViewById(R.id.toolbar);
                setSupportActionBar(toolbar);
                ActionBar actionBar = getSupportActionBar();
                if (actionBar != null) { // Если actionBar не равен null устанавливаем навигацю назад (стрелка слева вверху)
                    actionBar.setDisplayHomeAsUpEnabled(true);
                }

                getData(); // Получаем данные из БД


                ((TextView) (findViewById(R.id.exercise_dialog_header))).setText(exercise.getHeader());

                final String path = getExternalFilesDir("").getPath() + "/images/";
                final CustomImageViewRadius imageViewExercise = ((CustomImageViewRadius) findViewById(R.id.exercise_dialog_image));
                if (images.size() > 0) {

                    handler = new Handler();
                    final int[] counter = {0};
                    imageSlider = new Runnable() {
                        @Override
                        public void run() {
                            Bitmap bitmap = BitmapFactory.decodeFile(path + images.get(counter[0]));
                            imageViewExercise.setImageBitmap(bitmap);
                            counter[0]++;
                            if (counter[0] == images.size()) {
                                counter[0] = 0;
                            }
                            handler.postDelayed(this, 2000);
                        }
                    };
                    handler.post(imageSlider);
                } else {
                    imageViewExercise.setImageBitmap(null);
                }


                if (!imgM.equals("")) {
                    Bitmap bitmap = BitmapFactory.decodeFile(path + imgM);
                    ((CustomImageViewRadius) findViewById(R.id.exercise_m_image)).setImageBitmap(bitmap);
                }

                String desc = exercise.getDescription();
                ((TextView) (findViewById(R.id.exercise_description_text))).setText(desc);
                ((TextView) (findViewById(R.id.exercise_description_text))).setMovementMethod(new ScrollingMovementMethod());


//                final Button inFavoriteButton = (Button) findViewById(R.id.in_like_button);
//                inFavoriteButton.setOnClickListener(ExercisesDescriptionDialogFragment.this);
//                if (exercise.getFavorites() == 0) {
//                    inFavoriteButton.setText(getString(R.string.in_favorites));
//                    favorites = 0;
//                } else {
//                    inFavoriteButton.setText(getString(R.string.out_favorites));
//                    favorites = 1;
//                }
//
//                imgClose = findViewById(R.id.image_close);
//                imgClose.setOnClickListener(ExercisesDescriptionDialogFragment.this);

                waitLayout.setVisibility(View.GONE);
            }
        });


    }


    private void getData() {
        ImagesMViewModel imagesMViewModel = new ViewModelProvider(this).get(ImagesMViewModel.class);
        List<ImagesM> imagesMs = imagesMViewModel.getImagesLink(exId);
        imgM = "";
        if (imagesMs.size() > 0) {
            imgM = imagesMs.get(0).getLink() + ".png";
        }

        ImagesMaleViewModel imagesMaleViewModel = new ViewModelProvider(this).get(ImagesMaleViewModel.class);
        List<ImagesMale> imagesMales = imagesMaleViewModel.getImagesLink(exId);
        ImagesFemaleViewModel imagesFemaleViewModel = new ViewModelProvider(this).get(ImagesFemaleViewModel.class);
        List<ImagesFemale> imagesFemales = imagesFemaleViewModel.getImagesLink(exId);

        images = new ArrayList<>();
        if (ExercisesActivity.sex.equals("male")) {
            if (imagesMales.size() > 0) {
                for (ImagesMale imagesMale : imagesMales) {
                    images.add(imagesMale.getLink() + ".jpg");
                }
            }
        } else {
            if (imagesFemales.size() > 0) {
                for (ImagesFemale imagesFemale : imagesFemales) {
                    images.add(imagesFemale.getLink() + ".jpg");
                }
            } else {
                for (ImagesMale imagesMale : imagesMales) {
                    images.add(imagesMale.getLink() + ".jpg");
                }
            }
        }
    }
}
