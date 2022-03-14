package com.teamhowl.howl.activities.ui.chatRoom;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.teamhowl.howl.activities.MessageActivity;
import com.teamhowl.howl.controllers.ChatRoomAdapter;
import com.teamhowl.howl.controllers.UserDao;
import com.teamhowl.howl.databinding.FragmentChatRoomBinding;
import com.teamhowl.howl.models.ChatRoom;
import com.teamhowl.howl.models.User;
import com.teamhowl.howl.repositories.UserRoomDatabase;

import java.util.ArrayList;

public class ChatRoomFragment extends Fragment {

    private ChatRoomAdapter chatRoomAdapter;
    private FragmentChatRoomBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        ChatRoomViewModel chatRoomViewModel =
                new ViewModelProvider(this).get(ChatRoomViewModel.class);

        binding = FragmentChatRoomBinding.inflate(inflater, container, false);
        chatRoomAdapter = new ChatRoomAdapter(getContext());

        ListView chatListView = binding.chatRoomListView;
        chatListView.setAdapter(chatRoomAdapter);

        // Add action when selecting a Device
        chatListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Intent intent = new Intent(getActivity(), MessageActivity.class);
                intent.putExtra(
                    MessageActivity.KEY_CHAT_ID,
                    chatRoomAdapter.getItem(position).getUser().getChatId());

                startActivity(intent);
            }
        });

        //final TextView textView = binding.textHome;
        //chatRoomViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onResume() {
        super.onResume();

        chatRoomAdapter.clear();

        UserRoomDatabase userDatabase =
                UserRoomDatabase.getDatabase(getActivity().getApplicationContext());
        UserDao userDao = userDatabase.userDao();

        for (User user : userDao.findAllUsers()) {

            ChatRoom chatRoom = new ChatRoom(user);
            chatRoomAdapter.add(chatRoom);
        }

        // TODO remove Mock
        chatRoomAdapter.add(new ChatRoom(new User("chatId", "userName")));
    }

}