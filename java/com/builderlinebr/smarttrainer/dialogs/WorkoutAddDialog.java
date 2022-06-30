package com.builderlinebr.smarttrainer.dialogs;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Outline;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.*;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import com.builderlinebr.smarttrainer.R;
import com.builderlinebr.smarttrainer.customviews.BubbleButton;
import com.builderlinebr.smarttrainer.database.RealPathUtil;
import com.builderlinebr.smarttrainer.database.Workouts;
import de.hdodenhof.circleimageview.CircleImageView;

import java.io.*;
import java.util.Calendar;

@SuppressLint("ValidFragment")
public class WorkoutAddDialog extends DialogFragment implements View.OnClickListener {

    public static final int PICK_IMAGE = 1;

    Context context;


    public WorkoutAddDialog(Context context) {
        this.context = context;
        workout = null;
    }

    public WorkoutAddDialog(Context context, Workouts workout) {
        this.context = context;
        this.workout = workout;
    }

    public EditText editTextWorkoutName;
    public EditText editTextWorkoutDescription;
    TextView buttonAdd;
    TextView buttonCancel;
    CircleImageView imageViewPhoto;
    public CheckBox forGirlChecBox;

    public boolean result;
    public String photoFileName;
    public Workouts workout;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {


        View view = LayoutInflater.from(requireActivity()).inflate(R.layout.workout_add_dialog, null);

        editTextWorkoutName = view.findViewById(R.id.editText_workout_name);
        editTextWorkoutName.setOutlineProvider(outlineProvider);
        editTextWorkoutDescription = view.findViewById(R.id.editText_workout_description);
        editTextWorkoutDescription.setOutlineProvider(outlineProvider);

        buttonAdd = view.findViewById(R.id.button_workout_add);
        buttonAdd.setOnClickListener(this);
        buttonAdd.setOnTouchListener(new BubbleButton().onTouchListener);
        buttonCancel = view.findViewById(R.id.button_workout_cancel);
        buttonCancel.setOnClickListener(this);
        buttonCancel.setOnTouchListener(new BubbleButton().onTouchListener);

        imageViewPhoto = view.findViewById(R.id.workout_photo);
        imageViewPhoto.setOnClickListener(this);
        imageViewPhoto.setOutlineProvider(outlineProviderOval);

        forGirlChecBox = view.findViewById(R.id.checkBoxForGirl);

        result = false;
        photoFileName = "";


        if (workout != null) {
            editTextWorkoutName.setText(workout.getWorkoutName());
            editTextWorkoutDescription.setText(workout.getWorkoutDescription());
            photoFileName = workout.getWorkoutImage();
            if (!photoFileName.equals("")) {
                String path = context.getExternalFilesDir("").getPath() + "/images/" + photoFileName;
                Bitmap bitmap = BitmapFactory.decodeFile(path);
                imageViewPhoto.setImageBitmap(bitmap);
            }

            forGirlChecBox.setChecked(workout.getForGirl() == 1 ? true : false);

            buttonAdd.setText(getText(R.string.replace));
            ((TextView) view.findViewById(R.id.workout_dialog_title)).setText(getText(R.string.replace_name_workout));
        }


        return new AlertDialog.Builder(requireActivity())
                .setView(view)
                .create();
    }

    ViewOutlineProvider outlineProvider = new ViewOutlineProvider() {
        @Override
        public void getOutline(View view, Outline outline) {
            Rect rect = new Rect(0, 2, view.getWidth(), view.getHeight());
            float dpi = getResources().getDisplayMetrics().densityDpi; // вычисляем плотность пикселей (dpi)
            float px = 32f * (dpi / 160);    // вычислем радиус углов прямоугольника (32 - 32dp как в edit_text_background.xml)
            outline.setRoundRect(rect, px);
        }
    };

    ViewOutlineProvider outlineProviderOval = new ViewOutlineProvider() {
        @Override
        public void getOutline(View view, Outline outline) {
            outline.setOval(0, 0, view.getWidth(), view.getHeight());
        }
    };

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_workout_add:
                result = true;
                getDialog().cancel();
                break;
            case R.id.button_workout_cancel:
                getDialog().cancel();
                break;
            case R.id.workout_photo:
                initCamera();
                break;
        }
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

        if (requestCode == PICK_IMAGE) {
            if (data != null) {
                Uri uri = data.getData();
                if (uri != null) { // Если выбран файл из галереи
                    String realPath = RealPathUtil.getRealPath(context, uri); // // получаем реальный путь к файлу
                    photoFileName = realPath.substring(realPath.lastIndexOf("/") + 1);
                    String path = context.getExternalFilesDir("").getPath() + "/images/" + photoFileName;
                    Bitmap bitmap = BitmapFactory.decodeFile(realPath);
                    writeAndViewImage(bitmap, path);
                } else { // Если сделано фото
                    Bundle extras = data.getExtras();
                    Bitmap mImageBitmap = (Bitmap) extras.get("data");
                    photoFileName = Long.toString(Calendar.getInstance().getTimeInMillis()) + ".jpg";
                    String path = context.getExternalFilesDir("").getPath() + "/images/" + photoFileName;
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
        imageViewPhoto.setImageBitmap(bitmap);
    }
}
