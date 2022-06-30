package com.builderlinebr.smarttrainer;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Outline;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.view.*;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.core.app.ActivityCompat;
import androidx.lifecycle.ViewModelProvider;
import com.builderlinebr.smarttrainer.customviews.BubbleButton;
import com.builderlinebr.smarttrainer.database.RealPathUtil;
import com.builderlinebr.smarttrainer.database.UserTable;
import com.builderlinebr.smarttrainer.viewmodel.UserTableViewModel;
import com.google.android.gms.ads.MobileAds;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import de.hdodenhof.circleimageview.CircleImageView;

import java.io.*;
import java.util.Calendar;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    CircleImageView imageView;
    EditText editText;

    UserTableViewModel viewModel;
    String photoLink;

    TextView nextButton;

    public static final int PICK_IMAGE = 1;


    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        verifyStoragePermissions(MainActivity.this);

        imageView = findViewById(R.id.circleImageView);
        imageView.setOutlineProvider(new ViewOutlineProvider() {
            @Override
            public void getOutline(View view, Outline outline) {
                outline.setOval(0, 2, view.getWidth(), view.getHeight());
            }
        });
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initCamera();
            }
        });

        editText = findViewById(R.id.editText_name);
        editText.setOutlineProvider(new ViewOutlineProvider() {
            @Override
            public void getOutline(View view, Outline outline) {
                Rect rect = new Rect(0, 2, view.getWidth(), view.getHeight());
                float dpi = getResources().getDisplayMetrics().densityDpi; // вычисляем плотность пикселей (dpi)
                float px = 32f * (dpi / 160);    // вычислем радиус углов прямоугольника (32 - 32dp как в edit_text_background.xml)
                outline.setRoundRect(rect, px);
            }
        });


        viewModel = new ViewModelProvider(this).get(UserTableViewModel.class);
        List<UserTable> users = viewModel.selectAllUsers();

        if (users != null && users.size() > 0) {
            editText.setText(users.get(0).getUserName());
            photoLink = users.get(0).getUserPhoto();
            if (photoLink != null && !photoLink.equals(""))
            getImageFromFile(photoLink, imageView);
        } else {
            UserTable user = new UserTable();
            user.setUserName("");
            user.setUserPhoto("");
            viewModel.insertUser(user);
        }

        nextButton = findViewById(R.id.next_button);
        nextButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()){
                    case MotionEvent.ACTION_DOWN: // если нажата на view
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) { // Проверка версии андройд >= 5
                            new BubbleButton().show(v, (int) event.getX(), (int) event.getY(), 250);
                        }
                        break;
                    case MotionEvent.ACTION_UP: // если отжата view
                        v.setVisibility(View.VISIBLE);
                }

                if (!editText.getText().toString().equals("")) {

                    UserTable user = new UserTable();
                    user.setUserName(editText.getText().toString());
                    user.setUserPhoto(photoLink);
                    user.setPk(1);
                    viewModel.updateUser(user);

                    Intent intent = new Intent(MainActivity.this, NavigationActivity.class);
                    startActivity(intent);
                } else {
                    Toast.makeText(MainActivity.this, getResources().getText(R.string.empty_name), Toast.LENGTH_SHORT).show();
                }

                return false;
            }
        });




        // Инициализация рекламы
        MobileAds.initialize(this  );

    }

    // Для андрой sdk23+ необходимо дополнительно спросить разрешение к записи чтению файлов
    public static void verifyStoragePermissions(Activity activity) {
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (permission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }
    }


    public void onClick(View view) {




    }



    private void initCamera() {

        //
        Intent getIntent = new Intent(Intent.ACTION_GET_CONTENT);
        getIntent.setType("image/*");

        // Намерение открытия галереи
        Intent galeryIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        galeryIntent.setType("image/*");

        // Намерение открытия фотокамеры
        Intent photoIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        // Вызываем намерение выбора
        Intent chooserIntent = Intent.createChooser(getIntent, "Выберите изображение");
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[]{galeryIntent, photoIntent});
        startActivityForResult(chooserIntent, PICK_IMAGE);
    }

    // Метод, срабатывающий после выбора изображения или фотоснимка
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE) {
            if (data != null) {
                Uri uri = data.getData();
                if (uri != null) { // Если выбран файл из галереи
                    String realPath = RealPathUtil.getRealPath(this, uri); // // получаем реальный путь к файлу
                    photoLink = realPath.substring(realPath.lastIndexOf("/") + 1);
                    String path = getExternalFilesDir("").getPath() + "/images/" + photoLink;
                    Bitmap bitmap = BitmapFactory.decodeFile(realPath);
                    writeAndViewImage(bitmap, path);
                } else { // Если сделано фото
                    Bundle extras = data.getExtras();
                    Bitmap mImageBitmap = (Bitmap) extras.get("data");
                    photoLink = Long.toString(Calendar.getInstance().getTimeInMillis()) + ".jpg";
                    String path = getExternalFilesDir("").getPath() + "/images/" + photoLink;
                    writeAndViewImage(mImageBitmap, path);
                }
            }
        }
    }



    // мзменяем размер изображения
    private Bitmap resizeBitmap(Bitmap inBitmap, int width) {
        int w = inBitmap.getWidth();
        int h = inBitmap.getHeight();
        float wh = (float) w / (float) h;
        int height = Math.round(width / wh);
        Bitmap result = Bitmap.createScaledBitmap(inBitmap, width, height, false);
        return result;
    }

    private void writeAndViewImage(Bitmap bitmap, String path) {
        bitmap = resizeBitmap(bitmap, 500);
        try (FileOutputStream out = new FileOutputStream(path)) {
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
        } catch (IOException e) {
            e.printStackTrace();
        }
        imageView.setImageBitmap(bitmap);
    }




    private void getImageFromFile(String imageName, final ImageView imageView) {
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
        }
    }
}
