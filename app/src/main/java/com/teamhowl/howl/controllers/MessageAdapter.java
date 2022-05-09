package com.teamhowl.howl.controllers;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.teamhowl.howl.R;
import com.teamhowl.howl.models.Message;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class MessageAdapter extends ArrayAdapter<Message> {

    private final static long SECOND = 1;
    private final static long MINUTE = 60 * SECOND;
    private final static long HOUR = 60 * MINUTE;
    private final static long DAY = 24 * HOUR;
    private final static long MONTH = 30 * DAY;
    private final static long YEAR = 12 * MONTH;


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
                sentTime1.setText("sent: " + parseDate(message.getTimeSent()));
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
                receivedTime1.setText("sent: " + parseDate(message.getTimeSent()));
                //receivedTime1.setText("");

                TextView receivedTime2 = convertView.findViewById(R.id.received_time2);
                receivedTime2.setText("rec: " + parseDate(message.getTimeReceived()));
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

    public static String parseDate(Date date){

        long timeSent = date.getTime();
        long currentTime = Calendar.getInstance().getTimeInMillis();
        long delta = currentTime - timeSent;

        delta = delta / 1000;
        if(delta < MINUTE)
            return "now";
        else if(delta < HOUR)
            return (delta / MINUTE) + " minutes";
        else if(delta < DAY)
            return (delta / HOUR) + " minutes";
        else if(delta < YEAR)
            return (delta / DAY) + " minutes";
        else
            return (delta / YEAR) + " years";
    }

}
