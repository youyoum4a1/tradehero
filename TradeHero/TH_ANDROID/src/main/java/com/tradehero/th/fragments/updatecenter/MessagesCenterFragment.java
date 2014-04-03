package com.tradehero.th.fragments.updatecenter;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.tradehero.th.fragments.base.DashboardFragment;
import timber.log.Timber;

/**
 * Created by tradehero on 14-4-3.
 */
public class MessagesCenterFragment extends DashboardFragment
{
    @Override public boolean isTabBarVisible()
    {
        return false;
    }

    @Override public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        super.onCreateOptionsMenu(menu, inflater);
        Timber.d("MessagesCenterFragment onCreateOptionsMenu");
    }

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState)
    {
        TextView textView = new TextView(getActivity());
        textView.setText("Hello world");
        return textView;
    }

    private UpdateCenterFragment.TitleNumberCallback callback;

    public void setTitleNumberCallback(UpdateCenterFragment.TitleNumberCallback callback)
    {
        this.callback = callback;
    }
}
