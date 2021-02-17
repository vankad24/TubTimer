package com.application.tubtimer.adapters;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.application.tubtimer.R;
import com.application.tubtimer.connection.Command;
import com.application.tubtimer.database.DatabaseManager;
import com.application.tubtimer.database.Timer;
import com.application.tubtimer.fragments.TubeFragment;

import java.util.ArrayList;

public abstract class TubeAdapter extends RecyclerView.Adapter<TubeAdapter.TubeViewHolder> {
    RecyclerView recycler;
    public ArrayList<Timer> timers;
    TextView empty;
    DatabaseManager manager;
    TubeFragment tubeFragment;
    public int type;

    public TubeAdapter(TubeFragment tubeFragment, int type) {
        this.tubeFragment = tubeFragment;
        timers = tubeFragment.manager.getByType(type);
        empty = tubeFragment.empty;
        manager = tubeFragment.manager;
        recycler = tubeFragment.recycler;
        this.type = type;



    }

    @NonNull
    @Override
    public TubeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.tube_item, parent, false);
        return new TubeViewHolder(v);
    }

    @Override
    public int getItemCount() {
        return timers.size();
    }

    public void startTimer(Timer timer) {
        Log.d("my","startTimer");
        int position = tubeFragment.freeTubeAdapter.timers.indexOf(timer);
        if (position>=0) {
            tubeFragment.freeTubeAdapter.timers.remove(position);
            tubeFragment.freeTubeAdapter.notifyItemRemoved(position);
        }
        timer.start();
        manager.update(timer);
        ((TubeAdapter) recycler.getAdapter()).checkEmpty();
    }


    public void stopTimer(Timer timer) {
        int position = tubeFragment.trackTubeAdapter.timers.indexOf(timer);
        if (position>=0) {
            tubeFragment.trackTubeAdapter.timers.remove(position);
            tubeFragment.trackTubeAdapter.notifyItemRemoved(position);
        }
        timer.stop();
        manager.update(timer);
        recycler.smoothScrollToPosition(0);
        tubeFragment.freeTubeAdapter.notifyItemInserted(0);
        ((TubeAdapter) recycler.getAdapter()).checkEmpty();
    }

    public void deleteTimer(int position){
        manager.delete(timers.get(position));
        timers.remove(position);
        notifyItemRemoved(position);
    }

    public void moveToRepair(Timer timer){
        TubeAdapter adapter;
        if (timer.type == Timer.TUBE_FREE)adapter = tubeFragment.freeTubeAdapter;
        else adapter = tubeFragment.trackTubeAdapter;
        int position = adapter.timers.indexOf(timer);
        adapter.timers.remove(position);
        adapter.notifyItemRemoved(position);
        if (timer.activated)timer.stop();
        timer.type = Timer.TUBE_IN_REPAIR;

        manager.update(timer);
        tubeFragment.repairTubeAdapter.notifyDataSetChanged();
    }

    public void checkEmpty(){
        if (timers.size() == 0) empty.setVisibility(View.VISIBLE);
        else empty.setVisibility(View.GONE);
    }

    class TubeViewHolder extends RecyclerView.ViewHolder {

        TextView timerView, tvNumber;
        public TubeViewHolder(@NonNull View root) {
            super(root);
            timerView = root.findViewById(R.id.time);
            tvNumber = root.findViewById(R.id.tv_number);
        }
    }

}
