package com.tradehero.th.fragments.discussion.stock;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.tradehero.th.R;
import com.tradehero.th.fragments.base.DashboardFragment;

/**
 * Created by thonguyen on 4/4/14.
 */
public class SecurityDiscussionFragment extends DashboardFragment
{
    @InjectView(R.id.stock_discussion_view) SecurityDiscussionView securityDiscussionView;

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.stock_discussion, container, false);
        ButterKnife.inject(this, view);
        return view;
    }

    @Override public boolean isTabBarVisible()
    {
        return false;
    }
}
