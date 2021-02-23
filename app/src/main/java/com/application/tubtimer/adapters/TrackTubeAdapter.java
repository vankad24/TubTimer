package com.application.tubtimer.adapters;

import android.content.DialogInterface;
import android.graphics.Color;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;

import com.application.tubtimer.connection.Command;
import com.application.tubtimer.database.Timer;
import com.application.tubtimer.fragments.TubeFragment;

public class TrackTubeAdapter extends TubeAdapter {
    public TrackTubeAdapter(TubeFragment tubeFragment, int type) {
        super(tubeFragment, type);
    }

    @Override
    public void onBindViewHolder(@NonNull final TubeViewHolder holder, int position) {
        final Timer timer = timers.get(holder.getAdapterPosition());
        holder.timerView.setText(timer.getTimeString());
        holder.tvNumber.setText(timer.number+"");

        int color = Color.BLACK;
        if (!timer.activated)color = Color.RED;
        holder.tvNumber.setTextColor(color);
        holder.timerView.setTextColor(color);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!timer.activated){
                    stopTimer(timer);
                    tubeFragment.commandManager.send(Command.ACTION_CHANGE, timer);
                } else
                    new AlertDialog.Builder(holder.itemView.getContext())
                            .setMessage("Остановить таймер?")
                            .setPositiveButton("Да", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    if (timer.activated){
                                        stopTimer(timer);
                                        tubeFragment.commandManager.send(Command.ACTION_CHANGE, timer);
                                    }
                                }
                            })
                            .setNegativeButton("Нет", null).create().show();
            }
        });

        timer.setOnTickListener(new Timer.TickListener() {
            @Override
            public void onTick(int secondsUntilFinished) {
                holder.timerView.setText(timer.getTimeString());

            }

            @Override
            public void onFinish() {
                holder.timerView.setText(timer.getTimeString());
                finishTimer(timer);
                holder.tvNumber.setTextColor(Color.RED);
                holder.timerView.setTextColor(Color.RED);
            }
        });

    }

    public void finishTimer(Timer timer){
        Toast.makeText(tubeFragment.getActivity().getApplicationContext(),"Время вышло",Toast.LENGTH_SHORT).show();
        tubeFragment.main.myService.sendNotification("Время для тюба номер "+timer.number+" вышло!");
        int position = tubeFragment.trackTubeAdapter.timers.indexOf(timer);
        if (position>0) {
                tubeFragment.trackTubeAdapter.timers.remove(position);
                tubeFragment.trackTubeAdapter.notifyItemRemoved(position);
                recycler.smoothScrollToPosition(0);
                tubeFragment.trackTubeAdapter.timers.add(0,timer);
                tubeFragment.trackTubeAdapter.notifyItemInserted(0);

        }

    }

}
