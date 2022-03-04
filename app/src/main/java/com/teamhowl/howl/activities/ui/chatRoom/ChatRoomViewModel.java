package com.teamhowl.howl.activities.ui.chatRoom;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.teamhowl.howl.activities.ui.connect.ConnectFragment;
import com.teamhowl.howl.models.ChatRoom;
import com.teamhowl.howl.models.User;

import java.util.ArrayList;

public class ChatRoomViewModel extends ViewModel {

    private MediatorLiveData<ArrayList<ChatRoom>> connectRooms;

    public ChatRoomViewModel() {
        connectRooms = new MediatorLiveData<>();
        connectRooms.setValue(null);

        LiveData<ArrayList<ChatRoom>> rooms = null;

        connectRooms.addSource(rooms, connectRooms::setValue);
    }

    public LiveData<ArrayList<ChatRoom>> getChatRooms() {
        return connectRooms;
    }
}