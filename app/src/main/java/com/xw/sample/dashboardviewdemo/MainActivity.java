package com.xw.sample.dashboardviewdemo;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.SeekBar;
import android.widget.TextView;

import java.util.Locale;
import java.util.Random;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    //    private DashboardView1 mDashboardView1;
//    private DashboardView2 mDashboardView2;
//    private DashboardView3 mDashboardView3;
    private DashboardView4 mDashboardView4;

    private boolean isAnimFinished = true;
    private SeekBar mSeekBar;
    private TextView mTvProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

//        mDashboardView1 = (DashboardView1) findViewById(R.id.dashboard_view_1);
//        mDashboardView2 = (DashboardView2) findViewById(R.id.dashboard_view_2);
//        mDashboardView3 = (DashboardView3) findViewById(R.id.dashboard_view_3);
        mDashboardView4 = (DashboardView4) findViewById(R.id.dashboard_view_4);

//        mDashboardView1.setOnClickListener(this);
//        mDashboardView2.setOnClickListener(this);
//        mDashboardView3.setOnClickListener(this);
        mDashboardView4.setOnClickListener(this);

//        mDashboardView2.setCreditValueWithAnim(new Random().nextInt(600) + 350);

        mSeekBar = ((SeekBar) findViewById(R.id.seek_bar));
        mTvProgress = ((TextView) findViewById(R.id.tv_progress));

        mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                mTvProgress.setText(String.format(Locale.getDefault(), "进度：%d%%", progress));
//                mDashboardView1.setRealTimeValue(progress);
//                mDashboardView2.setCreditValueWithAnim(progress);
//                mDashboardView3.setCreditValue(progress);
                mDashboardView4.setVelocity(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
//            case R.id.dashboard_view_1:
//                mDashboardView1.setRealTimeValue(new Random().nextInt(100));
//
//                break;
//            case R.id.dashboard_view_2:
//                mDashboardView2.setCreditValueWithAnim(new Random().nextInt(950 - 350) + 350);
//
//                break;
//            case R.id.dashboard_view_3:
//                mDashboardView3.setCreditValue(new Random().nextInt(950 - 350) + 350);
//
//                break;
            case R.id.dashboard_view_4:
                if (isAnimFinished) {
                    ObjectAnimator animator = ObjectAnimator.ofInt(mDashboardView4, "velocity",
                            mDashboardView4.getVelocity(), new Random().nextInt(180));
                    animator.setDuration(1500).setInterpolator(new LinearInterpolator());
                    animator.addListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationStart(Animator animation) {
                            isAnimFinished = false;
                        }

                        @Override
                        public void onAnimationEnd(Animator animation) {
                            isAnimFinished = true;
                        }

                        @Override
                        public void onAnimationCancel(Animator animation) {
                            isAnimFinished = true;
                        }
                    });
                    animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                        @Override
                        public void onAnimationUpdate(ValueAnimator animation) {
                            int value = (int) animation.getAnimatedValue();
                            mDashboardView4.setVelocity(value);
                        }
                    });
                    animator.start();
                }

                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
