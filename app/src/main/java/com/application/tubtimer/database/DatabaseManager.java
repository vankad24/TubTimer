package com.application.tubtimer.database;

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
        return null;
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
//        getByType(timer.type).remove(timer);
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
}
