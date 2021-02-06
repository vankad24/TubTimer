package com.application.tubtimer.database;

import android.os.CountDownTimer;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.google.gson.Gson;

@Entity
public class Timer{
    public static final int TUBE_FREE=0;
    public static final int TUBE_ON_TRACK=1;
    public static final int TUBE_IN_REPAIR=2;

    @Ignore
    CountDownTimer downTimer;
    @Ignore
    TickListener listener;
    @PrimaryKey
    public int number;     //numberOfTube
    int duration;     // in seconds
    int time_left;
    int type;
    public boolean activated;



    public void setOnTickListener(TickListener listener) {
        this.listener = listener;
    }

    public Timer(int number, int duration, int time_left, int type, boolean activated) {
        this.number = number;
        this.duration = duration;
        this.time_left = time_left;
        this.type = type;
        this.activated = activated;
    }


    @Ignore
    public Timer(int number, int seconds) {
        duration = seconds;
        time_left = seconds;
        this.number = number;
        activated= false;
        type = TUBE_ON_TRACK;
    }


    public void start(){
        activated = true;
        downTimer = new CountDownTimer(time_left*1000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                time_left = (int) (millisUntilFinished/1000);
                listener.onTick(time_left);
            }

            @Override
            public void onFinish() {
                time_left = duration;
                activated = false;
                listener.onFinish();
            }
        }.start();
    }

    public void stop(){
        time_left = duration;
        activated = false;
        downTimer.cancel();
    }

    public void pause(){
        activated = false;
        downTimer.cancel();
    }

    public String getStringData(){
       /* Gson gson = new GsonBuilder()
                .excludeFieldsWithoutExposeAnnotation().create();*/
        Gson gson = new Gson();
        return gson.toJson(this, Timer.class);
    }

    public String getTimeString(){
        String time="";
        int t = (time_left / 3600)%24;
        if (t!=0)time+=t +"ч ";
        t =(time_left / 60) % 60;
        if (t!=0)time+=t+"мин ";
        /*t =(time_left % 60);
        if (t!=0)time+=t+"сек";*/
        return time;
    }

    public interface TickListener{
        void onTick(int secondsUntilFinished);
        void onFinish();
    }
}
