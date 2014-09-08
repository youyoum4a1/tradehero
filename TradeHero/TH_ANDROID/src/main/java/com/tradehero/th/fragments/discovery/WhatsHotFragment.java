package com.tradehero.th.fragments.discovery;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import butterknife.ButterKnife;
import com.tradehero.th.R;

public class WhatsHotFragment extends Fragment
{
    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.discovery_whatshot, container, false);
        ButterKnife.inject(view);
        return view;
    }
}
