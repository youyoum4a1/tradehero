package com.androidth.general.fragments.position;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.androidth.general.R;
import com.androidth.general.fragments.base.DashboardFragment;

public class HistoryFragment extends DashboardFragment{
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.history_fragment,container,false);
        return v;
    }
}
