package com.project.autotrade.chat.Adapter;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.ArrayList;

public class GroupListAdapter extends BaseAdapter {

    ArrayList<GroupListItem> groupListItems = new ArrayList<>();

    public void addItem(GroupListItem item) {
        groupListItems.add(item);
    }

    @Override
    public int getCount() {
        return groupListItems.size();
    }

    @Override
    public Object getItem(int position) {
        return groupListItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {

        GroupListItemView itemView = null;

        if (view == null) {
            itemView = new GroupListItemView(view.getContext());
        }
        else {
            itemView = (GroupListItemView) view;
        }

        GroupListItem item = groupListItems.get(position);
        itemView.setGroupNameText(item.getName());
        itemView.setGroupUserCountText(item.getUserCount());

        return itemView;
    }
}
