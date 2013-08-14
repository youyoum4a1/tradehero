package com.tradehero.th.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class CommunityScreenFragment extends Fragment
{

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState)
    {

        //for temporary
        TextView mytext = new TextView(getActivity());
        mytext.setText("In Progress");
        mytext.setTextSize(30);
        container.addView(mytext);
        return super.onCreateView(inflater, container, savedInstanceState);
    }
}
