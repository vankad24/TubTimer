package com.application.tubtimer.adapters;

import android.content.DialogInterface;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;

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

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(holder.itemView.getContext())
                        .setMessage("Запустить таймер?")
                        .setPositiveButton("Да", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                startTimer(timer);
                            }
                        })
                        .setNegativeButton("Нет", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        }).create().show();
            }
        });

        timer.setOnTickListener(new Timer.TickListener() {
            @Override
            public void onTick(int secondsUntilFinished) {
                if (!timer.activated)holder.timerView.setText(Timer.getTimeString(secondsUntilFinished));
                /*if (tubeFragment.activeAdapter.type==Timer.TUBE_ON_TRACK) Log.d("my","Hi in track");
                else Log.d("my","Hi in free");*/
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


}
