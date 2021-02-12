package com.application.tubtimer.activities;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.collection.ArraySet;
import androidx.room.Room;

import com.adroitandroid.near.connect.NearConnect;
import com.adroitandroid.near.model.Host;
import com.application.tubtimer.R;
import com.application.tubtimer.connection.Command;
import com.application.tubtimer.database.AppDatabase;
import com.application.tubtimer.database.DatabaseManager;
import com.application.tubtimer.fragments.TubeFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    AppDatabase database;
    public DatabaseManager manager;
    boolean bound;
    MyService myService;
    Intent intent;
    ServiceConnection sConn;
    TubeFragment tubeFragment = new TubeFragment();

    ArrayList<Host> connectedHosts;
    NearConnect nearConnection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        BottomNavigationView navView = findViewById(R.id.nav_view);

        getSupportFragmentManager().beginTransaction().add(R.id.container, tubeFragment).commit();
        navView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                tubeFragment.changeFragment(item.getItemId());
//                getSupportFragmentManager().beginTransaction().replace(R.id.container, tubeFragment).commit();
                return true;
            }
        });

        database = Room.databaseBuilder(getApplicationContext(),
                AppDatabase.class, "tube_database1")
                .allowMainThreadQueries()
                .build();

        manager = new DatabaseManager(database.timerDao());

        intent = new Intent(getApplicationContext(), MyService.class);
        sConn = new ServiceConnection() {
            public void onServiceConnected(ComponentName name, IBinder binder) {
                Log.d("my", "MainActivity onServiceConnected");
                myService = ((MyService.MyBinder) binder).getService();
                bound = true;
                myService.nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                    NotificationChannel channel = new NotificationChannel(myService.NOTIF_ID,myService.NOTIF_ID, NotificationManager.IMPORTANCE_DEFAULT);
                    myService.nm.createNotificationChannel(channel);
                }
//                myService.sendNotification("Я родился!");

            }

            public void onServiceDisconnected(ComponentName name) {
                Log.d("my", "MainActivity onServiceDisconnected");
                bound = false;
            }
        };
        bindService(intent, sConn, BIND_AUTO_CREATE);

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        connectedHosts = data.getParcelableExtra(SearchActivity.DEVICES);
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void initNearConnect() {
        ArraySet<Host> peers = new ArraySet<>(connectedHosts);
        nearConnection = new NearConnect.Builder()
                .forPeers(peers)
                .setContext(this)
                .setListener(getNearConnectListener(), Looper.getMainLooper()).build();

        nearConnection.startReceiving();
    }

    @NonNull
    private NearConnect.Listener getNearConnectListener() {
        return new NearConnect.Listener() {
            @Override
            public void onReceive(@NotNull byte[] bytes, @NotNull Host sender) {
                String message = new String(bytes);
                if (message.startsWith("&")) {
                    Command command = Command.parseCommand(message);
                    //todo switch()
                    switch (command.action){

                    }
                }

            }

            @Override
            public void onSendComplete(long jobId) {

            }

            @Override
            public void onSendFailure(@org.jetbrains.annotations.Nullable Throwable e, long jobId) {

            }

            @Override
            public void onStartListenFailure(@org.jetbrains.annotations.Nullable Throwable e) {

            }
        };
    }

    public void onClick(View view) {
        Intent intent = new Intent(this,SearchActivity.class);
        startActivity(intent);
    }
}