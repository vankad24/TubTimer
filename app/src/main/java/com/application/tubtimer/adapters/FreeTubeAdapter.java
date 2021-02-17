package com.application.tubtimer.adapters;

import android.content.DialogInterface;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;

import com.application.tubtimer.connection.Command;
import com.application.tubtimer.database.Timer;
import com.application.tubtimer.fragments.TubeFragment;

public class FreeTubeAdapter extends TubeAdapter {

    public FreeTubeAdapter(TubeFragment tubeFragment, int type) {
        super(tubeFragment, type);
    }

    @Override
    public void onBindViewHolder(@NonNull final TubeViewHolder holder, int position) {
        final Timer timer = timers.get(holder.getAdapterPosition());
        holder.timerView.setText(timer.getTimeString());

        holder.tvNumber.setText(timer.number+"");

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                moveToRepair(timer);
                return true;
            }
        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(holder.itemView.getContext())
                        .setMessage("Запустить таймер?")
                        .setPositiveButton("Да", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                startTimer(timer);
                                tubeFragment.commandManager.send(Command.ACTION_CHANGE, timer);
                            }
                        })
                        .setNegativeButton("Нет", null).create().show();
            }
        });

        timer.setOnTickListener(new Timer.TickListener() {
            @Override
            public void onTick(int secondsUntilFinished) {
            }

            @Override
            public void onFinish() {
                Toast.makeText(holder.timerView.getContext(),"Время вышло",Toast.LENGTH_SHORT).show();
                tubeFragment.main.myService.sendNotification("Время для тюба номер "+timer.number+" вышло!");
                stopTimer(timer);
            }
        });
    }


}
