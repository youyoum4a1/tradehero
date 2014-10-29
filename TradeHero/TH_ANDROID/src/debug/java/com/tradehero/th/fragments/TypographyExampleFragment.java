package com.tradehero.th.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.etiennelawlor.quickreturn.library.views.NotifyingScrollView;
import com.tradehero.th.R;
import com.tradehero.th.fragments.base.DashboardFragment;
import javax.inject.Inject;

public class TypographyExampleFragment extends DashboardFragment
{
    @Inject Context context;
    @InjectView(R.id.typography_scroll) NotifyingScrollView scrollView;

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.fragment_typography_style_list, container, false);
    }

    @Override public void onViewCreated(View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.inject(this, view);
        scrollView.setOnScrollChangedListener(dashboardBottomTabScrollViewScrollListener.get());
    }
}
