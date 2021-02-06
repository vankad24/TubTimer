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
import com.application.tubtimer.database.Timer;

import java.util.ArrayList;

public class TubeAdapter extends RecyclerView.Adapter<TubeAdapter.TubeViewHolder> {
    ArrayList<Timer> timers;

    public TubeAdapter(ArrayList<Timer> timers) {
        this.timers = timers;
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
            }

            @Override
            public void onFinish() {
                holder.timerView.setText("Finished!");
                Toast.makeText(holder.timerView.getContext(),"Finished",Toast.LENGTH_SHORT).show();
            }
        });

        holder.tvNumber.setText(timer.number+"");

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (timer.activated)timer.pause();
                else timer.start();
                Log.d("myTag",timer.getStringData());
            }
        });
    }

    public class TubeViewHolder extends RecyclerView.ViewHolder {
        TextView timerView, tvNumber;
        public TubeViewHolder(@NonNull View root) {
            super(root);
            timerView = root.findViewById(R.id.time);
            tvNumber = root.findViewById(R.id.tv_number);
        }
    }
}
