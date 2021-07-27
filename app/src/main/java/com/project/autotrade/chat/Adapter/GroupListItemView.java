package com.project.autotrade.chat.Adapter;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.project.autotrade.R;

public class GroupListItemView extends LinearLayout {

    private TextView groupNameText;
    private ImageView groupUserImage;
    private TextView groupUserCountText;

    public GroupListItemView(Context context) {
        super(context);
        init(context);
    }

    public GroupListItemView(Context context, @Nullable AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    // initialize
    private void init(Context context) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        inflater.inflate(R.layout.activity_chat_grouplist, this, true);

        groupNameText = (TextView) findViewById(R.id.chat_grouplist_name);
        groupUserImage = (ImageView) findViewById(R.id.chat_grouplist_image);
        groupUserCountText = (TextView) findViewById(R.id.chat_grouplist_usercount);
    }

    // set text
    public void setGroupNameText(String name) {
        groupNameText.setText(name);
    }

    public void setGroupUserCountText(String userCount) {
        groupUserCountText.setText(userCount);
    }
}
