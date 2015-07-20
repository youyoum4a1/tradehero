package com.tradehero.th.fragments.live;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import butterknife.ButterKnife;
import butterknife.Bind;
import com.android.common.SlidingTabLayout;
import com.tradehero.th.R;
import com.tradehero.th.fragments.base.BaseFragment;
import com.tradehero.th.rx.TimberOnErrorAction;
import javax.inject.Inject;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;

public class LiveSignUpMainFragment extends BaseFragment
{
    @Inject SignUpLivePagerAdapterFactory signUpLivePagerAdapterFactory;
    @Inject Toolbar toolbar;

    @Bind(R.id.android_tabs) protected SlidingTabLayout tabLayout;
    @Bind(R.id.pager) protected ViewPager viewPager;

    @Override public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.settings_menu, menu);
        actionBarOwnerMixin.setCustomView(LayoutInflater.from(getActivity()).inflate(R.layout.sign_up_custom_actionbar, toolbar, false));
    }

    @Nullable @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.fragment_sign_up_live_main, container, false);
    }

    @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);

        tabLayout.setCustomTabView(R.layout.th_sign_up_tab_indicator, android.R.id.title);
        tabLayout.setDistributeEvenly(true);
        tabLayout.setSelectedIndicatorColors(getResources().getColor(R.color.tradehero_tab_indicator_color));

        onDestroyViewSubscriptions.add(
                signUpLivePagerAdapterFactory.create(getChildFragmentManager(), getArguments())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                new Action1<PagerAdapter>()
                                {
                                    @Override public void call(PagerAdapter pagerAdapter)
                                    {
                                        viewPager.setAdapter(pagerAdapter);
                                        tabLayout.setViewPager(viewPager);
                                    }
                                },
                                new TimberOnErrorAction("Failed to load pager")));
    }
}
