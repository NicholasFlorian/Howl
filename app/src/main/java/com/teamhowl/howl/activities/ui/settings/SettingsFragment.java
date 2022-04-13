package com.teamhowl.howl.activities.ui.settings;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.teamhowl.howl.databinding.FragmentSettingsBinding;
import com.teamhowl.howl.utilities.Settings;

public class SettingsFragment extends Fragment {

    private FragmentSettingsBinding binding;

    EditText editTextDeviceName;
    EditText editTextUserName;
    Switch switchNotification;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        SettingsViewModel settingsViewModel =
                new ViewModelProvider(this).get(SettingsViewModel.class);

        binding = FragmentSettingsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        //final TextView textView = binding.textNotifications;
        //settingsViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);

        editTextDeviceName = binding.editTextDeviceName;
        editTextUserName = binding.editTextUserName;
        switchNotification = binding.switchNotification;

        switchNotification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Settings.setNotification(getActivity(), switchNotification.isChecked());
            }
        });

        editTextUserName.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                Settings.setUserName(getActivity(), s.toString());
            }
        });

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onResume() {
        super.onResume();

        editTextDeviceName.setText(Settings.getDeviceName(getActivity()));
        editTextUserName.setText(Settings.getUserName(getActivity()));
        switchNotification.setChecked(Settings.getNotification(getActivity()));
    }
}