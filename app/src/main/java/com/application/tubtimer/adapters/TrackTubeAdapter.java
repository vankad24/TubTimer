package com.application.tubtimer.adapters;

import android.content.DialogInterface;
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


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
            }
        });

    }

    public void finishTimer(Timer timer){
        Toast.makeText(tubeFragment.getActivity().getApplicationContext(),"Время вышло",Toast.LENGTH_SHORT).show();
        tubeFragment.main.myService.sendNotification("Время для тюба номер "+timer.number+" вышло!");
        stopTimer(timer);
    }


}
