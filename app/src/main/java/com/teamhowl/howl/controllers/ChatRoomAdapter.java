package com.teamhowl.howl.controllers;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.teamhowl.howl.models.ChatRoom;

import com.teamhowl.howl.R;

public class ChatRoomAdapter extends ArrayAdapter<ChatRoom> {

    public ChatRoomAdapter(Context context) {
        super(context, 0);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        // Get the data item for this position
        ChatRoom chatRoom = getItem(position);

        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {

            convertView = LayoutInflater.from(getContext()).inflate(
                    R.layout.cardview_chat_room,
                    parent,
                    false);
        }

        TextView userName = convertView.findViewById(R.id.user_name);
        TextView message = convertView.findViewById(R.id.message);

        userName.setText(chatRoom.getUser().getUserName());

        // Return the completed view to render on screen
        return convertView;
    }

}
