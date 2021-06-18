package com.example.socket;

import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.kaazing.net.ws.WebSocket;
import com.kaazing.net.ws.WebSocketFactory;
import com.kaazing.net.ws.WebSocketMessageReader;
import com.kaazing.net.ws.WebSocketMessageType;
import com.kaazing.net.ws.WebSocketMessageWriter;

import java.io.IOException;
import java.net.URI;
import java.util.Arrays;

public class MySocket extends ViewModel {

    private static final String TAG = "socket_class";
    private String message;
    private String url = "wss://echo.websocket.org/";

    private WebSocket mWebSocket;
    private DispatchQueue dispatchQueue;

    private MutableLiveData<String> responseMutableLiveData =
            new MutableLiveData<>();
    private MutableLiveData<Boolean> connectionLiveData =
            new MutableLiveData<>();
    private MutableLiveData<Boolean> resultStatusLiveData =
            new MutableLiveData<>();

    public void connect(String message){
        dispatchQueue = new DispatchQueue("Async Dispatch Queue");
        dispatchQueue.start();
        dispatchQueue.dispatchAsync(() -> {
            try {
                WebSocketFactory webSocketFactory = WebSocketFactory.createWebSocketFactory();
                mWebSocket = webSocketFactory.createWebSocket(URI.create(url));
                mWebSocket.connect();
                Log.d(TAG, "CONNECTED");

                new Handler(Looper.getMainLooper())
                        .post(() -> connectionLiveData.setValue(true));

                WebSocketMessageReader messageReader = mWebSocket.getMessageReader();
                MessageReceiver messageReceiver = new MessageReceiver(messageReader);
                new Thread(messageReceiver).start();

                WebSocketMessageWriter messageWriter = mWebSocket.getMessageWriter();
                messageWriter.writeText(message);
                Log.d(TAG, "sendMessage: "+message);

            } catch (Exception e) {
                Log.d(TAG, "Connect exception: "+e.toString());
                Log.d(TAG, "Exception message: "+e.getMessage());
                Log.d(TAG, "Exception message: "+e.getCause().toString());
                Log.d(TAG, "Exception code: "+ Arrays.toString(e.getStackTrace()));
                dispatchQueue.quit();
            }
        });
    }

    public void disconnect() {
        Log.d(TAG, "DISCONNECTING: ");
        if (dispatchQueue.isAlive()){
            Log.d(TAG, "Dispatch Queue Alive: ");
            dispatchQueue.removePendingJobs();
            dispatchQueue.quit();
        }else {
            Log.d(TAG, "Dispatch Queue Not Alive: ");
        }
        new Thread(() -> {
            try {
                mWebSocket.close();
                Log.d(TAG, "DISCONNECTED");
                new Handler(Looper.getMainLooper())
                        .post(() -> connectionLiveData.setValue(false));
            } catch (IOException e) {
                Log.d(TAG, "run: "+e.getMessage());
            }
            finally {
                mWebSocket = null;
            }
        }).start();
    }

    public MutableLiveData<String> getLiveResponse() {
        return responseMutableLiveData;
    }

    public MutableLiveData<Boolean> getConnectionStatus() {
        return connectionLiveData;
    }

    public MutableLiveData<Boolean> getResponseStatus() {
        return resultStatusLiveData;
    }



    public class MessageReceiver implements Runnable{

        private static final String TAG = "message_receiver";
        private WebSocketMessageReader messageReader;

        public MessageReceiver(WebSocketMessageReader reader) {
            this.messageReader = reader;
        }

        @Override
        public void run() {
            try {
                while (messageReader.next() != WebSocketMessageType.EOS) {
                    CharSequence charSequence = messageReader.getText();
                    message = charSequence.toString();
                    Log.d(TAG, "Received Message: "+message);

                    if (!TextUtils.isEmpty(message)){
                        new Handler(Looper.getMainLooper())
                                .post(() -> resultStatusLiveData.setValue(true));
                    }else {
                        new Handler(Looper.getMainLooper())
                                .post(() -> resultStatusLiveData.setValue(false));
                    }

                    new Handler(Looper.getMainLooper()).post(() -> responseMutableLiveData.setValue(message));
                }
//            if (!closedExplicitly) {
//
//                // Connection got closed due to either of the cases
//                // - Server closing the connection because of authentication time out
//                // - network failure
//                webSocket = null;
//                logMessage("Connection Closed!!");
//                updateButtonsForDisconnected();
//                if (loginDialog != null && !loginDialog.isHidden()) {
//                    loginDialog.cancel();
//                }
//            }
            }
            catch (Exception ex) {
                ex.printStackTrace();
                Log.d(TAG, "run: "+ex.getMessage());
                new Handler(Looper.getMainLooper())
                        .post(() -> resultStatusLiveData.setValue(false));
            }
        }

    }





}
