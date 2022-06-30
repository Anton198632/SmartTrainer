package com.builderlinebr.smarttrainer;

import android.hardware.Camera;
import android.media.MediaRecorder;
import android.media.session.MediaController;
import android.view.*;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;
//import com.arthenica.mobileffmpeg.FFmpeg;


import java.util.*;

//    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED
//        || ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
//        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO, Manifest.permission.CAMERA}, 0);
//    }
//

public class MovieActivity extends AppCompatActivity  { //implements View.OnClickListener

    public final int REC_SECONDS = 3;


    private MediaController mc = null;
    private SurfaceHolder surfaceHolder;
    private SurfaceView surfaceView;
    public MediaRecorder mediaRecorder = new MediaRecorder();
    private Camera mCamera;
    public TextView btnStart;
    LinearLayout waitLayout;
    public Toolbar toolbar;
    private TextView rec0;
    private TextView rec1;
    private TextView counterText;
    private RecyclerView recyclerView;
    public List<String> items;
    public VideoView videoView;
    ConstraintLayout progressBarLayout;
    ProgressBar progressBar;
    TextView percentText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie);

//        // проверяем разрешения на запись с камеры
//        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED
//                || ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
//            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO, Manifest.permission.CAMERA}, 0);
//        } else {
//            init();
//        }

    }

