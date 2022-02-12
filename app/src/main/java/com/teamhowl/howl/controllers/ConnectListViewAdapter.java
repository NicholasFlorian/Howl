package com.teamhowl.howl.controllers;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.teamhowl.howl.models.User;

import java.util.ArrayList;

public class ConnectListViewAdapter extends ArrayAdapter<User> {

    public ConnectListViewAdapter(Context context, ArrayList<User> users) {

        super(context, 0, users);

    }

    @Override

    public View getView(int position, View convertView, ViewGroup parent) {

        // Get the data item for this position

        User user = getItem(position);

        // Check if an existing view is being reused, otherwise inflate the view

        if (convertView == null) {

            convertView = LayoutInflater.from(getContext()).inflate(R.layout.connect_card_view, parent, false);

        }

        // Lookup view for data population

        TextView tvName = (TextView) convertView.findViewById(R.id.connectUserNameTextView);

        TextView tvHome = (TextView) convertView.findViewById(R.id.tvHome);

        // Populate the data into the template view using the data object

        tvName.setText(user.name);

        tvHome.setText(user.hometown);

        // Return the completed view to render on screen

        return convertView;

    }

}