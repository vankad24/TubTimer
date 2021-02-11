package com.application.tubtimer.adapters;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.application.tubtimer.R;
import com.application.tubtimer.database.DatabaseManager;
import com.application.tubtimer.database.Timer;
import com.application.tubtimer.fragments.TubeFragment;

import java.util.ArrayList;

public class TubeAdapter extends RecyclerView.Adapter<TubeAdapter.TubeViewHolder> {
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
        if (timers.size()==0)empty.setVisibility(View.VISIBLE);
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

    @Override
    public void onBindViewHolder(@NonNull final TubeViewHolder holder, final int position) {
        final Timer timer = timers.get(position);
        holder.timerView.setText(timer.getTimeString());

        timer.setOnTickListener(new Timer.TickListener() {
            @Override
            public void onTick(int secondsUntilFinished) {
                holder.timerView.setText(timer.getTimeString());
                if (tubeFragment.activeAdapter.type==Timer.TUBE_ON_TRACK)Log.d("my","Hi in track");
                else Log.d("my","Hi in free");
            }

            @Override
            public void onFinish() {
                Toast.makeText(holder.timerView.getContext(),"Finished",Toast.LENGTH_SHORT).show();
//                if (tubeFragment.activeAdapter!=TubeAdapter.this){//значит мы во вкладке на трассе

                    if (timers.size() == 0) empty.setVisibility(View.VISIBLE);
                    else empty.setVisibility(View.GONE);
                    stopTimer(timer);

//                tubeFragment.activeAdapter.notifyDataSetChanged();//todo
            }
        });

        holder.tvNumber.setText(timer.number+"");

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (timer.activated)stopTimer(timer);
                else startTimer(timer);

//                Log.d("myTag",manager.getBy(timer.type).size()+" "+timers.size());

//                Log.d("myTag",(manager.getList(timer.type).size()+" "+timers.size()));
            }
        });
    }

    @Override
    public void onViewAttachedToWindow(@NonNull TubeViewHolder holder) {
        if (timers.size() == 0) empty.setVisibility(View.VISIBLE);
        else empty.setVisibility(View.GONE);
        super.onViewAttachedToWindow(holder);
    }

    void update(Timer timer){
        manager.update(timer);
        int position = timers.indexOf(timer);
        timers.remove(position);
        notifyItemRemoved(position);
    }

    void stopTimer(Timer timer){

        if (tubeFragment.activeAdapter.type==Timer.TUBE_ON_TRACK){

            int position = tubeFragment.activeAdapter.timers.indexOf(timer);
            tubeFragment.activeAdapter.timers.remove(position);
            tubeFragment.activeAdapter.notifyItemRemoved(position);
            timer.stop();
            manager.update(timer);
            Log.d("my","Hi in track");
        }else {
            manager.getByType(Timer.TUBE_ON_TRACK).remove(timer);
            timer.stop();
            manager.update(timer);
            tubeFragment.activeAdapter.notifyItemInserted(0);
            Log.d("my","Hi in free");
        }

        /*if (type==Timer.TUBE_ON_TRACK){
            if (tubeFragment.activeAdapter.type==type){
                int position = timers.indexOf(timer);
                timers.remove(position);
                notifyItemRemoved(position);
                timer.stop();
                manager.update(timer);
            }else {
                int position = timers.indexOf(timer);
                timers.remove(position);
                timer.stop();
                manager.update(timer);
                tubeFragment.activeAdapter.notifyItemInserted(0);
            }
        }else {
            if (tubeFragment.activeAdapter.type==type){
                ArrayList<Timer> byType = manager.getByType(timer.type);
                byType.remove(timer);
                timer.stop();
                manager.update(timer);
                notifyItemInserted(0);
            }else {
                Log.d("my","Hi 4");
            }
        }*/
        /*int position = timers.indexOf(timer);
        if (position>0) {
            timers.remove(position);
            notifyItemRemoved(position);
            timer.stop();
            manager.update(timer);
            if (tubeFragment.activeAdapter.type == Timer.TUBE_FREE)
                tubeFragment.activeAdapter.notifyItemInserted(0);
        }else {
            ArrayList<Timer> byType = manager.getByType(timer.type);
            byType.remove(timer);
            timer.stop();
            manager.update(timer);
            notifyItemInserted(0);
        }*/


    }

    void startTimer(Timer timer){
        int position = timers.indexOf(timer);
        if (position>=0){
            timers.remove(position);
            notifyItemRemoved(position);
        }
        timer.start();
        manager.update(timer);
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
