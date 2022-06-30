package com.builderlinebr.smarttrainer;

import android.content.Intent;
import android.view.*;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.builderlinebr.smarttrainer.adapters.FoodsAdapter;
import com.builderlinebr.smarttrainer.adapters.FoodsSelectedAdapter;
import com.builderlinebr.smarttrainer.database.Food;
import com.builderlinebr.smarttrainer.viewmodel.FoodViewModel;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class FoodCalcAtivity extends AppCompatActivity implements View.OnClickListener {

    FrameLayout bsPersistent;
    BottomSheetBehavior behavior;

    FloatingActionButton fab;
    Menu menu;
    Toolbar foodsToolbar;
    EditText editTextSearch;
    TextView tableHeaderId;

    RecyclerView recyclerViewFoods;
    public List<Food> foodList;

    public List<Food> selectedFoods;
    RecyclerView selectedFoodsRecyclerView;
    public TextView foodsResult;

    private AdView mAdView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_calc_ativity);

        editTextSearch = findViewById(R.id.foods_search);

        tableHeaderId = findViewById(R.id.table_header_id);

        fab = findViewById(R.id.fab_arrow_up);
        fab.setOnClickListener(this);

        // Созадем ActionBar
        foodsToolbar = findViewById(R.id.foods_toolbar);
        foodsToolbar.setTitle("");
        setSupportActionBar(foodsToolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) { // Если actionBar не равен null устанавливаем навигацю назад (стрелка слева вверху)
            actionBar.setDisplayHomeAsUpEnabled(true);
        }


        bsPersistent = findViewById(R.id.bs_persistent);
        behavior = BottomSheetBehavior.from(bsPersistent);
        if (behavior != null)
            behavior.addBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
                @Override
                public void onStateChanged(@NonNull View bottomSheet, int newState) { // вызывается при смене состояния нижней панели
                    switch (newState) {
                        case BottomSheetBehavior.STATE_COLLAPSED: // свернутое состояние поумолчанию
                            break;
                        case BottomSheetBehavior.STATE_EXPANDED: // развенутое состляние
                            break;
                        case BottomSheetBehavior.STATE_DRAGGING: // промежуточное состояние при перетаскивании панели вниз ил вверх
                            break;
                    }

                    if (behavior.getState() == BottomSheetBehavior.STATE_COLLAPSED) {
                        fab.setEnabled(true);
                    } else {
                        fab.setEnabled(false);
                    }

                }

                // Метод onSlide() вызывается, когда нижняя панель перетаскивается.
                // View bottomSheet - нижняя панель.
                // float slideOffset - новое смещение нижней панели в диапазоне [-1,1].
                // Смещение увеличивается по мере продвижения нижней панели вверх.
                // От 0 до 1 панель находится между свернутым и развернутым состояниями,
                // а от -1 до 0 - между скрытым (STATE_HIDDEN) и свернутым состояниями.
                @Override
                public void onSlide(@NonNull View bottomSheet, float slideOffset) { // когда нижняя панель перетаскивается
                    // Модифицируем FAB.
                    // FAB изменяется при движении нижней панели.
                    fab.animate().scaleX(1 - slideOffset).scaleY(1 - slideOffset).setDuration(0).start();
                    tableHeaderId.animate().scaleX(1 - slideOffset).scaleY(1 - slideOffset).setDuration(0).start();
                    if (slideOffset > 0.8f) {
                        editTextSearch.setVisibility(View.GONE);
                        foodsToolbar.setTitle(getString(R.string.table_header));
                        tableHeaderId.setVisibility(View.GONE);
                        menu.getItem(0).setVisible(false);
                    } else {
                        foodsToolbar.setTitle("");
                        editTextSearch.setVisibility(View.VISIBLE);
                        tableHeaderId.setVisibility(View.VISIBLE);
                        menu.getItem(0).setVisible(true);
                    }


                }
            });


        // Заполняем RecyclerView
        recyclerViewFoods = findViewById(R.id.foods_list);

        FoodViewModel viewModel = new ViewModelProvider(this).get(FoodViewModel.class);
        foodList = viewModel.select(0, 10000);

        recyclerViewFoods.setHasFixedSize(true); // для постоянных списков для лучшей производительности

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerViewFoods.setLayoutManager(linearLayoutManager);

        FoodsAdapter myAdapter = new FoodsAdapter(this, foodList);
        recyclerViewFoods.setAdapter(myAdapter);


        // --------------------------------------------------------------

        selectedFoods = new ArrayList<>();
        selectedFoodsRecyclerView = findViewById(R.id.selected_foods_recyclerview);
        foodsResult = findViewById(R.id.food_result);


        // Показ рекламы
        mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        if (getResources().getBoolean(R.bool.ad_use))
            mAdView.loadAd(adRequest);
        else mAdView.setVisibility(View.GONE);

    }

    @Override
    public void invalidateOptionsMenu() {
        super.invalidateOptionsMenu();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        this.menu = menu;
        this.menu.add("Search")
                .setIcon(R.drawable.ic_search)
                .setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);

        return true;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fab_arrow_up:
                if (behavior.getState() != BottomSheetBehavior.STATE_EXPANDED) {
                    behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                } else {
                    behavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                }
                break;
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getTitle().equals("Search")) {
            FoodViewModel viewModel = new ViewModelProvider(this).get(FoodViewModel.class);
            foodList = viewModel.selectSearchFoods(editTextSearch.getText().toString(), 10000);
            editTextSearch.setVisibility(View.GONE);
            FoodsAdapter myAdapter = new FoodsAdapter(this, foodList);
            recyclerViewFoods.setAdapter(myAdapter);
            item.setIcon(getDrawable(R.drawable.ic_close));
            item.setTitle("Close");
        } else {
            FoodViewModel viewModel = new ViewModelProvider(this).get(FoodViewModel.class);
            foodList = viewModel.select(0, 10000);
            editTextSearch.setText("");
            editTextSearch.setVisibility(View.VISIBLE);
            FoodsAdapter myAdapter = new FoodsAdapter(this, foodList);
            recyclerViewFoods.setAdapter(myAdapter);
            item.setIcon(getDrawable(R.drawable.ic_search));
            item.setTitle("Search");
        }
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent intent = new Intent(this, NavigationActivity.class);
                startActivity(intent);
                break;
        }

        return true;
    }

    public void updateSelectedFoodsList() {

        selectedFoodsRecyclerView.setHasFixedSize(true); // для постоянных списков для лучшей производительности

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        selectedFoodsRecyclerView.setLayoutManager(linearLayoutManager);

        FoodsSelectedAdapter myAdapter = new FoodsSelectedAdapter(this);
        selectedFoodsRecyclerView.setAdapter(myAdapter);
    }


}

