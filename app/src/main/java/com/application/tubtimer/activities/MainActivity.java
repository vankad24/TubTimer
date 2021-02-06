package com.application.tubtimer.activities;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.MenuItem;

import com.application.tubtimer.R;
import com.application.tubtimer.database.AppDatabase;
import com.application.tubtimer.database.DatabaseManager;
import com.application.tubtimer.database.Timer;
import com.application.tubtimer.database.TimerDao;
import com.application.tubtimer.fragments.TubeFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.room.Room;

import java.io.ObjectInputStream;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    public int fragmentId = R.id.navigation_track;
    AppDatabase database;
    TimerDao dao;
    public DatabaseManager manager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        BottomNavigationView navView = findViewById(R.id.nav_view);

        getSupportFragmentManager().beginTransaction().add(R.id.container, new TubeFragment()).commit();


        navView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                fragmentId = item.getItemId();
                getSupportFragmentManager().beginTransaction().replace(R.id.container, new TubeFragment()).commit();
                return true;
            }
        });


        database = Room.databaseBuilder(getApplicationContext(),
                AppDatabase.class, "tube_database")
                .allowMainThreadQueries()
                .build();

        manager = new DatabaseManager(database.timerDao());

    }

}