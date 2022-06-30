package com.builderlinebr.smarttrainer;

import android.content.Intent;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.*;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.builderlinebr.smarttrainer.adapters.ExercisesAdapter;
import com.builderlinebr.smarttrainer.database.Exercises;
import com.builderlinebr.smarttrainer.database.ImagesFemale;
import com.builderlinebr.smarttrainer.database.ImagesMale;
import com.builderlinebr.smarttrainer.dialogs.ExercisesInfoDialogFragment;
import com.builderlinebr.smarttrainer.viewmodel.ExercisesViewModel;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

import static android.widget.AbsListView.OnScrollListener.SCROLL_STATE_FLING;
import static android.widget.AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL;

public class ExercisesActivity extends AppCompatActivity {

    private AdView mAdView;

    RecyclerView recyclerView;

    EditText editTextSearch;

    Toolbar collapsToolbar;
    CollapsingToolbarLayout collapsingToolbarLayout;

    AppBarLayout collapsAppBar;

    private Menu collapsMenu; // глобальная переменная, хранящая копию оригенального меню
    private boolean appBarExpand = true; // флаг того, что развернут AppBarLayout

    FloatingActionButton upFab;

    public static String sex;
    public boolean forAdd;

    public List<ImagesMale> imagesMales;
    public List<ImagesFemale> imagesFemales;

    List<Exercises> myItemList;

    FrameLayout frameLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exercises);

        sex = getIntent().getStringExtra("sex");
        if (sex != null && sex.equals("female")) {
            ((ImageView) findViewById(R.id.toolbar_image)).setImageResource(R.drawable.girl_for_toolbar);
        } else if (sex == null) {
            sex = "male";
        }
        forAdd = getIntent().getBooleanExtra("forAdd", false);

        // Фрагмент ожидания загрузки данных
        frameLayout = findViewById(R.id.wait_layout);

        // -------- Заполняем recyclerView в потоке и эметируем прогрессбаром ожидание
        recyclerView = findViewById(R.id.rec_view);
        frameLayout.setVisibility(View.VISIBLE);
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                ExercisesViewModel viewModel = new ViewModelProvider(ExercisesActivity.this).get(ExercisesViewModel.class);
                myItemList = viewModel.getSearchList(0);
                // Заполняем RecyclerView

                recyclerView.setHasFixedSize(true); // для постоянных списков для лучшей производительности

                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(ExercisesActivity.this);
                recyclerView.setLayoutManager(linearLayoutManager);

                ExercisesAdapter myAdapter = new ExercisesAdapter(myItemList, ExercisesActivity.this);
                recyclerView.setAdapter(myAdapter);
                frameLayout.setVisibility(View.GONE);
            }
        });

        //-----------------------------------------


        upFab = findViewById(R.id.up_fab);
        upFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recyclerView.scrollToPosition(0);
                collapsAppBar.setExpanded(true, true);
            }
        });

        final int btn_initPosY = upFab.getScrollY();
        recyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {

                if (newState == SCROLL_STATE_TOUCH_SCROLL || newState == SCROLL_STATE_FLING) {   // Анимированно скрываем кнопку при прокрутке ListView
                    upFab.animate().cancel();
                    upFab.animate().translationYBy(300);
                } else {
                    upFab.animate().cancel();
                    upFab.animate().translationY(btn_initPosY);
                }
            }
        });

        // Определяем клик на floatActionBar
        ((FloatingActionButton) findViewById(R.id.collaps_fab)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                collapsAppBar.setExpanded(false, true);
            }
        });


        // Созадем ActionBar
        collapsToolbar = findViewById(R.id.collaps_toolbar);
        setSupportActionBar(collapsToolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) { // Если actionBar не равен null устанавливаем навигацю назад (стрелка слева вверху)
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        collapsingToolbarLayout = findViewById(R.id.collapsing_toolbar);
        collapsingToolbarLayout.setTitle(getString(R.string.exercise_actionbar_header)); // Устанавливаем заголовок toolbar-а

        // устанавливаем стиль текста для разных состояний toolbar-а через ресурс style
        collapsingToolbarLayout.setCollapsedTitleTextAppearance(R.style.AppbarCollaps);
        collapsingToolbarLayout.setExpandedTitleTextAppearance(R.style.AppbarExpand);

        //
        collapsAppBar = findViewById(R.id.collaps_appbar);
        // При сворачивании Appbar (Toolbar) нам необходимо преобразовать FAB, чтобы она не исчезала совсем.
        // Поскольку картинка должна полностью свернуться, то иконку FAB разместим в Toolbar, в самом меню.
        // Так как размещение самой FAB с иконкой, при свернутом Toolbar, будет не совсем логично и эстетично.
        // Здесь применим решение со слушателем. Мы должны слушать (отслеживать) когда AppBarLayout сворачивается
        // и разворачивается.
        // Для этих целей нам необходим OffsetChangedListener, а точнее
        // конструкция AppBarLayout.OnOffsetChangedListener,
        // которая представляет собой определение интерфейса для обратного вызова (callback),
        // вызываемого при изменении вертикального смещения AppBarLayout.

        collapsAppBar.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {

                if (Math.abs(verticalOffset) > 200) { // т.к. verticalOffset всегда < 0
                    appBarExpand = false;
                    invalidateOptionsMenu();

                } else {
                    appBarExpand = true;
                    invalidateOptionsMenu();

                }
            }
        });

        editTextSearch = findViewById(R.id.edit_text_search);
        editTextSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {

                if (editTextSearch.getText().toString().length() > 3) {
                    updateRecyclerView(editTextSearch.getText().toString());
                }
            }
        });

        // Показ рекламы
        mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        if (getResources().getBoolean(R.bool.ad_use)) {
            mAdView.loadAd(adRequest);
        } else
            mAdView.setVisibility(View.GONE);

    }


    private void updateRecyclerView(final String searchText) {
        frameLayout.setVisibility(View.VISIBLE);
        new Handler().post(new Runnable() {
            @Override
            public void run() {

                ExercisesViewModel viewModel = new ViewModelProvider(ExercisesActivity.this).get(ExercisesViewModel.class);
                myItemList = viewModel.getByLikeNameAndProperties(searchText.toUpperCase(), 1000);
                ExercisesAdapter myAdapter = new ExercisesAdapter(myItemList, ExercisesActivity.this);
                recyclerView.setAdapter(myAdapter);

                frameLayout.setVisibility(View.GONE);
                if (searchText.equals("")) { // Если пустой поис (сброс поиска - х), то разворачиваем collapsAppBar
                    collapsAppBar.setExpanded(true, true);
                    editTextSearch.setText("");
                }
            }
        });

    }


    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
