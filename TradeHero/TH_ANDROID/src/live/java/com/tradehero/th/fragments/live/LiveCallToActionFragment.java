package com.tradehero.th.fragments.live;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.tradehero.th.R;
import com.tradehero.th.activities.IdentityPromptActivity;
import com.tradehero.th.fragments.DashboardNavigator;
import com.tradehero.th.fragments.base.DashboardFragment;
import javax.inject.Inject;

public class LiveCallToActionFragment extends DashboardFragment
{
    @Inject DashboardNavigator navigator;

    @Nullable @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.fragment_live_action_screen, container, false);
    }

    @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.inject(this, view);
    }

    @Override public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        super.onCreateOptionsMenu(menu, inflater);
        setActionBarTitle(R.string.tradehero_live);
    }

    @Override public boolean shouldShowLiveTradingToggle()
    {
        return true;
    }

    @Override public void onLiveTradingChanged(boolean isLive)
    {
        super.onLiveTradingChanged(isLive);
        if (!isLive)
        {
            navigator.popFragment();
        }
    }

    @Override public void onDestroyView()
    {
        ButterKnife.reset(this);
        super.onDestroyView();
    }

    @OnClick(R.id.live_button_go_live)
    public void onGoLiveButtonClicked(View v)
    {
        navigator.launchActivity(IdentityPromptActivity.class);
    }

    @OnClick(R.id.live_button_later)
    public void onLaterButtonClicked(View v)
    {
        navigator.popFragment();
    }
}
