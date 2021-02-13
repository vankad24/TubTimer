package com.application.tubtimer.adapters;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.application.tubtimer.R;
import com.application.tubtimer.database.DatabaseManager;
import com.application.tubtimer.database.Timer;
import com.application.tubtimer.fragments.TubeFragment;

import java.util.ArrayList;

public abstract class TubeAdapter extends RecyclerView.Adapter<TubeAdapter.TubeViewHolder> {
    RecyclerView recycler;
    ArrayList<Timer> timers;
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

    void startTimer(Timer timer) {
        int position = tubeFragment.freeTubeAdapter.timers.indexOf(timer);
        Log.d("my", timer.number + " " + (position == position));

        tubeFragment.freeTubeAdapter.timers.remove(position);
        timer.start();
        tubeFragment.freeTubeAdapter.notifyItemRemoved(position);
        manager.update(timer);
        ((TubeAdapter) recycler.getAdapter()).checkEmpty();
    }


    void stopTimer(Timer timer) {
        int position = tubeFragment.trackTubeAdapter.timers.indexOf(timer);
        tubeFragment.trackTubeAdapter.timers.remove(position);
        timer.stop();
        tubeFragment.trackTubeAdapter.notifyItemRemoved(position);
        manager.update(timer);
        recycler.smoothScrollToPosition(0);
        tubeFragment.freeTubeAdapter.notifyItemInserted(0);
        ((TubeAdapter) recycler.getAdapter()).checkEmpty();
    }

    public void checkEmpty(){
        if (timers.size() == 0) empty.setVisibility(View.VISIBLE);
        else empty.setVisibility(View.GONE);
    }

    public void deleteTimer(int position){
        timers.remove(position);
        manager.delete(timers.get(position));
        notifyItemRemoved(position);

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
