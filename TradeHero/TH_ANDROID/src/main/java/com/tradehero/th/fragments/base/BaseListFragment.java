package com.tradehero.th.fragments.base;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.ListFragment;

import com.tradehero.th.utils.DaggerUtils;

public class BaseListFragment extends ListFragment {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        DaggerUtils.inject(this);
    }
}
