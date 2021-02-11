package com.application.tubtimer.adapters;

import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.application.tubtimer.database.Timer;
import com.application.tubtimer.fragments.TubeFragment;

public class TrackTubeAdapter extends TubeAdapter {
    public TrackTubeAdapter(TubeFragment tubeFragment, int type) {
        super(tubeFragment, type);
    }

    @Override
    public void onBindViewHolder(@NonNull final TubeViewHolder holder, int position) {

        final Timer timer = timers.get(position);
        holder.timerView.setText(timer.getTimeString());

        holder.tvNumber.setText(timer.number+"");

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (timer.activated)stopTimer(timer);
            }
        });

        timer.setOnTickListener(new Timer.TickListener() {
            @Override
            public void onTick(int secondsUntilFinished) {
                holder.timerView.setText(timer.getTimeString());
                if (type==Timer.TUBE_ON_TRACK) Log.d("my","Hi in track");
                else Log.d("my","Hi in free");
            }

            @Override
            public void onFinish() {
                Toast.makeText(holder.timerView.getContext(),"Finished",Toast.LENGTH_SHORT).show();
                holder.timerView.setText(timer.getTimeString());
                stopTimer(timer);
                if (timers.size() == 0) empty.setVisibility(View.VISIBLE);
                else empty.setVisibility(View.GONE);
            }
        });

    }

    @Override
    void stopTimer(Timer timer) {
        int position = timers.indexOf(timer);
        timers.remove(position);
        notifyItemRemoved(position);
        timer.stop();
        manager.update(timer);
        tubeFragment.freeTubeAdapter.notifyItemInserted(0);
    }

    @Override
    void startTimer(Timer timer) {

    }

}
