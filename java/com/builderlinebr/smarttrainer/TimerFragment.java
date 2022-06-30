package com.builderlinebr.smarttrainer;

import android.animation.Animator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.view.*;

import android.widget.ListView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import com.builderlinebr.smarttrainer.R;
import com.builderlinebr.smarttrainer.adapters.TimerAdapter;
import com.builderlinebr.smarttrainer.customviews.TimerView;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;


import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.TimeUnit;

@SuppressLint("ValidFragment")
public class TimerFragment extends Fragment implements View.OnClickListener, View.OnTouchListener {

    private Context context;
    private List<String> intervals;
    private ListView timingList;
    private TextView timerText;
    private TextView buttonStart, buttonPeriod, buttonClear, buttonStartDark, buttonPeriodDark, buttonClearDark;
    Thread thread;
    long interval;
    long intervalPre;

    TimerAdapter timerAdapter;

    TimerView timerView;

    private AdView mAdView;

    PowerManager.WakeLock mWakeLock;

    public TimerFragment(Context context) {
        this.context = context;
        intervals = new ArrayList<>();
        interval = 0;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {


        View view = inflater.inflate(R.layout.fragment_navigation_timer, container, false);

        timingList = view.findViewById(R.id.timing_list);


        timerText = view.findViewById(R.id.timer_text_id);
        buttonStart = view.findViewById(R.id.timer_start_button);
        buttonStart.setOnClickListener(this);
        buttonStart.setOnTouchListener(this);
        buttonStartDark = view.findViewById(R.id.timer_start_dark_button);

        buttonPeriod = view.findViewById(R.id.timer_period_button);
        buttonPeriod.setOnClickListener(this);
        buttonPeriod.setOnTouchListener(this);

        buttonClear = view.findViewById(R.id.timer_clear_button);
        buttonClear.setOnClickListener(this);
        buttonClear.setOnTouchListener(this);

        timerView = view.findViewById(R.id.timer_view);
        timerView.setSeconds(0);
        timerView.setMins(0);


        // Показ рекламы
        mAdView = view.findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        if (getResources().getBoolean(R.bool.ad_use))
            mAdView.loadAd(adRequest);
        else mAdView.setVisibility(View.GONE);

        return view;


    }


//    private void initCircleTimer(View view){
//        surface = view.findViewById(R.id.surfaceTimer);
//
//        surface.getHolder().addCallback(new SurfaceHolder.Callback() {
//            @Override
//            public void surfaceCreated(SurfaceHolder holder) {
//
//                Canvas canvas = holder.lockCanvas();
//                canvas.drawColor(Color.WHITE);
//
//                Paint paint = new Paint();
//                paint.setColor(Color.BLACK);
//                paint.setStrokeWidth(5f);
//                paint.setAntiAlias(true);
//
//
//
//                holder.unlockCanvasAndPost(canvas);
//
//            }
//
//            @Override
//            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
//
//            }
//
//            @Override
//            public void surfaceDestroyed(SurfaceHolder holder) {
//
//            }
//        });
//
//
//
//    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        if (this.mWakeLock != null)
            this.mWakeLock.acquire();
    }

    @SuppressLint("InvalidWakeLockTag")
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.timer_start_button:
                if (((TextView) v).getText().equals(getString(R.string.timer_start))) {
                    ((TextView) v).setText(getString(R.string.timer_stop));
                    buttonStartDark.setText(getString(R.string.timer_stop));

                    if (getResources().getBoolean(R.bool.ad_use))
                        mAdView.pause();
                    mAdView.setVisibility(View.GONE);

                    thread = new Thread(runnable);
                    thread.start();

                    // Включение запрета перехода в спящий режим
                    PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
                    this.mWakeLock = pm.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK, "MyTag");
                    this.mWakeLock.acquire();

                } else {
                    ((TextView) v).setText(getString(R.string.timer_start));
                    buttonStartDark.setText(getString(R.string.timer_start));
                    thread.interrupt();
                    this.mWakeLock.release(); // Отключение запрета перехода в спящий режим

                    mAdView.setVisibility(View.VISIBLE);
                    if (getResources().getBoolean(R.bool.ad_use))
                        mAdView.loadAd(new AdRequest.Builder().build());
                    else mAdView.setVisibility(View.GONE);

                }
                break;
            case R.id.timer_period_button:
                intervals.add(timerText.getText().toString());
                timerAdapter = new TimerAdapter(intervals, context, getLayoutInflater());
                timingList.setAdapter(timerAdapter);

                break;
            case R.id.timer_clear_button:
                intervals = new ArrayList<>();
                timerText.setText(getText(R.string.timer_null));
                timerAdapter = new TimerAdapter(intervals, context, getLayoutInflater());
                timingList.setAdapter(timerAdapter);
                interval = 0;
                intervalPre = 0;

                // Для прорисовки стрелок
                timerView.setSeconds(0);
                timerView.setMins(0);

                break;
        }
    }

    Runnable runnable = new Runnable() {
        @Override
        public void run() {

            long milisec = Calendar.getInstance().getTimeInMillis();

            intervalPre = interval;
            while (true) {
                try {

                    if (interval == 0) milisec = Calendar.getInstance().getTimeInMillis();

                    interval = intervalPre + (Calendar.getInstance().getTimeInMillis() - milisec);

                    long hours = TimeUnit.MILLISECONDS.toHours(interval) - (TimeUnit.MILLISECONDS.toHours(interval) / 24) * 24;
                    final long mins = TimeUnit.MILLISECONDS.toMinutes(interval) - (TimeUnit.MILLISECONDS.toMinutes(interval) / 60) * 60;
                    final long sec = TimeUnit.MILLISECONDS.toSeconds(interval) - (TimeUnit.MILLISECONDS.toSeconds(interval) / 60) * 60;
                    final long milis = TimeUnit.MILLISECONDS.toMillis(interval) - (TimeUnit.MILLISECONDS.toMillis(interval) / 1000) * 1000;


                    String h = Long.toString(hours);
                    if (hours < 10) h = "0" + h;
                    String m = Long.toString(mins);
                    if (mins < 10) m = "0" + m;
                    String s = Long.toString(sec);
                    if (sec < 10) s = "0" + s;
                    String mi = Long.toString(milis);
                    if (milis < 10) mi = "00" + mi;
                    else if (milis >= 10 & milis < 100) mi = "0" + mi;


                    final StringBuilder stringBuilder = new StringBuilder();
                    stringBuilder.append(h).append(":").append(m).append(":").append(s).append(":").append(mi);
                    ((NavigationActivity) context).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            timerText.setText(stringBuilder.toString());

                            // Для прорисовки стрелок
                            timerView.setSeconds(sec + (float) milis / 1000f);
                            double mins_ = mins < 30 ? mins : mins - 30 * (int) (mins / 30);
                            mins_ += (float) sec / 60;
                            timerView.setMins(mins_);
                        }
                    });


                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    break;
                }
            }
        }
    };


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP) // Только для андрой 5 и выше
    public void show(View view, int cx, int cy) { // метод анимации расширения круга
        int maxRadius = view.getHeight();
        Animator animator = ViewAnimationUtils.createCircularReveal(view, cx, cy, 0, maxRadius);
        animator.setDuration(300);
        animator.start();

    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN: // если нажата на view
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) { // Проверка версии андройд >= 5
                    show(v, (int) event.getX(), (int) event.getY());
                }
                break;
            case MotionEvent.ACTION_UP: // если отжата view
                v.setVisibility(View.VISIBLE);
        }

        return false;
    }
}
