package com.example.socketapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.example.socket.MySocket;
import com.example.socket.ResponseCallback;
import com.example.socketapp.databinding.ActivityMainBinding;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private ActivityMainBinding mainBinding;
    private Context mContext;
    private RecyclerView messagesRecyclerView;
    private Button disconnectButton;
    private ImageView sendMessageImageView;
    private EditText sendMessageEditText;
    public static final String TAG = "main_activity";
    private String message;
    private MessageModel messageModel;
    private MessageAdapter messageAdapter;
    private ArrayList<MessageModel> mMessageModelArrayList;
//    private MySocket mySocket;

    private MySocket mySocket;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainBinding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(mainBinding.getRoot());
        mySocket = new ViewModelProvider(this).get(MySocket.class);
        mContext = this;
        messagesRecyclerView = mainBinding.messagesRecyclerView;
        disconnectButton = mainBinding.disconnectButton;
        sendMessageEditText = mainBinding.sendMessageEditText;
        sendMessageImageView = mainBinding.sendMessageImageView;

        messagesRecyclerView.setHasFixedSize(true);
        messagesRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));

        sendMessageImageView.setOnClickListener(this);
        disconnectButton.setOnClickListener(this);

        mMessageModelArrayList = new ArrayList<>();
        mySocket = new MySocket();


        Observer<String> liveDataObserver= s -> {
            Log.d(TAG, "onChanged: ");
            String response = s;
            Log.d(TAG, "Response Message: "+s);
            messageModel = new MessageModel();
            messageModel.setMessage(response);
            mMessageModelArrayList.add(messageModel);
            messagesRecyclerView.setAdapter(messageAdapter);
            messageAdapter.notifyDataSetChanged();
        };
        mySocket.getLiveResponse().observe(this,liveDataObserver);

    }

    @Override
    public void onClick(View v) {
        if (v == sendMessageImageView){
            Log.d(TAG, "Send Message Clicked");
            message = sendMessageEditText.getText().toString().trim();
            if (!TextUtils.isEmpty(message)){
                // Call library methods
                // 1. Start socket connection
                // 2. Send Message
                mySocket.connect();
//                mySocket.sendMessage(message);
                messageModel = new MessageModel();
                messageModel.setMessage("SENT: "+message);
                mMessageModelArrayList.add(messageModel);
                messageAdapter = new MessageAdapter(mContext, mMessageModelArrayList);
                messagesRecyclerView.setAdapter(messageAdapter);
                messageAdapter.notifyDataSetChanged();
                sendMessageEditText.getText().clear();
            }else {
                sendMessageEditText.setError("Enter a message");
            }

        }else if (v == disconnectButton){
            Log.d(TAG, "Disconnect Button Clicked");
            mySocket.disconnect();
        }
    }


}