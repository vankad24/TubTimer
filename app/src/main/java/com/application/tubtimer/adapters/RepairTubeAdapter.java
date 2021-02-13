package com.application.tubtimer.adapters;

import android.content.DialogInterface;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;

import com.application.tubtimer.database.Timer;
import com.application.tubtimer.fragments.TubeFragment;

public class RepairTubeAdapter extends TubeAdapter {
    public RepairTubeAdapter(TubeFragment tubeFragment, int type) {
        super(tubeFragment, type);
    }

    @Override
    public void onBindViewHolder(@NonNull final TubeViewHolder holder, int position) {
        final Timer timer = timers.get(holder.getAdapterPosition());
        holder.timerView.setText(timer.getTimeString());
        holder.tvNumber.setText(timer.number+"");

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        timer.setOnTickListener(new Timer.TickListener() {
            @Override
            public void onTick(int secondsUntilFinished) {
            }

            @Override
            public void onFinish() {

            }
        });
    }
}
