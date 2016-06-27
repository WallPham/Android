package com.google.firebase.codelab.friendlychat.ui.chat;

import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import com.google.firebase.codelab.friendlychat.databinding.ItemMessageBinding;
import com.google.firebase.codelab.friendlychat.model.FriendlyMessage;

/**
 * Created by tuongpv on 6/19/2016.
 */
public class ChatItemViewHolder extends RecyclerView.ViewHolder {

    private final ItemMessageBinding mBinding;

    private final ChatItemViewModel mViewModel;

    public ChatItemViewHolder(View itemView) {

        super(itemView);
        mViewModel = new ChatItemViewModel();
        mBinding = DataBindingUtil.bind(itemView);
        mBinding.setViewModel(mViewModel);
    }

    public void bind(FriendlyMessage message){

        mViewModel.bind(message);
    }
}