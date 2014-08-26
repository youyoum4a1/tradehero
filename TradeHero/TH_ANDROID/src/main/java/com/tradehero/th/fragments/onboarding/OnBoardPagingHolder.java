package com.tradehero.th.fragments.onboarding;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.view.View;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import com.tradehero.th.R;
import com.tradehero.th.api.leaderboard.LeaderboardUserDTOList;
import com.tradehero.th.api.security.SecurityCompactDTO;
import com.tradehero.th.api.security.SecurityCompactDTOList;
import com.tradehero.th.api.security.SecurityIntegerIdListForm;
import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.api.users.UpdateCountryCodeFormDTO;
import com.tradehero.th.api.users.UserBaseDTO;
import com.tradehero.th.fragments.onboarding.hero.OnBoardPickHeroFragment;
import com.tradehero.th.fragments.onboarding.pref.OnBoardPickExchangeSectorFragment;
import com.tradehero.th.fragments.onboarding.pref.OnBoardPrefDTO;
import com.tradehero.th.fragments.onboarding.stock.OnBoardPickStockFragment;
import com.tradehero.th.fragments.social.friend.BatchFollowFormDTO;
import com.tradehero.th.network.service.UserServiceWrapper;
import com.tradehero.th.network.service.WatchlistServiceWrapper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class OnBoardPagingHolder implements ViewPager.OnPageChangeListener
{
    @InjectView(R.id.next_button) View nextButton;
    @InjectView(R.id.done_button) View doneButton;
    @InjectView(R.id.pager) ViewPager pager;
    @NotNull OnBoardFragmentPagerAdapter pagerAdapter;
    @NotNull CurrentUserId currentUserId;
    @NotNull UserServiceWrapper userServiceWrapper;
    @NotNull WatchlistServiceWrapper watchlistServiceWrapper;
    @Nullable OnBoardPrefDTO onBoardPrefDTO;
    @Nullable LeaderboardUserDTOList selectedHeroes;
    @Nullable SecurityCompactDTOList selectedStocks;

    //<editor-fold desc="Constructors">
    public OnBoardPagingHolder(
            @NotNull FragmentManager fm,
            @NotNull CurrentUserId currentUserId,
            @NotNull UserServiceWrapper userServiceWrapper,
            @NotNull WatchlistServiceWrapper watchlistServiceWrapper)
    {
        super();
        pagerAdapter = new OnBoardFragmentPagerAdapter(fm);
        this.currentUserId = currentUserId;
        this.userServiceWrapper = userServiceWrapper;
        this.watchlistServiceWrapper = watchlistServiceWrapper;
    }
    //</editor-fold>

    void attachView(View view)
    {
        ButterKnife.inject(this, view);
        pager.setAdapter(pagerAdapter);
        pager.setOnPageChangeListener(this);
        pager.setCurrentItem(0, false);
    }

    void detachView()
    {
        pager.setOnPageChangeListener(null);
        ButterKnife.reset(this);
    }

    @OnClick(R.id.next_button)
    public void onNextClicked(/*View view*/)
    {
        showNext();
    }

    void showNext()
    {
        pager.setCurrentItem(pager.getCurrentItem() + 1, true);
    }

    @Override public void onPageScrolled(int i, float v, int i2)
    {
    }

    @Override public void onPageSelected(int position)
    {
        Fragment currentFragment = pagerAdapter.getFragmentAt(position);
        switch (position)
        {
            case OnBoardFragmentPagerAdapter.FRAGMENT_PREF:
                break;

            case OnBoardFragmentPagerAdapter.FRAGMENT_HERO:
                this.onBoardPrefDTO = ((OnBoardPickExchangeSectorFragment)
                        pagerAdapter.getFragmentAt(OnBoardFragmentPagerAdapter.FRAGMENT_PREF))
                        .getOnBoardPrefs();
                ((OnBoardPickHeroFragment) currentFragment)
                        .setExchangeSectorSecurityListType(
                                onBoardPrefDTO.createExchangeSectorSecurityListType());
                break;

            case OnBoardFragmentPagerAdapter.FRAGMENT_STOCK:
                selectedHeroes = ((OnBoardPickHeroFragment)
                        pagerAdapter.getFragmentAt(OnBoardFragmentPagerAdapter.FRAGMENT_HERO))
                        .getSelectedHeroes();
                //noinspection ConstantConditions
                ((OnBoardPickStockFragment) currentFragment)
                        .setExchangeSectorSecurityListType(
                                onBoardPrefDTO.createExchangeSectorSecurityListType());
                break;
        }
        boolean lastPage = position == (pagerAdapter.getCount() - 1);
        nextButton.setVisibility(lastPage ? View.GONE : View.VISIBLE);
        doneButton.setVisibility(lastPage ? View.VISIBLE : View.GONE);
    }

    @Override public void onPageScrollStateChanged(int i)
    {
    }

    public void wrapUpAndSubmitSelection()
    {
        // Update country
        //noinspection ConstantConditions
        userServiceWrapper.updateCountryCode(
                currentUserId.toUserBaseKey(),
                new UpdateCountryCodeFormDTO(onBoardPrefDTO.preferredCountry.name()), null);

        // Follow heroes if any
        if (selectedHeroes != null && !selectedHeroes.isEmpty())
        {
            userServiceWrapper.followBatchFree(
                    new BatchFollowFormDTO(
                            selectedHeroes,
                            new UserBaseDTO()),
                    null);
        }

        // Watch stocks if any
        selectedStocks = ((OnBoardPickStockFragment)
                pagerAdapter.getFragmentAt(OnBoardFragmentPagerAdapter.FRAGMENT_STOCK))
                .getSelectedStocks();
        if (selectedStocks != null && !selectedStocks.isEmpty())
        {
            watchlistServiceWrapper.batchCreate(
                    new SecurityIntegerIdListForm(selectedStocks, (SecurityCompactDTO) null),
                    null);
        }
    }
}
