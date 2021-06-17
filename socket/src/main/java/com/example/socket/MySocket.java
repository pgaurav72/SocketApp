package com.example.socket;

import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

import java.net.URISyntaxException;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class MySocket {

    private static final String TAG = "socket_class";
    private Socket socket;
    private String message;
    private String url = "wss://echo.websocket.org/";


    public void launch() {
        try {
            socket = IO.socket(url);
            Log.d(TAG, "Launch: ");
            if (!socket.isActive()){
                Log.d(TAG, "Socket connecting");
                socket.connect();
            }
        }catch (Exception e){
            Log.d(TAG, "Socket exception: "+e.toString());
        }
    }

    public void sendMessage(String message){
        Log.d(TAG, "sendMessage: "+message);
        socket.emit("SENT", message);
    }

    public String receivedMessage(){
        Log.d(TAG, "receving message");
        socket.on("load", args -> {
            if (!TextUtils.isEmpty(args[0].toString())){
                message = args[0].toString();
                Log.d(TAG, "receivedMessage: "+message);
            }else {
                Log.d(TAG, "Empty response");
            }
        });
        return message;
    }

    public void disconnect(){
        Log.d(TAG, "disconnect: ");
        socket.disconnect();
    }




}
