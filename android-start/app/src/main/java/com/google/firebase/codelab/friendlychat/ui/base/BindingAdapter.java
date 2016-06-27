package com.google.firebase.codelab.friendlychat.ui.base;

import android.support.v4.content.ContextCompat;
import android.text.TextUtils;

import com.bumptech.glide.Glide;
import com.google.firebase.codelab.friendlychat.R;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 *
 * Created by tuongpv on 6/19/2016.
 */
public class BindingAdapter  {

    @android.databinding.BindingAdapter("app:avatar")
    public static void setAvatar(CircleImageView view, String avatar) {

        if (TextUtils.isEmpty(avatar)) {
            view.setImageDrawable(ContextCompat.getDrawable(view.getContext(), R.drawable.ic_account_circle_black_36dp));
        } else {
            Glide.with(view.getContext())
                    .load(avatar)
                    .into(view);
        }
    }
}
