package com.example.helloworld.Adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.helloworld.Models.Message;
import com.example.helloworld.R;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.Objects;

public class MessageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private ArrayList<Message> messagesList = new ArrayList<>();

    private FirebaseAuth auth;

    public Context context;

    public MessageAdapter(Context context) {
        this.context = context;
        auth = FirebaseAuth.getInstance();
    }

    private int CHATTYPE = 1;

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(viewType == CHATTYPE){
            LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
            View v = layoutInflater.inflate(R.layout.send_message_item, parent, false);
            return new MyViewHolder(v);
        }
        else{
            LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
            View v = layoutInflater.inflate(R.layout.receive_message, parent, false);
            return new MyViewHolderReceive(v);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Message message = messagesList.get(position);

        if(Objects.equals(message.userId, auth.getUid())){
            MyViewHolder myViewHolder = (MyViewHolder) holder;
            myViewHolder.sendMessage.setText(message.getMessage());
            myViewHolder.sendTime.setText(message.getTime());
        }
        else{
            MyViewHolderReceive myViewHolderReceive = (MyViewHolderReceive) holder;
            myViewHolderReceive.receiveMessage.setText(message.getMessage());
            myViewHolderReceive.receiveTime.setText(message.getTime());
        }
    }

    @Override
    public int getItemCount() {
        return messagesList.size();
    }

    @Override
    public int getItemViewType(int position) {
        if(Objects.equals(messagesList.get(position).userId, auth.getUid())){
            return 1;
        }
        else{
           return 0;
        }
    }

    protected class MyViewHolder extends RecyclerView.ViewHolder{
        private MaterialTextView sendMessage, sendTime;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            sendMessage = itemView.findViewById(R.id.txtSendMessage);
            sendTime = itemView.findViewById(R.id.txtSendTime);
        }
    }

    protected class MyViewHolderReceive extends RecyclerView.ViewHolder{
        private MaterialTextView receiveMessage, receiveTime;
        public MyViewHolderReceive(@NonNull View itemView) {
            super(itemView);
            receiveMessage = itemView.findViewById(R.id.txtReceiveMessage);
            receiveTime = itemView.findViewById(R.id.txtReceiveTime);
        }
    }

    public void getMessages(ArrayList<Message> messages){
        if(messages.size() > 0){
            for(Message mess: messages){
                messagesList.add(mess);
                notifyDataSetChanged();
            }
        }
    }

    public void addMessage(Message message, int pos){
        messagesList.add(message);
        notifyItemInserted(pos);
    }

    public void clearData(){
        messagesList.clear();
        notifyDataSetChanged();
    }
}
