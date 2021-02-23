package com.application.tubtimer.connection;

import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.collection.ArraySet;

import com.adroitandroid.near.connect.NearConnect;
import com.adroitandroid.near.model.Host;
import com.application.tubtimer.activities.MainActivity;
import com.application.tubtimer.adapters.TubeAdapter;
import com.application.tubtimer.database.Timer;
import com.google.gson.Gson;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class CommandManager {

    public final static String UPDATE_ALL = "$update";
    public final static String REQUEST_UPDATE_ALL = "request_update";
    public static final String MESSAGE_REQUEST_PING = "ping";

    MainActivity main;
    public NearConnect nearConnection;

    public CommandManager(MainActivity main) {
        this.main = main;
    }

    public void setPeers(final ArraySet<Host> peers){
        nearConnection = new NearConnect.Builder()
                .forPeers(peers)
                .setContext(main.getApplicationContext())
                .setListener(getNearConnectListener(), Looper.getMainLooper()).build();

        nearConnection.startReceiving();
    }

    @NonNull
    public NearConnect.Listener getNearConnectListener() {
        return new NearConnect.Listener() {
            @Override
            public void onReceive(@NotNull byte[] bytes, @NotNull Host sender) {
                try {
                    String message = new String(bytes);
                    Log.d("my", "receive " + message);
                    if (message.startsWith("&")) {
                        Command command = Command.parseCommand(message);
                        TubeAdapter activeAdapter = (TubeAdapter) main.tubeFragment.recycler.getAdapter();
                        switch (command.action) {
                            case Command.ACTION_ADD:
                                if (main.manager.timerNotExist(command.timer)) {
                                    main.manager.insert(command.timer);
                                    if (command.timer.type == activeAdapter.type)
                                        activeAdapter.notifyItemInserted(0);
                                }
                                break;
                            case Command.ACTION_CHANGE:
                                main.manager.change(command.timer, activeAdapter);
                                break;
                            case Command.ACTION_DELETE:
                                main.manager.delete(command.timer);
                                out:
                                for (int i = 0; i < 3; i++) {
                                    ArrayList<Timer> list = main.manager.getByType(i);
                                    for (int j = 0; j < list.size(); j++) {
                                        Timer t = list.get(j);
                                        if (t.number==command.timer.number){
                                            t.stop();
                                            list.remove(j);
                                            break out;
                                        }
                                    }
                                }
                                activeAdapter.notifyDataSetChanged();
                                break;
                        }
                    } else if (message.startsWith(UPDATE_ALL)) {
                        message = message.substring(UPDATE_ALL.length());
                        UpdateAllHelper.updateAllAdapters(main, message);
                    }else switch (message){
                        case MESSAGE_REQUEST_PING:
                            Toast.makeText(main.getApplicationContext(),"Ping", Toast.LENGTH_SHORT).show();
                            break;
                        case REQUEST_UPDATE_ALL:
                            sendAll();
                            break;
                    }
                }catch (Exception e){
                    e.printStackTrace();
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

    public void requestUpdateAll(){
        if (!DiscoveryManager.host)
            for (Host host: nearConnection.getPeers())
                nearConnection.send(REQUEST_UPDATE_ALL.getBytes(),host);
    }

    public void send(int action , Timer timer){
        if(nearConnection==null/*||!DiscoveryManager.host*/){
            Log.d("my","send null");
            return;
        }
        Log.d("my","send continue");

        byte[] bytes = new Command(action, timer).getBytes();
        for (Host host: nearConnection.getPeers()) {
            nearConnection.send(bytes, host);
        }
    }

    public void sendAll(){
        if (!DiscoveryManager.host)return;
        byte[] bytes =(UPDATE_ALL+new UpdateAllHelper(main.manager.getByType(Timer.TUBE_ON_TRACK),
                main.manager.getByType(Timer.TUBE_FREE),main.manager.getByType(Timer.TUBE_IN_REPAIR))
                .getData()).getBytes();
        for (Host host: nearConnection.getPeers()) {
            nearConnection.send(bytes, host);
        }
    }

    static class UpdateAllHelper{
        static Gson gson = new Gson();;
        public ArrayList<Timer> track;
        public ArrayList<Timer> free;
        public ArrayList<Timer> repair;

        public UpdateAllHelper(ArrayList<Timer> track, ArrayList<Timer> free, ArrayList<Timer> repair) {
            this.track = track;
            this.free = free;
            this.repair = repair;
        }

        String getData(){
            return gson.toJson(this, UpdateAllHelper.class);
        }

        static void updateAllAdapters(final MainActivity main, String data){
            UpdateAllHelper helper = gson.fromJson(data, UpdateAllHelper.class);
            main.manager.setLists(helper.track, helper.free, helper.repair);

            for (final Timer timer:helper.track){
                timer.setOnTickListener(new Timer.TickListener() {
                    @Override
                    public void onTick(int secondsUntilFinished) {
                    }

                    @Override
                    public void onFinish() {
                        main.tubeFragment.trackTubeAdapter.finishTimer(timer);
                    }
                });
                timer.start();
            }

            TubeAdapter adapter = (TubeAdapter) main.tubeFragment.recycler.getAdapter();
            adapter.notifyDataSetChanged();
            adapter.checkEmpty();
        }
    }
}
