package com.google.firebase.codelab.friendlychat.ui.chat;

import com.google.firebase.codelab.friendlychat.model.FriendlyMessage;
import com.google.firebase.codelab.friendlychat.ui.base.BaseViewModel;
import android.databinding.ObservableField;

/**
 * Created by tuongpv on 6/19/16.
 */
public class ChatItemViewModel extends BaseViewModel {

    public ObservableField<String> avatar = new ObservableField<>();

    public ObservableField<String> name = new ObservableField<>();

    public ObservableField<String> text = new ObservableField<>();

    public ChatItemViewModel() {
    }

    public void bind(FriendlyMessage message) {

        name.set(message.getName());
        text.set(message.getText());
        avatar.set(message.getPhotoUrl());
    }
}