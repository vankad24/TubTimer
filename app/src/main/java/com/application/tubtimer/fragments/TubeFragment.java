package com.application.tubtimer.fragments;

import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.application.tubtimer.adapters.FreeTubeAdapter;
import com.application.tubtimer.adapters.RepairTubeAdapter;
import com.application.tubtimer.adapters.TrackTubeAdapter;
import com.application.tubtimer.adapters.TubeAdapter;
import com.application.tubtimer.connection.Command;
import com.application.tubtimer.connection.CommandManager;
import com.application.tubtimer.database.DatabaseManager;
import com.application.tubtimer.database.Timer;
import com.application.tubtimer.activities.MainActivity;
import com.application.tubtimer.R;

public class TubeFragment extends Fragment {

    Button button_add, button_show;
    public MainActivity main;
    public RecyclerView recycler;
    public DatabaseManager manager;
    public TextView empty;
    EditText number, duration;
    LinearLayout linear_add;
    public CommandManager commandManager;

    public TrackTubeAdapter trackTubeAdapter;
    public FreeTubeAdapter freeTubeAdapter;
    public RepairTubeAdapter repairTubeAdapter;


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
        ((TubeAdapter) recycler.getAdapter()).checkEmpty();

    }

    public void hideKeyboard(){
        // прячем клавиатуру
        InputMethodManager imm = (InputMethodManager) main.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(number.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        imm.hideSoftInputFromWindow(duration.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }

    public void offerNumber(){
        int i=1;
        while (!manager.timerNotExist(i))i++;
        number.setText(i+"");
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
        //todo переворот
        RecyclerView.LayoutManager layoutManager;
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT)
            layoutManager = new LinearLayoutManager(main.getApplicationContext());
        else layoutManager = new GridLayoutManager(main.getApplicationContext(),2);

        recycler.setLayoutManager(layoutManager);

        freeTubeAdapter = new FreeTubeAdapter(this,Timer.TUBE_FREE);
        trackTubeAdapter = new TrackTubeAdapter(this,Timer.TUBE_ON_TRACK);
        repairTubeAdapter = new RepairTubeAdapter(this,Timer.TUBE_IN_REPAIR);



        button_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    TubeAdapter adapter = (TubeAdapter) recycler.getAdapter();
                    Timer timer = new Timer(Integer.parseInt(number.getText().toString()),
                            Integer.parseInt(duration.getText().toString()), adapter.type);
                    if (manager.timerNotExist(timer)){
                        manager.insert(timer);
                        commandManager.send(Command.ACTION_ADD, timer);
                        recycler.smoothScrollToPosition(0);
                        adapter.notifyItemInserted(0);
                        adapter.checkEmpty();
                        offerNumber();
                    }else Toast.makeText(main, "Error", Toast.LENGTH_SHORT).show();

                }catch (Exception e){
                    e.printStackTrace();
                    Toast.makeText(main,"Error",Toast.LENGTH_SHORT).show();
                }

            }
        });

        changeFragment(R.id.navigation_track);

        ItemTouchHelper.Callback callback = new ItemTouchHelper.Callback() {
            @Override
            public boolean isItemViewSwipeEnabled() {
                return true;
            }

            @Override
            public int getMovementFlags(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
                return makeMovementFlags(0, ItemTouchHelper.START | ItemTouchHelper.END);
            }

            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull final RecyclerView.ViewHolder holder, int direction) {
                new AlertDialog.Builder(holder.itemView.getContext())
                        .setMessage("Удалить таймер?")
                        .setPositiveButton("Да", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                TubeAdapter adapter = (TubeAdapter) recycler.getAdapter();
                                commandManager.send(Command.ACTION_DELETE, adapter.timers.get(holder.getAdapterPosition()));
                                adapter.deleteTimer(holder.getAdapterPosition());
                            }
                        })
                        .setNegativeButton("Нет", null)
                        .setOnDismissListener(new DialogInterface.OnDismissListener() {
                            @Override
                            public void onDismiss(DialogInterface dialog) {
                                recycler.getAdapter().notifyDataSetChanged();
                            }
                        }).create().show();

            }
        };
        new ItemTouchHelper(callback).attachToRecyclerView(recycler);



        button_show.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (linear_add.getVisibility()==View.GONE){
                    linear_add.setVisibility(View.VISIBLE);
                    button_show.setText("hide");
                    offerNumber();
                }else{
                    linear_add.setVisibility(View.GONE);
                    button_show.setText("show");
                    hideKeyboard();
                }
            }
        });

        return root;
    }






    void onTrack(){
        getActivity().setTitle(R.string.title_track);
        button_show.setVisibility(View.GONE);
        linear_add.setVisibility(View.GONE);

        recycler.setAdapter(trackTubeAdapter);
    }






    void onFree(){
        getActivity().setTitle(R.string.title_free);
        button_show.setVisibility(View.VISIBLE);
        button_show.setText("show");

        recycler.setAdapter(freeTubeAdapter);

    }






    void onRepair(){
        getActivity().setTitle(R.string.title_repair);
        button_show.setVisibility(View.VISIBLE);
        button_show.setText("show");

        recycler.setAdapter(repairTubeAdapter);
    }
}