//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//        // проверяем разрешения на запись с камеры
//        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED
//                || ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
//            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO, Manifest.permission.CAMERA}, 0);
//        } else {
//            init();
//        }
//    }
//
//
//    public void init() {
//
//        // -------- PorgressBarLayout ----------
//        progressBarLayout = findViewById(R.id.progressBarLayout);
//        progressBarLayout.setVisibility(View.GONE);
//        progressBar = findViewById(R.id.progressBar);
//        percentText = findViewById(R.id.percent_text);
//
//
//        // -------- VideoView -------------
//        videoView = findViewById(R.id.videoView);
//        videoView.setVisibility(View.GONE);
//
//        // -------- RecyclerView ----------
//        recyclerViewInit();
//
//
//        // ------ Устанавливае ActionBar ------------------------
//        toolbar = findViewById(R.id.toolbar);
//        toolbar.setTitle("");
//        TextView textView = toolbar.findViewById(R.id.toolbar_title_text_view);
//        textView.setText(getString(R.string.rec_header));
//
//
//        setSupportActionBar(toolbar);
//        ActionBar actionBar = getSupportActionBar();
//        if (actionBar != null) { // Если actionBar не равен null устанавливаем навигацю назад (стрелка слева вверху)
//            actionBar.setDisplayHomeAsUpEnabled(true);
//        }
//        // ----------------------------------------------------
//
//        //----- Индикатор записи --------------
//        rec0 = findViewById(R.id.rec0);
//        rec1 = findViewById(R.id.rec1);
//        rec0.setVisibility(View.GONE);
//        rec1.setVisibility(View.GONE);
//        counterText = findViewById(R.id.counter_text);
//        counterText.setVisibility(View.GONE);
//        //-------------------------------------
//
//
//        waitLayout = findViewById(R.id.wait_layout);
//        waitLayout.setVisibility(View.VISIBLE);
//
//        surfaceView = (SurfaceView) findViewById(R.id.camera_view);
//        mCamera = Camera.open();
//        mCamera.setDisplayOrientation(90);
//
//        surfaceHolder = surfaceView.getHolder();
//        surfaceHolder.addCallback(new SurfaceHolder.Callback() {
//            @Override
//            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
//
//            }
//
//            @Override
//            public void surfaceCreated(SurfaceHolder holder) {
//                if (mCamera != null) {
//                    Camera.Parameters params = mCamera.getParameters();
//                    params.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO);
//                    //params.setPreviewFrameRate(30);
//                    //params.setPreviewSize(176, 144);
//                    mCamera.setParameters(params);
//                    mCamera.setDisplayOrientation(90);
//                    Log.i("Surface", "Created");
//                } else {
//                    Toast.makeText(getApplicationContext(), "Камера не обнаружена", Toast.LENGTH_LONG).show();
//                    finish();
//                }
//            }
//
//            @Override
//            public void surfaceDestroyed(SurfaceHolder holder) {
//                // mCamera.stopPreview();
//                // mCamera.release();
//            }
//        });
//        surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
//
//
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                try {
//                    try {
//                        Thread.sleep(5000);
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
//
//                    runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
//                            waitLayout.setVisibility(View.GONE);
//                        }
//                    });
//
//                    mCamera.setPreviewDisplay(surfaceHolder);
//                    mCamera.startPreview();
//                    mCamera.lock();
//                    mCamera.unlock();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//        }).start();
//
//
//        btnStart = (TextView) findViewById(R.id.btn_start);
//        btnStart.setOnClickListener(this);
//
//    }
//
//
//    protected void startRecording() throws IOException {
//        if (mCamera == null)
//            mCamera = Camera.open();
//        final String path = getExternalFilesDir("").getPath() + "/videos/";
//        File dir = new File(path);
//        if (!dir.exists()) {
//            dir.mkdirs();
//        }
//
//        Date date = new Date();
//        final String fileAlias = "/rec" + date.toString().replace(" ", "_").replace(":", "_");
//        final String fileName = fileAlias + ".mp4";
//        File file = new File(dir, fileName);
//
//
//        mediaRecorder = new MediaRecorder();
//
//        mCamera.lock();
//        mCamera.unlock();
//
//        mediaRecorder.setCamera(mCamera);
//        mediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);
//        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.CAMCORDER);
//        mediaRecorder.setPreviewDisplay(surfaceHolder.getSurface());
//
//        CamcorderProfile camcorderProfile = CamcorderProfile.get(CamcorderProfile.QUALITY_HIGH);
//        mediaRecorder.setProfile(camcorderProfile);
//
//        mediaRecorder.setOutputFile(dir + fileName);
//        mediaRecorder.setOrientationHint(90);
//        mediaRecorder.prepare();
//
//        mediaRecorder.setOutputFile(dir + fileName);
//        mediaRecorder.start();
//
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//
//                runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        rec1.setVisibility(View.VISIBLE);
//                        rec0.setVisibility(View.VISIBLE);
//                        counterText.setVisibility(View.GONE);
//                        //анимация альфа канала (прозрачности от 0 до 1)
//                        Animation animation = new AlphaAnimation(0.0f, 1.0f);
//                        //длительность анимации 1/10 секунды
//                        animation.setDuration(500);
//                        //сдвижка начала анимации (с середины)
//                        animation.setStartOffset(50);
//                        //режим повтора - сначала или в обратном порядке
//                        animation.setRepeatMode(Animation.REVERSE);
//                        //режим повтора (бесконечно)
//                        animation.setRepeatCount(REC_SECONDS * 2);
//                        //накладываем анимацию на TextView
//                        rec0.startAnimation(animation);
//
//                    }
//                });
//                try {
//                    Thread.sleep(REC_SECONDS * 1000);
//                    mediaRecorder.stop();
//                    mediaRecorder.release();
//                    mediaRecorder = null;
//                    // создаем предпросмотр
//                    if (!new File(path + "thumbnails/").exists())
//                        new File(path + "thumbnails/").mkdir();
//                    Bitmap thumb = ThumbnailUtils.createVideoThumbnail(path + fileName,
//                            MediaStore.Images.Thumbnails.MINI_KIND);
//                    try {
//                        OutputStream os = new FileOutputStream(path + "thumbnails/t" + fileAlias.substring(1) + ".jpg");
//                        thumb.compress(Bitmap.CompressFormat.JPEG, 100, os);
//                        os.flush();
//                        os.close();
//
//                        runOnUiThread(new Runnable() {
//                            @Override
//                            public void run() {
//                                items.add(path + "thumbnails/t" + fileAlias.substring(1) + ".jpg");
//                                recyclerView.setVisibility(View.VISIBLE);
//                                recyclerView.getAdapter().notifyDataSetChanged();
//                                recyclerView.scrollToPosition(items.size() - 1);
//                            }
//                        });
//
//                    } catch (FileNotFoundException e) {
//                        e.printStackTrace();
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//
//
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//                runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        btnStart.setVisibility(View.VISIBLE);
//                        toolbar.setVisibility(View.VISIBLE);
//
//                        rec1.setVisibility(View.GONE);
//                        rec0.setVisibility(View.GONE);
//                        Toast.makeText(MovieActivity.this, "Запись завершена", Toast.LENGTH_SHORT).show();
//                    }
//                });
//            }
//        }).start();
//
//    }
//
//
//    @Override
//    public void onClick(View v) {
//        switch (v.getId()) {
//            case R.id.btn_start:
//                if (btnStart.getText().toString().equalsIgnoreCase(getString(R.string.rec_start))) {
//                    btnStart.setVisibility(View.GONE);
//                    counterText.setVisibility(View.VISIBLE);
//                    toolbar.setVisibility(View.GONE);
//                    recyclerView.setVisibility(View.GONE);
//                    new Thread(new Runnable() {
//                        @Override
//                        public void run() {
//                            for (int i = 3; i > 0; i--) {
//                                try {
//                                    Thread.sleep(1000);
//                                } catch (InterruptedException e) {
//                                    e.printStackTrace();
//                                }
//                                final int finalI = i;
//                                runOnUiThread(new Runnable() {
//                                    @Override
//                                    public void run() {
//                                        counterText.setText(Integer.toString(finalI));
//                                    }
//                                });
//                            }
//
//                            try {
//                                startRecording();
//                            } catch (IOException e) {
//                                String message = e.getMessage();
//                                Log.i(null, "Problem " + message);
//                                mediaRecorder.release();
//                                e.printStackTrace();
//                            }
//                        }
//                    }).start();
//
//
//                } else {
//                    btnStart.setText("Start");
//                    mediaRecorder.stop();
//                    mediaRecorder.release();
//                    mediaRecorder = null;
//                }
//                break;
//            default:
//                break;
//        }
//    }
//
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.movie_menu, menu);
//        return super.onCreateOptionsMenu(menu);
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
//
//        switch (item.getItemId()) {
//            case android.R.id.home:
//                Intent intent = new Intent(this, NavigationActivity.class);
//                startActivity(intent);
//                break;
//
//            case R.id.item_share:
//                AlertDialog.Builder dBuilder = new AlertDialog.Builder(MovieActivity.this, R.style.DialogWorkout);
//                dBuilder.setCancelable(true) // можно ли отмень диалог нажатием на кнопку <|
//                        .setMessage(getString(R.string.video_concat_message))
//                        .setNegativeButton(getString(R.string.vcm_cancel), new DialogInterface.OnClickListener() { // обработчик кнопки No
//                            @Override
//                            public void onClick(DialogInterface dialog, int which) {
//                            }
//                        })
//                        .setPositiveButton(R.string.vcm_ok, new DialogInterface.OnClickListener() { // обработчик кнопки Yes
//                            @Override
//                            public void onClick(DialogInterface dialog, int which) {
//                                concatVideo();
//                            }
//                        });
//
//                AlertDialog dialog = dBuilder.create();
//                dialog.show();
//                break;
//
//            case R.id.item_movies:
//                videoView.setVisibility(View.GONE);
//                btnStart.setVisibility(View.VISIBLE);
//                break;
//        }
//
//        return true;
//    }
//
//
//    private void concatVideo() {
//        final File[] files = new File(getExternalFilesDir("").getPath() + "/videos/").listFiles();
//        progressBarLayout.setVisibility(View.VISIBLE);
//        percentText.setText(0 + " %");
//        btnStart.setVisibility(View.GONE);
//        progressBar.setProgress(0);
//
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//
//
//                final List<File> fileList = new ArrayList<>();
//                for (File file : files) {
//                    fileList.add(file);
//                }
//                Collections.sort(fileList, new Comparator<File>() {
//                    @Override
//                    public int compare(File o1, File o2) {
//                        return (int) (o1.lastModified() - o2.lastModified());
//                    }
//                });
//
//                deleteSubFolders(getExternalFilesDir("").getPath() + "/videos/result");
//
//
//                if (!new File(getExternalFilesDir("").getPath() + "/videos/result/").exists())
//                    new File(getExternalFilesDir("").getPath() + "/videos/result/").mkdir();
//
//                String savingPath = getExternalFilesDir("").getPath() + "/videos/result/result.mp4";
//
//                // Преобразовываем из mp4 в mpeg-2 для дальнейшего склеивания
//                String cmd = "";
//                StringBuilder sb = new StringBuilder();
//                List<String> forDelete = new ArrayList<>();
//
//
//                for (int i = 0; i < fileList.size(); i++) {
//
//                    final int finalI = i;
//                    runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
//                            int progress = finalI * 100 / (fileList.size() - 1);
//                            if (progress < 101) {
//                                progressBar.setProgress(progress);
//                                percentText.setText(Integer.toString((int) progress) + " %");
//                            }
//                        }
//                    });
//
//                    if (fileList.get(i).isDirectory()) continue;
//                    String buffer0 = getExternalFilesDir("").getPath() + "/videos/result/buffer" + i + ".ts";
//                    String buffer1 = getExternalFilesDir("").getPath() + "/videos/result/buffer_" + i + ".ts";
//                    String buffer2 = getExternalFilesDir("").getPath() + "/videos/result/buffer__" + i + ".ts";
//                    cmd = "-i " + fileList.get(i).getAbsolutePath() + " -c copy -bsf:v h264_mp4toannexb -f mpegts " + buffer0;
//                    FFmpeg.execute(cmd);
//
//                    // поворачиваем изображение, т.к. оно почемуто перевернуто
//                    cmd = "-i " + buffer0 + " -filter_complex  transpose=1 " + buffer1;
//                    FFmpeg.execute(cmd);
//
//                    String fileFont1 = getExternalFilesDir("").getPath() + "/fonts/montserrat_medium.ttf";
//                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy HH.mm.ss");
//                    Date dt = new Date(fileList.get(i).lastModified());
//                    String dtS = simpleDateFormat.format(dt);
//                    cmd =
//                            "-i " + buffer1 + " -vf "
//                                    + "\"drawbox=x=0:y=0:w=iw:h=67.5:color=blue@0.4:t=h, "
//                                    + "drawtext=fontsize=42.66667:fontfile=" + fileFont1 + ":fontcolor=white:text='" + dtS + "':x=(w-tw)/2:y=10:, "
//                                    + "drawtext=fontsize=42.66667:fontfile=" + fileFont1 + ":fontcolor=blue:text='App «Smart Trainer»':x=0.5*w:y=0.9623188*h:\" " +
//                                    "-preset veryslow -crf 0 -acodec copy " + buffer2;
//                    FFmpeg.execute(cmd);
//
//                    sb.append(buffer2);
//                    if (i != fileList.size() - 1)
//                        sb.append("|");
//
//                    forDelete.add(buffer0);
//                    forDelete.add(buffer1);
//                    forDelete.add(buffer2);
//                }
//
//                // объединяем в один файл и сохраняем в mp4
//                cmd = "-i \"concat:" + sb.toString() + "\" -c copy " + savingPath; // -bsf:a aac_adtstoasc
//                FFmpeg.execute(cmd);
//
//                for (String buffer : forDelete) {
//                    new File(buffer).delete();
//                }
//
//                runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        progressBarLayout.setVisibility(View.GONE);
//                        btnStart.setVisibility(View.VISIBLE);
//                    }
//                });
//
//
//                Intent shareIntent = new Intent();
//                shareIntent.setAction(Intent.ACTION_SEND);
//                // файл для передачи в другие приложения
//                File f = new File(getExternalFilesDir("").getPath() + "/videos/result/result.mp4");
//
//
//                    shareIntent.putExtra(Intent.EXTRA_STREAM,
//                            FileProvider.getUriForFile(MovieActivity.this, BuildConfig.APPLICATION_ID + ".provider", f));
//
//                shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
//                shareIntent.setType("video/mp4");
//                startActivity(Intent.createChooser(shareIntent, "Поделиться видео"));
//
//
//            }
//        }).start();
//    }
//
//
//    private void recyclerViewInit() {
//
//        recyclerView = findViewById(R.id.movieRecyclerView);
//        LinearLayoutManager layoutManager = new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false);
//        recyclerView.setLayoutManager(layoutManager);
//
//        String path = getExternalFilesDir("").getPath() + "/videos/";
//        if (!new File(path).exists())
//            new File(path).mkdir();
//        path = getExternalFilesDir("").getPath() + "/videos/thumbnails/";
//        if (!new File(path).exists())
//            new File(path).mkdir();
//
//        File[] files = new File(path).listFiles();
//        List<File> fileList = new ArrayList<>();
//        for (File file : files) {
//            fileList.add(file);
//        }
//        Collections.sort(fileList, new Comparator<File>() {
//            @Override
//            public int compare(File o1, File o2) {
//                return (int) (o1.lastModified() - o2.lastModified());
//            }
//        });
//
//        items = new ArrayList<>();
//        for (File file : fileList) {
//            if (!file.isDirectory())
//                items.add(file.getAbsolutePath());
//        }
//
//        MovieAdapter movieAdapter = new MovieAdapter(items, this);
//        recyclerView.setAdapter(movieAdapter);
//        if (items.size() > 0)
//            recyclerView.scrollToPosition(items.size() - 1);
//
//    }
//
//    private void deleteSubFolders(String uri) {
//        File currentFolder = new File(uri);
//        File files[] = currentFolder.listFiles();
//
//        if (files == null) {
//            return;
//        }
//        for (File f : files) {
//            if (f.isDirectory()) {
//                deleteSubFolders(f.toString());
//            }
//            //no else, or you'll never get rid of this folder!
//            f.delete();
//        }
//    }
}
