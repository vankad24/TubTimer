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

  /*  @Override
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
                    stopTimer(timer,holder.getAdapterPosition());

//                tubeFragment.activeAdapter.notifyDataSetChanged();//todo
            }
        });

        holder.tvNumber.setText(timer.number+"");

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (timer.activated)stopTimer(timer,holder.getAdapterPosition());
                else startTimer(timer);

//                Log.d("myTag",manager.getBy(timer.type).size()+" "+timers.size());

//                Log.d("myTag",(manager.getList(timer.type).size()+" "+timers.size()));
            }
        });
    }*/

    @Override
    public void onViewAttachedToWindow(@NonNull TubeViewHolder holder) {
        if (timers.size() == 0) empty.setVisibility(View.VISIBLE);
        else empty.setVisibility(View.GONE);
        super.onViewAttachedToWindow(holder);
    }

     /*void update(Timer timer){
        manager.update(timer);
        int position = timers.indexOf(timer);
        timers.remove(position);
        notifyItemRemoved(position);
    }

   void stopTimer(Timer timer, int position){

        if (tubeFragment.activeAdapter.type==Timer.TUBE_ON_TRACK){

//            int position = tubeFragment.activeAdapter.timers.indexOf(timer);
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

    }*/

    void startTimer(Timer timer){
        int position = tubeFragment.freeTubeAdapter.timers.indexOf(timer);
//        Log.d("my",timer.number+" "+position);

            tubeFragment.freeTubeAdapter.timers.remove(position);
        timer.start();
//            tubeFragment.freeTubeAdapter.notifyItemRemoved(position);
            tubeFragment.freeTubeAdapter.notifyDataSetChanged();//todo bug

        manager.update(timer);
    }


    void stopTimer(Timer timer) {
        int position = tubeFragment.trackTubeAdapter.timers.indexOf(timer);
        tubeFragment.trackTubeAdapter.timers.remove(position);
        timer.stop();
        tubeFragment.trackTubeAdapter.notifyItemRemoved(position);
        manager.update(timer);
        tubeFragment.freeTubeAdapter.notifyItemInserted(0);
        recycler.smoothScrollToPosition(0);
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
