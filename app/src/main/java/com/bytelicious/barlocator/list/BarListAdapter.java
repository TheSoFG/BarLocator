package com.bytelicious.barlocator.list;

import android.location.Location;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bytelicious.barlocator.R;
import com.bytelicious.barlocator.Utils;
import com.bytelicious.barlocator.model.Bar;

import java.util.ArrayList;

/**
 * @author ylyubenov
 */

class BarListAdapter extends RecyclerView.Adapter<BarListAdapter.BarViewHolder> {

    private ArrayList<Bar> bars;

    private Location location;

    private OnBarItemClickedListener clickedListener;

    public void setClickedListener(OnBarItemClickedListener clickedListener) {
        this.clickedListener = clickedListener;
    }

    public interface OnBarItemClickedListener {
        void onBarItemClicked(Bar bar);
    }

    void setBars(ArrayList<Bar> bars, Location newLocation) {
        if (this.bars == null) {
            this.bars = new ArrayList<>();
        }
        this.bars.clear();
        this.bars.addAll(bars);
        this.location = newLocation;
        notifyDataSetChanged();
    }

    @Override
    public BarListAdapter.BarViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        return new BarViewHolder(inflater.inflate(R.layout.item_bar, parent, false));
    }

    @Override
    public void onBindViewHolder(BarListAdapter.BarViewHolder holder, int position) {
        holder.bind(bars.get(position));
    }

    @Override
    public int getItemCount() {
        return bars.size();
    }

    class BarViewHolder extends RecyclerView.ViewHolder {

        private RelativeLayout barItemLayout;
        private TextView distanceTextView;
        private TextView nameTextView;

        BarViewHolder(View itemView) {
            super(itemView);
            distanceTextView = itemView.findViewById(R.id.distance_text_view);
            barItemLayout = itemView.findViewById(R.id.bar_item_layout);
            nameTextView = itemView.findViewById(R.id.name_text_view);
        }

        void bind(final Bar bar) {
            nameTextView.setText(bar.getName());
            if (location != null) {
                float[] results = new float[3];
                Location.distanceBetween(location.getLatitude(), location.getLongitude(),
                        bar.getGeometry().getLocation().getLat(),
                        bar.getGeometry().getLocation().getLng(), results);
                String baseString = distanceTextView.getContext().getString(R.string.bar_distance);
                distanceTextView.setText(String.format(baseString, Utils.round(results[0], 2) + ""));
            } else {
                distanceTextView.setText("");
            }
            barItemLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (clickedListener != null) {
                        clickedListener.onBarItemClicked(bar);
                    }
                }
            });
        }

    }

}
