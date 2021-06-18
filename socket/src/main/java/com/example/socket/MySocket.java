package com.example.socket;

import android.os.AsyncTask;
import android.util.Log;
import com.kaazing.net.ws.WebSocket;
import com.kaazing.net.ws.WebSocketFactory;
import com.kaazing.net.ws.WebSocketMessageReader;
import com.kaazing.net.ws.WebSocketMessageType;
import com.kaazing.net.ws.WebSocketMessageWriter;

import java.io.IOException;
import java.net.URI;
import java.util.Arrays;

public class MySocket {

    private static final String TAG = "socket_class";
//    private Socket socket;
    private String message;
    private String url = "wss://echo.websocket.org/";

    private WebSocket mWebSocket;
    private DispatchQueue dispatchQueue;
    private MessageReceiver messageReceiver;
    private String responseMessage;
    public MessageReceiver mMessageReceiver;

    public void connect(){
        dispatchQueue = new DispatchQueue("Async Dispatch Queue");
        dispatchQueue.start();
        dispatchQueue.dispatchAsync(() -> {
            try {
                WebSocketFactory webSocketFactory = WebSocketFactory.createWebSocketFactory();
                mWebSocket = webSocketFactory.createWebSocket(URI.create(url));
                mWebSocket.connect();
                Log.d(TAG, "CONNECTED");
                WebSocketMessageWriter messageWriter = mWebSocket.getMessageWriter();
                messageWriter.writeText(message);
                Log.d(TAG, "sendMessage: "+message);

                WebSocketMessageReader messageReader = mWebSocket.getMessageReader();
                messageReceiver = new MessageReceiver(messageReader);
                mMessageReceiver = messageReceiver;
            } catch (Exception e) {
                Log.d(TAG, "Connect exception: "+e.toString());
                Log.d(TAG, "Exception message: "+e.getMessage());
                Log.d(TAG, "Exception message: "+e.getCause().toString());
                Log.d(TAG, "Exception code: "+ Arrays.toString(e.getStackTrace()));
                dispatchQueue.quit();
            }
        });
    }

//    public void sendMessage(String message){
//        dispatchQueue.dispatchAsync(() -> {
//            try {
//                WebSocketMessageWriter messageWriter = mWebSocket.getMessageWriter();
//                messageWriter.writeText(message);
//                Log.d(TAG, "sendMessage: "+message);
//            } catch (Exception e) {
//                e.printStackTrace();
//                Log.d(TAG, "sendMessage: "+e.getMessage());
//            }
//        });
//
//    }


    public String getResponseMessage(){
        new Thread(() -> {
            Log.d(TAG, "Getting Response Message");

        }).start();
        return responseMessage;
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
                Log.d(TAG, "DISCONNECTED");;
            } catch (IOException e) {
                Log.d(TAG, "run: "+e.getMessage());
            }
            finally {
                mWebSocket = null;
            }
        }).start();
    }


    public class MessageReceiver{

        private static final String TAG = "message_receiver";
        private WebSocketMessageReader messageReader;

        public MessageReceiver(WebSocketMessageReader reader) {
            this.messageReader = reader;
        }

        public void getResponse(ResultListener<String> resultListener){

            new Thread(() -> {
                try {
                    while (messageReader.next() != WebSocketMessageType.EOS) {
                        CharSequence charSequence = messageReader.getText();
                        message = charSequence.toString();
                        Log.d(TAG, "Received Message: "+message);
                        resultListener.listen(message);
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
                }
            }).start();
        }

    }





}
