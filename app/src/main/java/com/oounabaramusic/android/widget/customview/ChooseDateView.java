package com.oounabaramusic.android.widget.customview;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.oounabaramusic.android.R;
import com.oounabaramusic.android.util.DateUtil;
import com.oounabaramusic.android.util.FormatUtil;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import androidx.annotation.Nullable;

public class ChooseDateView extends LinearLayout {
    private TextView year;
    private TextView month;
    private TextView day;
    private MyHandler mHandler;
    private ScheduledExecutorService ses;

    public ChooseDateView(Context context) {
        super(context);
        LayoutInflater.from(context).inflate(R.layout.choose_date_view,this);
        year = findViewById(R.id.year);
        month = findViewById(R.id.month);
        day = findViewById(R.id.day);
        mHandler=new MyHandler(this);

        init();
    }

    public ChooseDateView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public ChooseDateView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public ChooseDateView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    private void init(){
        View.OnTouchListener touchListener=new View.OnTouchListener(){

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                v.performClick();

                if(event.getAction()==MotionEvent.ACTION_DOWN){
                    startSes(v.getId());
                }else if(event.getAction()==MotionEvent.ACTION_UP){
                    stopSes();
                }
                return true;
            }
        };

        findViewById(R.id.down_year).setOnTouchListener(touchListener);
        findViewById(R.id.down_month).setOnTouchListener(touchListener);
        findViewById(R.id.down_day).setOnTouchListener(touchListener);
        findViewById(R.id.up_year).setOnTouchListener(touchListener);
        findViewById(R.id.up_month).setOnTouchListener(touchListener);
        findViewById(R.id.up_day).setOnTouchListener(touchListener);

        //初始值
        year.setText(String.valueOf(2020));
        month.setText(String.valueOf(1));
        day.setText(String.valueOf(1));
    }

    public Date getDate(){
        Calendar calendar=Calendar.getInstance();
        int y = Integer.valueOf(year.getText().toString());
        int m = Integer.valueOf(month.getText().toString())-1;
        int d = Integer.valueOf(day.getText().toString());
        calendar.set(y,m,d);
        return calendar.getTime();
    }

    public void setCurrentDate(Date date){
        String str= FormatUtil.DateToString(date);
        String[] strings=str.split("-");
        year.setText(strings[0]);
        month.setText(strings[1]);
        day.setText(strings[2]);
    }

    private void startSes(final int id){
        ses= Executors.newSingleThreadScheduledExecutor();
        ses.scheduleWithFixedDelay(new Runnable() {
            @Override
            public void run() {
                mHandler.sendEmptyMessage(id);
            }
        },0,100, TimeUnit.MILLISECONDS);
    }

    private void stopSes(){
        if(ses!=null){
            ses.shutdownNow();
            ses=null;
        }
    }

    static class MyHandler extends Handler{
        ChooseDateView dcv;
        MyHandler(ChooseDateView dcv){
            this.dcv=dcv;
        }

        @Override
        public void handleMessage(Message msg) {
            int y = Integer.valueOf(dcv.year.getText().toString());
            int m = Integer.valueOf(dcv.month.getText().toString());
            int d = Integer.valueOf(dcv.day.getText().toString());

            switch (msg.what){
                case R.id.down_year:
                    if(DateUtil.checkDate(y-1,m,d)){
                        dcv.year.setText(FormatUtil.numberToString(y-1,4));
                    }
                    break;
                case R.id.down_month:
                    if(DateUtil.checkDate(y,m-1,d)){
                        dcv.month.setText(FormatUtil.numberToString(m-1,2));
                    }
                    break;
                case R.id.down_day:
                    if(DateUtil.checkDate(y,m,d-1)){
                        dcv.day.setText(FormatUtil.numberToString(d-1,2));
                    }
                    break;
                case R.id.up_year:
                    if(DateUtil.checkDate(y+1,m,d)){
                        dcv.year.setText(FormatUtil.numberToString(y+1,4));
                    }
                    break;
                case R.id.up_month:
                    if(DateUtil.checkDate(y,m+1,d)){
                        dcv.month.setText(FormatUtil.numberToString(m+1,2));
                    }
                    break;
                case R.id.up_day:
                    if(DateUtil.checkDate(y,m,d+1)){
                        dcv.day.setText(FormatUtil.numberToString(d+1,2));
                    }
                    break;
            }
        }
    }
}
