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

    
    public ArrayList<Timer> getByType(int type) {
        ArrayList<Timer> list = getList(type);
        if (list == null)list = (ArrayList<Timer>) dao.getByType(type);
        return list;
    }

    
    public boolean insert(Timer timer) {
        try{
            dao.insert(timer);
            return getList(timer.type).add(timer);
        }catch (Exception e){
            e.printStackTrace();
        }
        return false;
    }

    
    public void update(Timer timer) {
        dao.update(timer);
        for (int i = 0; i < free.size(); i++)
            if (free.get(i).number==timer.number){
                free.set(i,timer);
                return;
            }
        for (int i = 0; i < track.size(); i++)
            if (track.get(i).number==timer.number){
                track.set(i,timer);
                return;
            }
        for (int i = 0; i < repair.size(); i++)
            if (repair.get(i).number==timer.number){
                repair.set(i,timer);
                return;
            }
    }

    
    public void delete(Timer timer) {
        getList(timer.type).remove(timer);
        dao.delete(timer);
    }

    private ArrayList<Timer> getList(int type) {
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
