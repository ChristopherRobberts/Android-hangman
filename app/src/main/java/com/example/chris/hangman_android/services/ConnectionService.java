package com.example.chris.hangman_android.services;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

import com.example.chris.hangman_android.activities.Callback;

public class ConnectionService extends Service {
    private ServerConnection serverConnection;
    IBinder mBinder = new MyBinder();
    Callback callback;

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        this.serverConnection = new ServerConnection();
        this.serverConnection.start();
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    public class MyBinder extends Binder {

        public ConnectionService getServerInstance() {
            return ConnectionService.this;
        }
    }

    public void sendMsg(String msg) {
        serverConnection.sendMessage(msg);
    }

    public void listenToServer(Callback callback) {
        this.callback = callback;
        serverConnection.listenToServer(callback);
    }
}
