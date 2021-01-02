package com.example.cryptowatcher;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

public class PriceFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {
    public static SwipeRefreshLayout swipeAction;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.price_fragment, container, false);
        swipeAction = rootView.findViewById(R.id.swiperefresh);
        swipeAction.setOnRefreshListener(this);
        return rootView;
    }

    @Override
    public void onRefresh() {
        MainActivity.runningTask.execute();
    }
}