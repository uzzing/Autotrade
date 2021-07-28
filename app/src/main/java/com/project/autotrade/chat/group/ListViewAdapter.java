package com.project.autotrade.chat.group;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.project.autotrade.R;

import java.util.ArrayList;

public class ListViewAdapter extends BaseAdapter {

    private ArrayList<ListViewItem> listViewItems = new ArrayList<ListViewItem>();
    private TextView groupNameText;
    private ImageView groupUserImage;
    private TextView groupUserCountText;

    public ListViewAdapter(ArrayList<ListViewItem> listViewItems) {
        this.listViewItems = listViewItems;
    }

    @Override
    public int getCount() {
        return listViewItems.size();
    }

    @Override
    public Object getItem(int position) {
        return listViewItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        final Context context = parent.getContext();

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.activity_chat_grouplist, parent, false);
        }

        groupNameText = (TextView) convertView.findViewById(R.id.chat_grouplist_name);
        groupUserImage = (ImageView) convertView.findViewById(R.id.chat_grouplist_image);
        groupUserCountText = (TextView) convertView.findViewById(R.id.chat_grouplist_usercount);

        ListViewItem item = listViewItems.get(position);
        groupNameText.setText(item.getName());
        groupUserCountText.setText(item.getUserCount());

        return convertView;
    }
}
