package com.application.tubtimer.fragments;

import android.content.Context;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.application.tubtimer.adapters.TubeAdapter;
import com.application.tubtimer.database.DatabaseManager;
import com.application.tubtimer.database.Timer;
import com.application.tubtimer.activities.MainActivity;
import com.application.tubtimer.R;

import java.util.ArrayList;

public class TubeFragment extends Fragment {

    TextView textView;
    Button button;
    MainActivity main;
    RecyclerView recycler;
    DatabaseManager manager;
    EditText number, duration;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_tube, container, false);
        textView = root.findViewById(R.id.text_tube);
        button = root.findViewById(R.id.startBtn);
        recycler = root.findViewById(R.id.tube_recycler);
        number = root.findViewById(R.id.tub_number);
        duration = root.findViewById(R.id.duration);


        main = (MainActivity) getHost();
        manager = main.manager;
        recycler.setLayoutManager(new LinearLayoutManager(getActivity().getApplicationContext()));


        switch (main.fragmentId){
            case R.id.navigation_track:
                onTrack();
                break;
            case R.id.navigation_free:
                onFree();
                break;
            case R.id.navigation_repair:
                onRepair();
                break;
        }

        return root;
    }


    void onTrack(){
        getActivity().setTitle(R.string.title_track);
        textView.setText("onTrack");


        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if (!manager.insert(new Timer(Integer.parseInt(number.getText().toString()),Integer.parseInt(duration.getText().toString()))))
                        Toast.makeText(getActivity().getApplicationContext(),"Error",Toast.LENGTH_SHORT).show();
                }catch (Exception e){
                    e.printStackTrace();
                    Toast.makeText(getActivity().getApplicationContext(),"Error",Toast.LENGTH_SHORT).show();
                }
                recycler.getAdapter().notifyDataSetChanged();
            }
        });


        recycler.setAdapter(new TubeAdapter(manager.getByType(Timer.TUBE_ON_TRACK)));
    }






    void onFree(){
        getActivity().setTitle(R.string.title_free);
        textView.setText("onFree");

        button.setVisibility(View.GONE);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Vibrator vibrator = (Vibrator) getActivity().getSystemService(Context.VIBRATOR_SERVICE);
/*
                long[] pattern = { 500, 300, 400, 200 };
                if (vibrator.hasVibrator()) {
                    vibrator.vibrate(pattern, 2);
                }*/

            }
        });
    }



    void onRepair(){
        getActivity().setTitle(R.string.title_repair);
        textView.setText("onRepair");

    }

}
