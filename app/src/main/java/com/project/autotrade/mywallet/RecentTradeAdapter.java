package com.project.autotrade.mywallet;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.project.autotrade.R;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class RecentTradeAdapter extends BaseAdapter {

    private List<RecentTradeItem> recentTradeItems = new ArrayList<>();
    private TextView coinName, order, price, volume, createdAt;

    public RecentTradeAdapter(List<RecentTradeItem> recentTradeItems) {
        this.recentTradeItems = recentTradeItems;
    }

    @Override
    public int getCount() {
        return recentTradeItems.size();
    }

    @Override
    public Object getItem(int position) {
        return recentTradeItems.get(position);
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
            convertView = inflater.inflate(R.layout.mywallet_box, parent, false);
        }

        coinName = (TextView) convertView.findViewById(R.id.recent_trade_name);
        order = (TextView) convertView.findViewById(R.id.recent_trade_order);
        price = (TextView) convertView.findViewById(R.id.recent_trade_price);
        volume = (TextView) convertView.findViewById(R.id.recent_trade_volume);
        createdAt = (TextView) convertView.findViewById(R.id.recent_trade_createdAt);

        RecentTradeItem item = recentTradeItems.get(position);
        coinName.setText(item.getCoinName());
        order.setText(item.getOrder());
        price.setText(item.getPrice());
        volume.setText(item.getVolume());
        createdAt.setText(item.getCreatedAt());

        return convertView;
    }
}