// Меню свернуто
// Добавляем наше кастомное динамическое меню
// add("Add") добавляем действие
// setIcon(R.drawable.ic_action_add) устанавливаем иконку из ресурса drawable
// setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM) устанавливаем элемент меню как действие
// с флагом SHOW_AS_ACTION_IF_ROOM, что определяет элемент как кнопку в панели действий,
// если система решит, что для этого есть место.
        if (collapsMenu != null && (!appBarExpand || collapsMenu.size() != 1)) {

            collapsMenu.add("search_clear")
                    .setIcon(R.drawable.ic_close)
                    .setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
            // Убираем заголовки toolbar-ов, отображаем и фокусируем searchEditText
            collapsingToolbarLayout.setTitle("");
            collapsToolbar.setTitle("");
            editTextSearch.setVisibility(View.VISIBLE);
            editTextSearch.requestFocus();
            upFab.setVisibility(View.VISIBLE);
        } else {
            collapsingToolbarLayout.setTitle(getString(R.string.exercise_actionbar_header));
            collapsToolbar.setTitle(getString(R.string.exercise_actionbar_header));
            editTextSearch.setVisibility(View.GONE);
            upFab.setVisibility(View.GONE);


// Меню развернуто
// Не прописано действий, поскольку меню развернуто и действие, определенное через Add,
// должно быть удалено, т.е. его в развернутом меню просто не должно быть,
// после того как onCreateOptionsMenu() наполнит оригинальное меню.
        }

        return super.onPrepareOptionsMenu(collapsMenu);
    }

    @Override
    public void invalidateOptionsMenu() {
        super.invalidateOptionsMenu();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.exercises_menu, menu);

        collapsMenu = menu;
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case android.R.id.home:
                if (!forAdd) {
                    Intent intent = new Intent(ExercisesActivity.this, NavigationActivity.class);
                    startActivity(intent);
                } else finish();
                return true;
            case R.id.action_likes:

                frameLayout.setVisibility(View.VISIBLE);
                new Handler().post(new Runnable() {
                    @Override
                    public void run() {
                        ExercisesViewModel viewModel = new ViewModelProvider(ExercisesActivity.this).get(ExercisesViewModel.class);
                        myItemList = viewModel.getFavorites();
                        ExercisesAdapter myAdapter = new ExercisesAdapter(myItemList, ExercisesActivity.this);
                        recyclerView.setAdapter(myAdapter);

                        frameLayout.setVisibility(View.GONE);
                        collapsAppBar.setExpanded(false, true);
                    }
                });


                return true;
            case R.id.action_info:
                DialogFragment dialogFragment = new ExercisesInfoDialogFragment();
                FragmentManager fragmentManager = (this).getSupportFragmentManager();
                dialogFragment.show(fragmentManager, "exerciseInfoDialog");
                return true;
        }
        if (item.getTitle() == "search_clear") {
            updateRecyclerView("");
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == ExercisesAdapter.RESULT_FAVORITE) {
            boolean isFavorite = data.getBooleanExtra("isFavorite", false);
            int exId = data.getIntExtra("exId", -1);
            int currentPosition = data.getIntExtra("currentPosition", -1);
            if (exId != -1 && currentPosition != -1) {
                if (isFavorite) {
                    myItemList.get(currentPosition).setFavorites(1);
                } else {
                    myItemList.get(currentPosition).setFavorites(0);
                }

                // обновляем значение в БД
                ExercisesViewModel viewModel = new ViewModelProvider(this).get(ExercisesViewModel.class);
                viewModel.updateExercise(myItemList.get(currentPosition));

                // обновляем recyclerView
                recyclerView.getAdapter().notifyDataSetChanged();
            }
        }
    }
}
