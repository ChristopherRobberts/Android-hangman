package com.example.chris.hangman_android.services;

import com.example.chris.hangman_android.activities.Callback;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;

public class ServerConnection implements Runnable {
    private BufferedReader fromServer;
    private PrintWriter toServer;
    private Socket socket;

    public void start() {
        Thread t1 = new Thread(this);
        t1.start();
    }

    public void run() {
        try {
            this.socket = new Socket();
            this.socket.connect(new InetSocketAddress("192.168.0.13", 8080));
            toServer = new PrintWriter(this.socket.getOutputStream(), true);
            fromServer = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void listenToServer(final Callback serviceCallback) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    while (true) {
                        String state = fromServer.readLine();
                        StateParser parser = new StateParser(state);
                        serviceCallback.updateUI(parser.word, parser.attempts, parser.score);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    void sendMessage(String message) {
        this.toServer.println(message);
    }

    private class StateParser {
        private String word;
        private String score;
        private String attempts;

        private StateParser(String state) {
            String arr[] = state.split("#");
            this.word = arr[0];
            this.attempts = arr[1];
            this.score = arr[2];
        }
    }
}