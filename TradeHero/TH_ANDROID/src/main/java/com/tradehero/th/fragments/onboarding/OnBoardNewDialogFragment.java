package com.tradehero.th.fragments.onboarding;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import com.tradehero.th.R;
import com.tradehero.th.api.market.ExchangeCompactDTOList;
import com.tradehero.th.fragments.base.BaseDialogSupportFragment;
import com.tradehero.th.fragments.onboarding.exchange.ExchangeSelectionScreenFragment;
import com.tradehero.th.fragments.onboarding.sector.SectorSelectionScreenFragment;
import com.tradehero.th.rx.TimberOnErrorAction;
import rx.functions.Action1;

public class OnBoardNewDialogFragment extends BaseDialogSupportFragment
{
    @InjectView(android.R.id.content) ViewPager pager;

    private ExchangeCompactDTOList selectedExchanges;

    public static OnBoardNewDialogFragment showOnBoardDialog(FragmentManager fragmentManager)
    {
        OnBoardNewDialogFragment dialogFragment = new OnBoardNewDialogFragment();
        dialogFragment.show(fragmentManager, OnBoardNewDialogFragment.class.getName());
        return dialogFragment;
    }

    @Nullable @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.on_board_new_dialog, container, false);
    }

    @Override public void onViewCreated(View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.inject(this, view);
    }

    @Override public void onStart()
    {
        super.onStart();
        pager.setAdapter(new PagerAdapter(getChildFragmentManager()));
        pager.canScrollHorizontally(-1);
    }

    @Override public void onDestroyView()
    {
        ButterKnife.reset(this);
        super.onDestroyView();
    }

    @OnClick(android.R.id.closeButton)
    @Override public void dismiss()
    {
        super.dismiss();
    }

    private class PagerAdapter extends FragmentStatePagerAdapter
    {
        PagerAdapter(FragmentManager fm)
        {
            super(fm);
        }

        @Override public int getCount()
        {
            return 2;
        }

        @Override public Fragment getItem(int position)
        {
            switch (position)
            {
                case 0:
                    ExchangeSelectionScreenFragment fragment = new ExchangeSelectionScreenFragment();
                    onStopSubscriptions.add(fragment.getSelectedExchangesObservable()
                            .subscribe(
                                    new Action1<ExchangeCompactDTOList>()
                                    {
                                        @Override public void call(ExchangeCompactDTOList exchangeCompactDTOs)
                                        {
                                            selectedExchanges = exchangeCompactDTOs;
                                            pager.setCurrentItem(1, true);
                                        }
                                    },
                                    new TimberOnErrorAction("Failed to collect exchange compacts")));
                    return fragment;

                case 1:
                    return new SectorSelectionScreenFragment(); // TODO listen to clicks
            }
            throw new IllegalArgumentException("Unknown position " + position);
        }
    }
}
