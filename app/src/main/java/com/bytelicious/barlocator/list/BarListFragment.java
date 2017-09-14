package com.bytelicious.barlocator.list;

import android.content.Context;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.bytelicious.barlocator.R;
import com.bytelicious.barlocator.model.Bar;

import java.util.ArrayList;

/**
 * @author ylyubenov
 */

public class BarListFragment extends Fragment implements BarListAdapter.OnBarItemClickedListener {

    protected ArrayList<Bar> bars;

    private RecyclerView barListRecyclerView;
    private ProgressBar loadingProgressBar;
    private BarListAdapter adapter;

    public static final String TITLE = "Bar List";

    public static BarListFragment newInstance() {
        return new BarListFragment();
    }

    public interface OnBarSelectedListener {
        void onBarSelectedListener(Bar bar);
    }

    private OnBarSelectedListener onBarSelectedListener;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Object host = getHost();
        if(host instanceof  OnBarSelectedListener) {
            onBarSelectedListener = (OnBarSelectedListener) host;
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_bar_list, container, false);
        bars = new ArrayList<>();
        initViews(view);
        setupRecyclerView();
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        barListRecyclerView = null;
        loadingProgressBar = null;
    }

    public void setBars(ArrayList<Bar> bars, Location location) {
        if (this.bars != null) {
            this.bars.clear();
        } else {
            this.bars = new ArrayList<>();
        }
        this.bars.addAll(bars);
        if (adapter != null) {
            adapter.setBars(bars, location);
        }
    }

    @Override
    public void onBarItemClicked(Bar bar) {
        if(onBarSelectedListener != null) {
            onBarSelectedListener.onBarSelectedListener(bar);
        }
    }

    private void initViews(View view) {
        barListRecyclerView = view.findViewById(R.id.bar_list_recycler_view);
        loadingProgressBar = view.findViewById(R.id.loading_progress_bar);
    }

    private void setupRecyclerView() {
        adapter = new BarListAdapter();
        adapter.setBars(bars, null);
        adapter.setClickedListener(this);
        barListRecyclerView.setAdapter(adapter);
        barListRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
    }
}