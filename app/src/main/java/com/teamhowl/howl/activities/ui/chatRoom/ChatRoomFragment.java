package com.teamhowl.howl.activities.ui.chatRoom;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.teamhowl.howl.controllers.ChatRoomAdapter;
import com.teamhowl.howl.databinding.FragmentChatRoomBinding;
import com.teamhowl.howl.models.ChatRoom;

import java.util.ArrayList;

public class ChatRoomFragment extends Fragment {

    private ArrayList<ChatRoom> rooms;
    private ChatRoomAdapter chatAdapter;
    private ListView chatListView;
    private FragmentChatRoomBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        ChatRoomViewModel chatRoomViewModel =
                new ViewModelProvider(this).get(ChatRoomViewModel.class);

        binding = FragmentChatRoomBinding.inflate(inflater, container, false);

        rooms = new ArrayList<>();
        chatAdapter = new ChatRoomAdapter(getContext(), rooms);
        chatListView = binding.chatRoomListView;
        chatListView.setAdapter(chatAdapter);
        //chatListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
        //    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        //        requestConnection(chatAdapter.getItem((int)id));
        //    }
        //});

        //final TextView textView = binding.textHome;
        //chatRoomViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}