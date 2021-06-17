package com.example.socketapp;

import android.content.Context;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder> {

    private ArrayList<MessageModel> mMessageModelArrayList;
    private Context mContext;
    public static final String TAG = "message_adapter";

    public MessageAdapter(Context context, ArrayList<MessageModel> messageModelArrayList){
        this.mContext = context;
        this.mMessageModelArrayList = messageModelArrayList;
    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.message_view_holder, parent, false);
        return new MessageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MessageAdapter.MessageViewHolder holder, int position) {

        MessageModel messageModel = mMessageModelArrayList.get(position);
        holder.messageTextView.setText(messageModel.getMessage());

    }

    @Override
    public int getItemCount() {
        return mMessageModelArrayList.size();
    }

    static public class MessageViewHolder extends RecyclerView.ViewHolder {

        public TextView messageTextView;

        public MessageViewHolder(@NonNull View itemView) {
            super(itemView);

            messageTextView = itemView.findViewById(R.id.message_text_view);

        }
    }

}
