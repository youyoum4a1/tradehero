package com.tradehero.th.fragments.chinabuild.fragment.test;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.tradehero.th2.R;
import com.tradehero.th.fragments.base.DashboardFragment;
import com.tradehero.th.fragments.billing.StoreScreenFragment;
import com.tradehero.th.fragments.contestcenter.ContestCenterFragment;
import com.tradehero.th.fragments.home.HomeFragment;
import com.tradehero.th.fragments.leaderboard.main.LeaderboardCommunityFragment;
import com.tradehero.th.fragments.settings.AdminSettingsFragment;
import com.tradehero.th.fragments.settings.SettingsFragment;
import com.tradehero.th.fragments.social.friend.FriendsInvitationFragment;
import com.tradehero.th.fragments.timeline.MeTimelineFragment;
import com.tradehero.th.fragments.trending.TrendingFragment;
import com.tradehero.th.fragments.updatecenter.UpdateCenterFragment;
import timber.log.Timber;

public class FragmentTest03 extends DashboardFragment
{
    @InjectView(R.id.tvTest00) TextView tvTest00;
    @InjectView(R.id.tvTest01) TextView tvTest01;
    @InjectView(R.id.tvTest02) TextView tvTest02;
    @InjectView(R.id.tvTest03) TextView tvTest03;
    @InjectView(R.id.tvTest04) TextView tvTest04;
    @InjectView(R.id.tvTest05) TextView tvTest05;
    @InjectView(R.id.tvTest06) TextView tvTest06;
    @InjectView(R.id.tvTest07) TextView tvTest07;
    @InjectView(R.id.tvTest08) TextView tvTest08;
    @InjectView(R.id.tvTest09) TextView tvTest09;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.test_fragment03, container, false);
        ButterKnife.inject(this, view);
        return view;
    }

    @OnClick({R.id.tvTest00,R.id.tvTest01,R.id.tvTest02,R.id.tvTest03,R.id.tvTest04,R.id.tvTest05,R.id.tvTest06,R.id.tvTest07,R.id.tvTest08,R.id.tvTest09})
    public void onTestClicked(View view)
    {
        switch (view.getId())
        {
            case R.id.tvTest00:
                //getDashboardNavigator().pushFragment(MeTimelineFragment.class,new Bundle());
                break;
            case R.id.tvTest01:
                getDashboardNavigator().pushFragment(HomeFragment.class,new Bundle());
                break;
            case R.id.tvTest02:
                getDashboardNavigator().pushFragment(TrendingFragment.class,new Bundle());
                break;
            case R.id.tvTest03:
                getDashboardNavigator().pushFragment(LeaderboardCommunityFragment.class,new Bundle());
                break;
            case R.id.tvTest04:
                getDashboardNavigator().pushFragment(UpdateCenterFragment.class,new Bundle());
                break;
            case R.id.tvTest05:
                getDashboardNavigator().pushFragment(FriendsInvitationFragment.class,new Bundle());
                break;
            case R.id.tvTest06:
                getDashboardNavigator().pushFragment(ContestCenterFragment.class,new Bundle());
                break;
            case R.id.tvTest07:
                getDashboardNavigator().pushFragment(StoreScreenFragment.class,new Bundle());
                break;
            case R.id.tvTest08:
                getDashboardNavigator().pushFragment(SettingsFragment.class,new Bundle());
                break;
            case R.id.tvTest09:
                getDashboardNavigator().pushFragment(AdminSettingsFragment.class,new Bundle());
                break;
        }

    }

    @Override public void onStop()
    {
        super.onStop();
    }

    @Override public void onDestroyView()
    {
        ButterKnife.reset(this);
        super.onDestroyView();
    }

    @Override public void onDestroy()
    {
        super.onDestroy();
    }

    @Override public void onResume()
    {
        super.onResume();
    }

}
