package com.project.autotrade.mywallet;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.project.autotrade.R;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class RecentTradeAdapter extends RecyclerView.Adapter<RecentTradeAdapter.RecentTradeViewHolder>{

    private List<RecentTradeItem> recentTradeItems;

    public RecentTradeAdapter(List<RecentTradeItem> recentTradeItems) {
        this.recentTradeItems = recentTradeItems;
    }

    public class RecentTradeViewHolder extends RecyclerView.ViewHolder {

        private TextView recentTrade;

        public RecentTradeViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            recentTrade = (TextView) itemView.findViewById(R.id.recent_trade_box);
        }
    }

    @NonNull
    @NotNull
    @Override
    public RecentTradeViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup viewGroup, int viewType) {

        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.mywallet_box, viewGroup, false);

        return new RecentTradeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull RecentTradeAdapter.RecentTradeViewHolder recentTradeViewHolder, int position) {
        RecentTradeItem item = recentTradeItems.get(position);
        String result = item.getResult();

        recentTradeViewHolder.recentTrade.setText(result);
    }

    @Override
    public int getItemCount() {
        return recentTradeItems.size();
    }

}
