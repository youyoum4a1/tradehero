package com.tradehero.th.fragments.discussion;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.tradehero.th.R;

public class TimelineDiscussionFragment extends AbstractDiscussionFragment
{
    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.timeline_discussion, container, false);
        return view;
    }

    @Override public void onDestroyView()
    {
        super.onDestroyView();
    }

    @Override public boolean isTabBarVisible()
    {
        return false;
    }
}
