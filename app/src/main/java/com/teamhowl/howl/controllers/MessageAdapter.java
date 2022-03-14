package com.teamhowl.howl.controllers;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.teamhowl.howl.R;
import com.teamhowl.howl.models.Message;

import java.util.ArrayList;

public class MessageAdapter extends ArrayAdapter<Message> {

    public MessageAdapter(Context context) {
        super(context, 0);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        // Get the data item for this position
        Message message = getItem(position);

        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {

            if (message.getType() == Message.SENT) {

                convertView = LayoutInflater.from(getContext()).inflate(
                        R.layout.cardview_sent_message,
                        parent,
                        false);

                TextView sentMessage = convertView.findViewById(R.id.sent_message);
                sentMessage.setText(message.getText());

                TextView sentTime1 = convertView.findViewById(R.id.sent_time1);
                //sentTime1.setText(message.getTimeSent().toString());
                //sentTime1.setText("");
            }
            else if (message.getType() == Message.RECIEVED) {

                convertView = LayoutInflater.from(getContext()).inflate(
                        R.layout.cardview_received_message,
                        parent,
                        false);

                TextView receivedMessage = convertView.findViewById(R.id.received_message);
                receivedMessage.setText(message.getText());

                TextView receivedTime1 = convertView.findViewById(R.id.received_time1);
                //receivedTime1.setText(message.getTimeSent().toString());
                //receivedTime1.setText("");

                TextView receivedTime2 = convertView.findViewById(R.id.received_time2);
                //receivedTime2.setText(message.getTimeSent().toString());
                //receivedTime1.setText("");
            }
            else {

                convertView = LayoutInflater.from(getContext()).inflate(
                        R.layout.cardview_sent_message,
                        parent,
                        false);
            }
        }

        // Return the completed view to render on screen
        return convertView;
    }

    public void updateMessages(ArrayList<Message> messages){

        this.clear();
        this.addAll(messages);
    }

}
