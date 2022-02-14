package com.teamhowl.howl.activities.ui.chatRoom;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class ChatRoomViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    public ChatRoomViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is home fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}