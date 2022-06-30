package com.builderlinebr.smarttrainer;

import android.content.Intent;
import android.net.Uri;
import android.view.*;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;
import com.builderlinebr.smarttrainer.customviews.BubbleButton;
import com.builderlinebr.smarttrainer.database.UserTable;
import com.builderlinebr.smarttrainer.viewmodel.UserTableViewModel;
import com.google.android.material.navigation.NavigationView;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.List;

public class NavigationActivity extends AppCompatActivity {

    Toolbar toolbar;
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    ImageView editProfileImage;

    Button buttonShadow;

    FrameLayout mainFragment;

    ImageView imageMan;
    ImageView imageGirl;
    TextView textMan;
    TextView textWoman;

    UserTableViewModel viewModel;
    String photoLink;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation);


        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);


        // ------  Заголовок NavigationView ---------------------------
        ImageView avaImage = navigationView.getHeaderView(0).findViewById(R.id.circleImageView);
        TextView userNameTextView = navigationView.getHeaderView(0).findViewById(R.id.user_name_text_view);

        if (!getResources().getBoolean(R.bool.app_share_visible))
            navigationView.getMenu().setGroupVisible(R.id.gr3, false);

        viewModel = new ViewModelProvider(this).get(UserTableViewModel.class);
        List<UserTable> users = viewModel.selectAllUsers();

        if (users != null && users.size() > 0) {
            userNameTextView.setText(users.get(0).getUserName());
            photoLink = users.get(0).getUserPhoto();
            if (photoLink != null && !photoLink.equals("")) {
                if (!getImageFromFile(photoLink, avaImage)) {
                    //avaImage.setVisibility(View.GONE);
                    avaImage.setImageResource(R.drawable.icon_400);
                    //navigationView.getHeaderView(0).findViewById(R.id.button_shadow).setVisibility(View.GONE);
                }
            } else {
                //avaImage.setVisibility(View.GONE);
                avaImage.setImageResource(R.drawable.icon_400);
                //navigationView.getHeaderView(0).findViewById(R.id.button_shadow).setVisibility(View.GONE);
            }
        } else {
            UserTable user = new UserTable();
            user.setUserName("");
            user.setUserPhoto("");
            viewModel.insertUser(user);
        }


        // ----- ToolBar ------------
        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(getString(R.string.exercises_catalog));
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            // Устанавливаем иконку меню
            actionBar.setHomeAsUpIndicator(R.drawable.ic_nav_menu);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

//        mainFragment = findViewById(R.id.fragment_navigation_exercises_id);

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                // item.setChecked(true);

                drawerLayout.closeDrawer(Gravity.LEFT, true);

                int itemId = item.getItemId();

                switch (item.getItemId()) {
                    case R.id.exercises_catalog:
                        toolbar.setTitle(getString(R.string.exercises_catalog));
                        updateFragment(new ExercisesFragment(NavigationActivity.this));
                        break;
                    case R.id.training:
                        // mainFragment.setVisibility(View.GONE);
                        toolbar.setTitle(getString(R.string.training));
                        updateFragment(new WorkoutFragment(NavigationActivity.this));
                        break;
                    case R.id.nutrition_calc:
                        //  mainFragment.setVisibility(View.GONE);
                        toolbar.setTitle(getString(R.string.nutrition_calc));
                        Intent intent = new Intent(NavigationActivity.this, FoodCalcAtivity.class);
                        startActivity(intent);
                        break;
                    case R.id.timer:
                        //   mainFragment.setVisibility(View.GONE);
                        toolbar.setTitle(getString(R.string.timer_text));
                        updateFragment(new TimerFragment(NavigationActivity.this));
                        break;
                    case R.id.geo_location:
                        Intent intentG = new Intent(NavigationActivity.this, MyWorkoutsActivity.class);
                        intentG.putExtra("isMy", 2);
                        startActivity(intentG);
                        break;
//                    case R.id.movie:
//                        Intent intentM = new Intent(NavigationActivity.this, MovieActivity.class);
//                        startActivity(intentM);
//                        break;
                    case R.id.app_share_item:

                        Intent shareIntent = new Intent();
                        shareIntent.setType("text/plain");
                        shareIntent.setAction(Intent.ACTION_SEND);
                        shareIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.app_share_title));
                        shareIntent.putExtra(Intent.EXTRA_TEXT, getString(R.string.app_share_link) + getPackageName());
                        startActivity(Intent.createChooser(shareIntent, getString(R.string.app_share)));


                    default:
                }
                //Toast.makeText(NavigationActivity.this, item.getTitle() , Toast.LENGTH_SHORT).show();
                return true;
            }
        });


        TextView editTextView = navigationView.getHeaderView(0).findViewById(R.id.edit_profile);
        editTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(NavigationActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
        editTextView.setOnTouchListener(new BubbleButton().onTouchListener);


        updateFragment(new ExercisesFragment(this));


    }

    private void updateFragment(Fragment fragment) {

        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.frame_container, fragment).commit();


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case android.R.id.home:
                drawerLayout.openDrawer(GravityCompat.START);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    private boolean getImageFromFile(String imageName, final ImageView imageView) {
        boolean result = true;
        final String path = getExternalFilesDir("").getPath() + "/images/";
        if (!new File(path).exists()) {
            new File(path).mkdir();
        }
        File imgFile = new File(path + imageName);
        if (imgFile.exists()) {

            Uri uri = Uri.fromFile(imgFile);
            Picasso.get().load(uri).into(imageView, new Callback() {
                @Override
                public void onSuccess() {

                }

                @Override
                public void onError(Exception e) {
                }
            });
        } else {
            result = false;
        }
        return result;
    }


}
