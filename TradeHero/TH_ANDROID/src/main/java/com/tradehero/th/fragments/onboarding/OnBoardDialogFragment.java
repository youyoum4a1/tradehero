package com.tradehero.th.fragments.onboarding;

import android.app.FragmentManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ViewSwitcher;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import com.tradehero.common.persistence.DTOCacheNew;
import com.tradehero.common.utils.THToast;
import com.tradehero.th.R;
import com.tradehero.th.api.leaderboard.LeaderboardUserDTOList;
import com.tradehero.th.api.market.ExchangeSectorCompactListDTO;
import com.tradehero.th.api.security.SecurityCompactDTO;
import com.tradehero.th.api.security.SecurityCompactDTOList;
import com.tradehero.th.api.security.SecurityIntegerIdListForm;
import com.tradehero.th.api.security.key.ExchangeSectorSecurityListType;
import com.tradehero.th.api.security.key.SecurityListType;
import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.api.users.SuggestHeroesListType;
import com.tradehero.th.api.users.UserBaseDTO;
import com.tradehero.th.fragments.DashboardNavigator;
import com.tradehero.th.fragments.base.BaseDialogFragment;
import com.tradehero.th.fragments.dashboard.RootFragmentType;
import com.tradehero.th.fragments.onboarding.hero.OnBoardPickHeroViewHolder;
import com.tradehero.th.fragments.onboarding.pref.OnBoardPickExchangeSectorViewHolder;
import com.tradehero.th.fragments.onboarding.pref.OnBoardPrefDTO;
import com.tradehero.th.fragments.onboarding.stock.OnBoardPickStockViewHolder;
import com.tradehero.th.fragments.social.friend.BatchFollowFormDTO;
import com.tradehero.th.models.market.ExchangeSectorCompactKey;
import com.tradehero.th.network.service.UserServiceWrapper;
import com.tradehero.th.network.service.WatchlistServiceWrapper;
import com.tradehero.th.persistence.leaderboard.LeaderboardUserListCache;
import com.tradehero.th.persistence.market.ExchangeSectorCompactListCache;
import com.tradehero.th.persistence.prefs.FirstShowOnBoardDialog;
import com.tradehero.th.persistence.security.SecurityCompactListCache;
import com.tradehero.th.persistence.timing.TimingIntervalPreference;
import com.tradehero.th.persistence.user.UserProfileCache;
import javax.inject.Inject;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class OnBoardDialogFragment extends BaseDialogFragment
{
    @Inject CurrentUserId currentUserId;
    @Inject UserServiceWrapper userServiceWrapper;
    @Inject WatchlistServiceWrapper watchlistServiceWrapper;
    @Inject @FirstShowOnBoardDialog TimingIntervalPreference firstShowOnBoardDialogPreference;
    @Inject UserProfileCache userProfileCache;
    @Inject ExchangeSectorCompactListCache exchangeSectorCompactListCache;
    @Inject LeaderboardUserListCache leaderboardUserListCache;
    @Inject DashboardNavigator navigator;

    @InjectView(R.id.next_button) Button nextButton;
    @InjectView(R.id.done_button) Button doneButton;
    @InjectView(R.id.exchange_switcher) ViewSwitcher mExchangeSwitcher;
    @InjectView(R.id.hero_switcher) ViewSwitcher mHeroSwitcher;
    @InjectView(R.id.stock_switcher) ViewSwitcher mStockSwitcher;

    //exchange
    @NotNull OnBoardPickExchangeSectorViewHolder exchangeSectorViewHolder;
    @Nullable DTOCacheNew.Listener<ExchangeSectorCompactKey, ExchangeSectorCompactListDTO> exchangeSectorListener;

    //hero
    @NotNull OnBoardPickHeroViewHolder heroViewHolder;
    @Nullable DTOCacheNew.Listener<SuggestHeroesListType, LeaderboardUserDTOList> leaderboardUserListCacheListener;

    //stock
    @Inject SecurityCompactListCache securityCompactListCache;
    @NotNull OnBoardPickStockViewHolder stockViewHolder;
    @Nullable DTOCacheNew.Listener<SecurityListType, SecurityCompactDTOList> securityListCacheListener;

    public static OnBoardDialogFragment showOnBoardDialog(FragmentManager fragmentManager)
    {
        OnBoardDialogFragment dialogFragment = new OnBoardDialogFragment();
        dialogFragment.show(fragmentManager, OnBoardDialogFragment.class.getName());
        return dialogFragment;
    }

    @Override public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setStyle(BaseDialogFragment.STYLE_NO_TITLE, R.style.TH_onBoardDialog);
        //exchange
        exchangeSectorListener = new OnBoardPickExchangeSectorListener();
        exchangeSectorViewHolder = new OnBoardPickExchangeSectorViewHolder(getActivity());
        //hero
        heroViewHolder = new OnBoardPickHeroViewHolder(getActivity());
        leaderboardUserListCacheListener = new OnboardPickHeroLeaderboardCacheListener();
        //stock
        stockViewHolder = new OnBoardPickStockViewHolder(getActivity());
        securityListCacheListener = new OnBoardPickStockCacheListener();
    }

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.onboard_dialog, container, false);
    }

    @Override public void onViewCreated(View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.inject(this, view);
        exchangeSectorViewHolder.attachView(view);
        heroViewHolder.attachView(view);
        stockViewHolder.attachView(view);
        fetchExchangeSectors();
    }

    protected void fetchExchangeSectors()
    {
        detachExchangeSectorCompactListCache();
        ExchangeSectorCompactKey key = new ExchangeSectorCompactKey();
        exchangeSectorCompactListCache.register(key, exchangeSectorListener);
        exchangeSectorCompactListCache.getOrFetchAsync(key);
    }

    protected void detachExchangeSectorCompactListCache()
    {
        exchangeSectorCompactListCache.unregister(exchangeSectorListener);
    }

    protected class OnBoardPickExchangeSectorListener
            implements DTOCacheNew.Listener<ExchangeSectorCompactKey, ExchangeSectorCompactListDTO>
    {
        @Override public void onDTOReceived(@NotNull ExchangeSectorCompactKey key, @NotNull ExchangeSectorCompactListDTO value)
        {
            exchangeSectorViewHolder.setExchangeSector(value);
            mExchangeSwitcher.setDisplayedChild(1);
        }

        @Override public void onErrorThrown(@NotNull ExchangeSectorCompactKey key, @NotNull Throwable error)
        {
            THToast.show(R.string.market_on_board_error_fetch_exchange_sector);
        }
    }

    @NotNull public OnBoardPrefDTO getOnBoardPrefs()
    {
        return exchangeSectorViewHolder.getOnBoardPrefs();
    }

    protected class OnboardPickHeroLeaderboardCacheListener implements DTOCacheNew.Listener<SuggestHeroesListType, LeaderboardUserDTOList>
    {
        @Override public void onDTOReceived(@NotNull SuggestHeroesListType key, @NotNull LeaderboardUserDTOList value)
        {
            mHeroSwitcher.setDisplayedChild(1);
            heroViewHolder.setUsers(value);
        }

        @Override public void onErrorThrown(@NotNull SuggestHeroesListType key, @NotNull Throwable error)
        {
            THToast.show(R.string.error_fetch_leaderboard_info);
        }
    }

    protected void fetchSuggestedUsers(ExchangeSectorSecurityListType exchangeSectorSecurityListType)
    {
        if (exchangeSectorSecurityListType != null)
        {
            SuggestHeroesListType key = new SuggestHeroesListType(
                    exchangeSectorSecurityListType.exchangeId,
                    exchangeSectorSecurityListType.sectorId,
                    1, null);
            detachLeaderboardUserListCache();
            leaderboardUserListCache.register(
                    key,
                    leaderboardUserListCacheListener);
            leaderboardUserListCache.getOrFetchAsync(key);
        }
    }

    protected void detachLeaderboardUserListCache()
    {
        leaderboardUserListCache.unregister(leaderboardUserListCacheListener);
    }

    protected void fetchExchangeSectorSecurities(ExchangeSectorSecurityListType exchangeSectorSecurityListType)
    {
        if (exchangeSectorSecurityListType != null)
        {
            detachSecurityListCache();
            securityCompactListCache.register(exchangeSectorSecurityListType, securityListCacheListener);
            securityCompactListCache.getOrFetchAsync(exchangeSectorSecurityListType);
        }
    }

    protected void detachSecurityListCache()
    {
        securityCompactListCache.unregister(securityListCacheListener);
    }

    protected class OnBoardPickStockCacheListener implements DTOCacheNew.Listener<SecurityListType, SecurityCompactDTOList>
    {
        @Override public void onDTOReceived(@NotNull SecurityListType key, @NotNull SecurityCompactDTOList value)
        {
            //Timber.d("lyl stock "+value.toString());
            mStockSwitcher.setDisplayedChild(1);
            stockViewHolder.setStocks(value);
            submitHeros();
        }

        @Override public void onErrorThrown(@NotNull SecurityListType key, @NotNull Throwable error)
        {
            THToast.show(R.string.error_fetch_security_list_info);
        }
    }

    @Override public void onDestroyView()
    {
        detachExchangeSectorCompactListCache();
        detachLeaderboardUserListCache();
        detachSecurityListCache();
        exchangeSectorViewHolder.detachView();
        heroViewHolder.detachView();
        stockViewHolder.detachView();
        ButterKnife.reset(this);
        super.onDestroyView();
    }

    @SuppressWarnings("UnusedDeclaration")
    @OnClick(R.id.close)
    public void onCloseClicked(/*View view*/)
    {
        dismiss();
        firstShowOnBoardDialogPreference.justHandled();
    }

    @SuppressWarnings("UnusedDeclaration")
    @OnClick(R.id.done_button)
    public void onDoneClicked(/*View view*/)
    {
        dismiss();
        submitStockWatchlist();
        userProfileCache.invalidate(currentUserId.toUserBaseKey());
        navigator.goToTab(RootFragmentType.CONTEST_CENTER);
        navigator.goToTab(RootFragmentType.ME);
        firstShowOnBoardDialogPreference.justHandled();
    }

    public void submitHeros()
    {
        // Follow heroes if any
        LeaderboardUserDTOList heroesList = heroViewHolder.getSelectedHeroes();
        if (!heroesList.isEmpty())
        {
            userServiceWrapper.followBatchFree(
                    new BatchFollowFormDTO(heroesList, new UserBaseDTO()), null);
        }
    }

    public void submitStockWatchlist()
    {
        // Watch stocks if any
        SecurityCompactDTOList stocksList = stockViewHolder.getSelectedStocks();
        if (!stocksList.isEmpty())
        {
            watchlistServiceWrapper.batchCreate(
                    new SecurityIntegerIdListForm(stocksList, (SecurityCompactDTO) null), null);
        }
    }

    @SuppressWarnings("UnusedDeclaration")
    @OnClick(R.id.next_button)
    public void onNextClicked(/*View view*/)
    {
        if (mHeroSwitcher.getDisplayedChild() == 1)
        {
            mHeroSwitcher.setVisibility(View.GONE);
            mStockSwitcher.setVisibility(View.VISIBLE);
            mStockSwitcher.setDisplayedChild(1);
            fetchExchangeSectorSecurities(getOnBoardPrefs().createExchangeSectorSecurityListType());
            if (nextButton != null)
            {
                nextButton.setVisibility(View.GONE);
            }
            if (doneButton != null)
            {
                doneButton.setVisibility(View.VISIBLE);
            }
        }
        else if (mExchangeSwitcher.getDisplayedChild() == 1)
        {
            mExchangeSwitcher.setVisibility(View.GONE);
            mHeroSwitcher.setVisibility(View.VISIBLE);
            mHeroSwitcher.setDisplayedChild(1);
            fetchSuggestedUsers(getOnBoardPrefs().createExchangeSectorSecurityListType());
        }
    }
}
