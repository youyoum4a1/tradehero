package com.tradehero.th.ui;

import android.app.Activity;
import android.view.ViewGroup;
import com.tradehero.th.R;

import javax.inject.Inject;

import static butterknife.ButterKnife.findById;

public class AppContainerImpl implements AppContainer
{

    @Inject public AppContainerImpl()
    {
    }

    @Override public ViewGroup get(final Activity activity)
    {
        activity.setContentView(R.layout.dashboard_with_bottom_bar);
        return findById(activity, android.R.id.content);
    }

}
