package com.teamhowl.howl.activities.ui.connect;

import android.app.Application;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
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

public class ConnectViewModel extends AndroidViewModel {

    private MutableLiveData<ArrayList<User>> localUsersObservable;

    public ConnectViewModel(Application application) {
        super(application);

        localUsersObservable = new MutableLiveData<>();
        localUsersObservable.setValue(null); // todo get local devices
    }

    public LiveData<ArrayList<User>> getLocalUsersObservable() {

        return localUsersObservable;
    }

}