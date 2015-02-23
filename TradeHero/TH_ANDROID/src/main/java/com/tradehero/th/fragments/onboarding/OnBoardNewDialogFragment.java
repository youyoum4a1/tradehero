package com.tradehero.th.fragments.onboarding;

import android.os.Bundle;
import android.support.annotation.NonNull;
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
import com.tradehero.th.api.leaderboard.LeaderboardUserDTOList;
import com.tradehero.th.api.market.ExchangeDTOList;
import com.tradehero.th.api.market.ExchangeSectorListDTO;
import com.tradehero.th.api.market.SectorDTOList;
import com.tradehero.th.fragments.base.BaseDialogSupportFragment;
import com.tradehero.th.fragments.onboarding.exchange.ExchangeSelectionScreenFragment;
import com.tradehero.th.fragments.onboarding.hero.UserSelectionScreenFragment;
import com.tradehero.th.fragments.onboarding.sector.SectorSelectionScreenFragment;
import com.tradehero.th.rx.TimberOnErrorAction;
import rx.functions.Action1;
import rx.subjects.BehaviorSubject;

public class OnBoardNewDialogFragment extends BaseDialogSupportFragment
{
    private static final int INDEX_SELECTION_EXCHANGES = 0;
    private static final int INDEX_SELECTION_SECTORS = 1;
    private static final int INDEX_SELECTION_HEROES = 2;
    private static final int INDEX_SELECTION_WATCHLIST = 3;

    @InjectView(android.R.id.content) ViewPager pager;

    private ExchangeDTOList selectedExchanges;
    private SectorDTOList selectedSectors;
    private LeaderboardUserDTOList selectedHeroes;
    @NonNull private BehaviorSubject<ExchangeSectorListDTO> exchangeSectorBehavior;

    public static OnBoardNewDialogFragment showOnBoardDialog(FragmentManager fragmentManager)
    {
        OnBoardNewDialogFragment dialogFragment = new OnBoardNewDialogFragment();
        dialogFragment.show(fragmentManager, OnBoardNewDialogFragment.class.getName());
        return dialogFragment;
    }

    @Override public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        exchangeSectorBehavior = BehaviorSubject.create();
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
        if (selectedHeroes != null)
        {
            // TODO set to watchlist
        }
        else if (selectedSectors != null)
        {
            pager.setCurrentItem(INDEX_SELECTION_HEROES);
        }
        else if (selectedExchanges != null)
        {
            pager.setCurrentItem(INDEX_SELECTION_SECTORS);
        }
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
            return 3;
        }

        @Override public Fragment getItem(int position)
        {
            switch (position)
            {
                case INDEX_SELECTION_EXCHANGES:
                    ExchangeSelectionScreenFragment fragment0 = new ExchangeSelectionScreenFragment();
                    onStopSubscriptions.add(fragment0.getSelectedExchangesObservable()
                            .subscribe(
                                    new Action1<ExchangeDTOList>()
                                    {
                                        @Override public void call(ExchangeDTOList exchangeDTOs)
                                        {
                                            selectedExchanges = exchangeDTOs;
                                            pager.setCurrentItem(INDEX_SELECTION_SECTORS, true);
                                        }
                                    },
                                    new TimberOnErrorAction("Failed to collect exchange compacts")));
                    return fragment0;

                case INDEX_SELECTION_SECTORS:
                    SectorSelectionScreenFragment fragment1 = new SectorSelectionScreenFragment();
                    onStopSubscriptions.add(fragment1.getSelectedSectorsObservable()
                            .subscribe(
                                    new Action1<SectorDTOList>()
                                    {
                                        @Override public void call(SectorDTOList sectorDTOs)
                                        {
                                            selectedSectors = sectorDTOs;
                                            pager.setCurrentItem(INDEX_SELECTION_HEROES, true);
                                            exchangeSectorBehavior.onNext(new ExchangeSectorListDTO(selectedExchanges, sectorDTOs));
                                        }
                                    },
                                    new TimberOnErrorAction("Failed to collect sectors")));
                    return fragment1;

                case INDEX_SELECTION_HEROES:
                    UserSelectionScreenFragment fragment2 = new UserSelectionScreenFragment();
                    fragment2.setSelectedExchangesSectorsObservable(exchangeSectorBehavior.asObservable());
                    onStopSubscriptions.add(fragment2.getSelectedUsersObservable()
                            .subscribe(
                                    new Action1<LeaderboardUserDTOList>()
                                    {
                                        @Override public void call(LeaderboardUserDTOList leaderboardUserDTOs)
                                        {
                                            selectedHeroes = leaderboardUserDTOs;
                                            // TODO move to watchlist
                                        }
                                    },
                                    new TimberOnErrorAction("Failed to collect heroes")));
                    return fragment2;

                case INDEX_SELECTION_WATCHLIST:
                    // TODO
                    return null;
            }
            throw new IllegalArgumentException("Unknown position " + position);
        }
    }
}
