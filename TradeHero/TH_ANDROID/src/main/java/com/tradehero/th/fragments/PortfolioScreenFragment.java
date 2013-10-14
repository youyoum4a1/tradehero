package com.tradehero.th.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.actionbarsherlock.app.SherlockFragment;

@Deprecated
public class PortfolioScreenFragment extends SherlockFragment
{

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState)
    {
        // TODO Auto-generated method stub
        TextView mytext = new TextView(getActivity());
        mytext.setText("In Progress");
        mytext.setTextSize(30);
        container.addView(mytext);
        return super.onCreateView(inflater, container, savedInstanceState);
    }
}
