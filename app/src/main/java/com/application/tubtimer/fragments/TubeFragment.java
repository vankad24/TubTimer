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
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.application.tubtimer.adapters.FreeTubeAdapter;
import com.application.tubtimer.adapters.TrackTubeAdapter;
import com.application.tubtimer.adapters.TubeAdapter;
import com.application.tubtimer.database.DatabaseManager;
import com.application.tubtimer.database.Timer;
import com.application.tubtimer.activities.MainActivity;
import com.application.tubtimer.R;

public class TubeFragment extends Fragment {

    Button button_add, button_show;
    MainActivity main;
    RecyclerView recycler;
    public DatabaseManager manager;
    EditText number, duration;
    LinearLayout linear_add;
    public TextView empty;

    public TubeAdapter activeAdapter;

    public static TrackTubeAdapter trackTubeAdapter;
    public static FreeTubeAdapter freeTubeAdapter;


    public void changeFragment(int id){
        switch (id){
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
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {


        View root = inflater.inflate(R.layout.fragment_tube, container, false);
        button_add = root.findViewById(R.id.startBtn);
        button_show = root.findViewById(R.id.show_add);
        recycler = root.findViewById(R.id.tube_recycler);
        number = root.findViewById(R.id.tub_number);
        duration = root.findViewById(R.id.duration);
        linear_add = root.findViewById(R.id.linear_add);
        empty = root.findViewById(R.id.empty_list);

        main = (MainActivity) getHost();



        manager = main.manager;
        recycler.setLayoutManager(new LinearLayoutManager(getActivity().getApplicationContext()));


        freeTubeAdapter = new FreeTubeAdapter(this,Timer.TUBE_FREE);
        trackTubeAdapter = new TrackTubeAdapter(this,Timer.TUBE_ON_TRACK);


        changeFragment(R.id.navigation_track);
        return root;
    }


    void onTrack(){
        getActivity().setTitle(R.string.title_track);
        button_show.setVisibility(View.GONE);

//        activeAdapter = new TubeAdapter(this,Timer.TUBE_ON_TRACK);
        recycler.setAdapter(trackTubeAdapter);
    }






    void onFree(){
        getActivity().setTitle(R.string.title_free);
//        textView.setText("onFree");
        button_show.setVisibility(View.VISIBLE);


        button_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if (!manager.insert(new Timer(Integer.parseInt(number.getText().toString()),
                            Integer.parseInt(duration.getText().toString()), Timer.TUBE_FREE)))
                        Toast.makeText(getActivity().getApplicationContext(),"Error",Toast.LENGTH_SHORT).show();
                }catch (Exception e){
                    e.printStackTrace();
                    Toast.makeText(getActivity().getApplicationContext(),"Error",Toast.LENGTH_SHORT).show();
                }
                recycler.getAdapter().notifyDataSetChanged();
            }
        });


        button_show.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (linear_add.getVisibility()==View.GONE){
                    linear_add.setVisibility(View.VISIBLE);
                    button_show.setText("hide");
                }else{
                    linear_add.setVisibility(View.GONE);
                    button_show.setText("show");
                }
            }
        });
//        activeAdapter = new TubeAdapter(this,Timer.TUBE_FREE);


        recycler.setAdapter(freeTubeAdapter);


        /*button_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Vibrator vibrator = (Vibrator) getActivity().getSystemService(Context.VIBRATOR_SERVICE);

                long[] pattern = { 500, 300, 400, 200 };
                if (vibrator.hasVibrator()) {
                    vibrator.vibrate(pattern, 2);
                }
            }
        });*/
    }



    void onRepair(){
        getActivity().setTitle(R.string.title_repair);
//        textView.setText("onRepair");
        button_show.setVisibility(View.VISIBLE);

        button_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if (!manager.insert(new Timer(Integer.parseInt(number.getText().toString()),
                            Integer.parseInt(duration.getText().toString()), Timer.TUBE_IN_REPAIR)))
                        Toast.makeText(getActivity().getApplicationContext(),"Error",Toast.LENGTH_SHORT).show();
                }catch (Exception e){
                    e.printStackTrace();
                    Toast.makeText(getActivity().getApplicationContext(),"Error",Toast.LENGTH_SHORT).show();
                }
                recycler.getAdapter().notifyDataSetChanged();
            }
        });


        button_show.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (linear_add.getVisibility()==View.GONE){
                    linear_add.setVisibility(View.VISIBLE);
                    button_show.setText("hide");
                }else{
                    linear_add.setVisibility(View.GONE);
                    button_show.setText("show");
                }
            }
        });
        activeAdapter = new TubeAdapter(this,Timer.TUBE_IN_REPAIR);
        recycler.setAdapter(activeAdapter);

    }

}
