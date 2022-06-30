package com.builderlinebr.smarttrainer;

import android.content.Intent;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

import java.io.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class UnzipActivity extends AppCompatActivity {

    ProgressBar progressBar;
    TextView percentText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_unzip);


        progressBar = findViewById(R.id.progressBar);
        percentText = findViewById(R.id.percent_text);

        // Распаковываем шрифты
        String pathFonts = getExternalFilesDir("").getPath() + "/fonts/";
        if (!new File(pathFonts).exists()) {
            new File(pathFonts).mkdir(); // создаем директорию
            String fileFont1 = getExternalFilesDir("").getPath() + "/fonts/montserrat_medium.ttf";
            fromRawToFile(R.raw.montserrat_medium, fileFont1);
        }

        // проверяем есть ли уже распакованные файлы
        String path = getExternalFilesDir("").getPath() + "/images/";
        final String zipFile = getExternalFilesDir("").getPath() + "/images.zip";
        if (!new File(path).exists()) {
            new File(path).mkdir(); // создаем директорию
            InputStream inputStream = getResources().openRawResource(R.raw.images);
            try {
                OutputStream outputStream = new FileOutputStream(zipFile);
                byte[] buffer = new byte[4 * 1024];
                int read;
                while ((read = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, read);
                }
                outputStream.flush();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            // Распаковывем архив в потоке, для индикации процесса
            new Thread(new Runnable() {
                @Override
                public void run() {
                    unzip(new File(zipFile), new File(getExternalFilesDir("").getPath() + "/"));
                }
            }).start();

        } else {
            Intent intent = new Intent(UnzipActivity.this, MainActivity.class);
            startActivity(intent);
        }

    }

    private void fromRawToFile(int id, String filePath){
        InputStream inputStream = getResources().openRawResource(id);
        try {
            OutputStream outputStream = new FileOutputStream(filePath);
            byte[] buffer = new byte[4 * 1024];
            int read;
            while ((read = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, read);
            }
            outputStream.flush();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void unzip(File zipFile, File targetDirectory) {
        try (FileInputStream fis = new FileInputStream(zipFile)) {
            long zipSize = zipFile.length();
            try (BufferedInputStream bis = new BufferedInputStream(fis)) {

                try (ZipInputStream zis = new ZipInputStream(bis)) {

                    ZipEntry ze;
                    int count;
                    byte[] buffer = new byte[4096];
                    int i = 0;
                    int bufferCounter = 0;

                    double bufferStep =1.5 * zipSize / (100 * 1.0598); // 1.4645 - коэффициент сжатия (отношение размера вложения к размеру архива)
                    while ((ze = zis.getNextEntry()) != null) {

                        bufferCounter += buffer.length;
                        if (bufferCounter >= bufferStep) {
                            bufferCounter =(int)(bufferStep - bufferCounter);
                            i++;
                            final double finalI = i;
                            if (i < 101) {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        percentText.setText(Integer.toString((int) finalI) + " %");
                                        progressBar.setProgress((int) finalI);
                                    }
                                });
                            }
                        }


                        File file = new File(targetDirectory, ze.getName());
                        File dir = ze.isDirectory() ? file : file.getParentFile();
                        if (!dir.isDirectory() && !dir.mkdirs())
                            throw new FileNotFoundException("Failed to ensure directory: " + dir.getAbsolutePath());
                        if (ze.isDirectory())
                            continue;
                        try (FileOutputStream fout = new FileOutputStream(file)) {
                            while ((count = zis.read(buffer)) != -1) {
                                fout.write(buffer, 0, count);

                            }
                        }
                    }

                    Intent intent = new Intent(UnzipActivity.this, MainActivity.class);
                    startActivity(intent);

                }
            }
        } catch (Exception ex) {
        }
    }




}
