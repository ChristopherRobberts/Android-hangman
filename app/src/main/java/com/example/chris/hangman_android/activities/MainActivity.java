package com.example.chris.hangman_android.activities;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.AsyncTask;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.chris.hangman_android.R;
import com.example.chris.hangman_android.services.ConnectionService;

public class MainActivity extends AppCompatActivity implements Callback {

    private ConnectionService mServer;
    private boolean mBounded;
    private Button guessButton;
    private TextView guessTextView;
    private TextView wordState;
    private TextView scoreValue;
    private TextView attemptsValue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        guessTextView = findViewById(R.id.guessTextView);
        wordState = findViewById(R.id.wordState);
        guessButton = findViewById(R.id.guessButton);
        scoreValue = findViewById(R.id.scoreValue);
        attemptsValue = findViewById(R.id.attemptsValue);

        Intent intent = new Intent(this, ConnectionService.class);
        startService(intent);
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);

        guessButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String guess = guessTextView.getText().toString();
                new ServerCommunicator().execute(guess);
            }
        });
    }

    public void updateUI(final String word, final String remaining, final String score) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                wordState.setText(word);
                attemptsValue.setText(remaining);
                scoreValue.setText(score);
            }
        });
    }

    public void onConnectionMade() {
        mServer.listenToServer(MainActivity.this);
    }

    ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mBounded = true;
            ConnectionService.MyBinder mLocalBinder = (ConnectionService.MyBinder) service;
            mServer = mLocalBinder.getServerInstance();

            onConnectionMade();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mBounded = false;
            mServer = null;
        }
    };


    private class ServerCommunicator extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected String doInBackground(String... params) {
            mServer.sendMsg(params[0]);
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            guessTextView.setText("");
        }
    }
}
