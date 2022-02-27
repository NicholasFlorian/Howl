package com.teamhowl.howl.activities.ui.connect;

import android.app.Application;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;

import com.teamhowl.howl.R;
import com.teamhowl.howl.controllers.UserAdapter;
import com.teamhowl.howl.models.User;

import java.util.ArrayList;
import java.util.List;

public class ConnectViewModel extends ViewModel {

    private MediatorLiveData<ArrayList<User>> connectUsers;

    public ConnectViewModel(ConnectFragment application) {
        super();

        connectUsers = new MediatorLiveData<>();

        // set by default null, until we get data from the database.
        connectUsers.setValue(null);
        LiveData<ArrayList<User>> users = null;//mRepository.getProducts();

        // observe the changes of the products from the database and forward them
        connectUsers.addSource(users, connectUsers::setValue);
    }

    public LiveData<ArrayList<User>> getUsers() {
        return connectUsers;
    }

}