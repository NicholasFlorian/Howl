package com.teamhowl.howl.controllers;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.teamhowl.howl.models.ChatRoom;
import com.teamhowl.howl.models.User;

import java.util.ArrayList;
import java.util.List;

import com.teamhowl.howl.R;

public class ChatRoomAdapter extends ArrayAdapter<ChatRoom> {
    public ChatRoomAdapter(Context context, List<ChatRoom> chatRooms) {
        super(context, 0, chatRooms);
    }

    @Override

    public View getView(int position, View convertView, ViewGroup parent) {

        // Get the data item for this position
        ChatRoom chatRoom = getItem(position);

        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {

            convertView = LayoutInflater.from(getContext()).inflate(
                    R.layout.cardview_connect_user,
                    parent,
                    false);
        }
        // Lookup view for data population
        //TextView userName = (TextView) convertView.findViewById(R.id.connect_user_name);
        //TextView chatId = (TextView) convertView.findViewById(R.id.connect_chat_id);

        // Populate the data into the template view using the data object
        //userName.setText(chatRoom.user.getUserName());
        //chatId.setText(user.getChatId());

        // Return the completed view to render on screen
        return convertView;
    }

}
