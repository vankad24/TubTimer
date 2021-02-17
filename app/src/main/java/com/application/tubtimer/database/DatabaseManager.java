package com.application.tubtimer.database;

import android.util.Log;

import com.application.tubtimer.adapters.TubeAdapter;

import java.util.ArrayList;
import java.util.List;

public class DatabaseManager{
    TimerDao dao;
    private ArrayList<Timer> track;
    private ArrayList<Timer> free;
    private ArrayList<Timer> repair;

    public DatabaseManager(TimerDao dao) {
        this.dao = dao;
    }

    public List<Timer> getAll(){
        return null;
    }

    
    public Timer getByNumber(int number) {
        for (int i = 0; i < 3; i++) {
            for (Timer t:getByType(i)){
                if (t.number==number)return t;
            }
        }
        return null;
    }

    public boolean timerNotExist(Timer timer){
        return timerNotExist(timer.number);
    }

    public boolean timerNotExist(int number){
        return dao.getByNumber(number)==null;
    }


    public void change(Timer timer, TubeAdapter activeAdapter){
        for (int i = 0; i < 3; i++) {
            ArrayList<Timer> list = getByType(i);
            for (int j = 0; j < list.size(); j++) {
                Timer t = list.get(j);
                if (t.number==timer.number){
                    Log.d("my", "change tube " + t.number);
                    if (timer.type == Timer.TUBE_IN_REPAIR)activeAdapter.moveToRepair(t);
                    else {
                        list.set(j, timer);
                        if (timer.activated) activeAdapter.startTimer(timer);
                        else activeAdapter.stopTimer(timer);
                        activeAdapter.notifyDataSetChanged();
                    }
                    return;
                }
            }
        }
    }
    
    public boolean insert(Timer timer) {
        try{
            dao.insert(timer);
            getByType(timer.type).add(0, timer);
            return true;
        }catch (Exception e){
            e.printStackTrace();
        }
        return false;
    }

    
    public void update(Timer timer) {
        dao.update(timer);
        getByType(timer.type).add(0,timer);
    }

    
    public void delete(Timer timer) {
        dao.deleteByNumber(timer.number);
    }

    public ArrayList<Timer> getByType(int type) {
        switch (type) {
            case Timer.TUBE_FREE:
                if (free==null)free = (ArrayList<Timer>) dao.getByType(Timer.TUBE_FREE);
                return free;
            case Timer.TUBE_ON_TRACK:
                if (track==null)track = (ArrayList<Timer>) dao.getByType(Timer.TUBE_ON_TRACK);
                return track;
            case Timer.TUBE_IN_REPAIR:
                if (repair==null)repair = (ArrayList<Timer>) dao.getByType(Timer.TUBE_IN_REPAIR);
                return repair;
        }
        return null;
    }

    public void setLists(ArrayList<Timer> track, ArrayList<Timer> free, ArrayList<Timer> repair){
        //todo очистить БД
        this.track = track;
        this.free = free;
        this.repair = repair;
    }
}
