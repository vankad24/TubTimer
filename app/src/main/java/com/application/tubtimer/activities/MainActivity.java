package com.application.tubtimer.activities;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import com.application.tubtimer.R;
import com.application.tubtimer.database.AppDatabase;
import com.application.tubtimer.database.DatabaseManager;
import com.application.tubtimer.database.Timer;
import com.application.tubtimer.database.TimerDao;
import com.application.tubtimer.fragments.TubeFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.room.Room;

import java.io.ObjectInputStream;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    AppDatabase database;
    public DatabaseManager manager;
    boolean bound;
    MyService myService;
    Intent intent;
    ServiceConnection sConn;
    TubeFragment tubeFragment = new TubeFragment();


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
                AppDatabase.class, "tube_database")
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
                myService.sendNotification("Я родился!");

            }

            public void onServiceDisconnected(ComponentName name) {
                Log.d("my", "MainActivity onServiceDisconnected");
                bound = false;
            }
        };
        bindService(intent, sConn, BIND_AUTO_CREATE);

    }


    public void onClick(View view) {
        Intent intent = new Intent(this,SearchActivity.class);
        startActivity(intent);
    }
}