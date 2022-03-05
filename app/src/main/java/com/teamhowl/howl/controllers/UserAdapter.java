package com.teamhowl.howl.controllers;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.teamhowl.howl.models.User;

import java.util.ArrayList;

import com.teamhowl.howl.R;

public class UserAdapter extends ArrayAdapter<User> {

    public UserAdapter(Context context) {
        super(context, 0);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        // Get the data item for this position
        User user = getItem(position);

        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {

            convertView = LayoutInflater.from(getContext()).inflate(
                    R.layout.cardview_user,
                    parent,
                    false);
        }

        // Lookup view for data population
        TextView userName = (TextView) convertView.findViewById(R.id.connect_user_name);
        TextView chatId = (TextView) convertView.findViewById(R.id.connect_chat_id);

        // Populate the data into the template view using the data object
        userName.setText(user.getUserName());
        chatId.setText(user.getChatId());

        // Return the completed view to render on screen
        return convertView;
    }

    public void setUsers(ArrayList<User> users){

        this.clear();
        this.addAll(users);
    }
}
