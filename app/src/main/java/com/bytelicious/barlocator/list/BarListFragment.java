package com.bytelicious.barlocator.list;

import android.content.Context;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bytelicious.barlocator.R;
import com.bytelicious.barlocator.base.BarFragment;
import com.bytelicious.barlocator.model.Bar;

import java.util.ArrayList;

/**
 * @author ylyubenov
 */

public class BarListFragment extends BarFragment implements BarListAdapter.OnBarItemClickedListener {

    private RecyclerView barListRecyclerView;
    private BarListAdapter adapter;

    public static final String TITLE = "Bar List";

    public static BarListFragment newInstance() {
        return new BarListFragment();
    }

    public interface OnBarSelectedListener {
        void onBarSelectedListener(String barId);
    }

    private OnBarSelectedListener onBarSelectedListener;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Object host = getHost();
        if (host instanceof OnBarSelectedListener) {
            onBarSelectedListener = (OnBarSelectedListener) host;
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_bar_list, container, false);
        initViews(view);
        setupRecyclerView();
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        barListRecyclerView = null;
    }

    @Override
    public void setBars(ArrayList<Bar> bars, Location location) {
        super.setBars(bars, location);
        if (adapter != null) {
            adapter.setBars(bars, location);
        }
    }

    @Override
    public void onBarItemClicked(String barId) {
        if (onBarSelectedListener != null) {
            onBarSelectedListener.onBarSelectedListener(barId);
        }
    }

    private void initViews(View view) {
        barListRecyclerView = view.findViewById(R.id.bar_list_recycler_view);
    }

    private void setupRecyclerView() {
        adapter = new BarListAdapter();
        adapter.setBars(bars, location);
        adapter.setClickedListener(this);
        barListRecyclerView.setAdapter(adapter);
        barListRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
    }
}