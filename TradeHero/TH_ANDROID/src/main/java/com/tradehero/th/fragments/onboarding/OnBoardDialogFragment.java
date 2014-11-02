package com.tradehero.th.fragments.onboarding;

import android.app.FragmentManager;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Pair;
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
import com.tradehero.th.api.security.SecurityCompactDTOList;
import com.tradehero.th.api.security.SecurityIntegerIdListForm;
import com.tradehero.th.api.security.key.ExchangeSectorSecurityListType;
import com.tradehero.th.api.security.key.SecurityListType;
import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.api.users.SuggestHeroesListType;
import com.tradehero.th.api.users.UserBaseDTO;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.api.users.UserProfileDTO;
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
import com.tradehero.th.persistence.market.ExchangeSectorCompactListCacheRx;
import com.tradehero.th.persistence.prefs.FirstShowOnBoardDialog;
import com.tradehero.th.persistence.security.SecurityCompactListCacheRx;
import com.tradehero.th.persistence.timing.TimingIntervalPreference;
import com.tradehero.th.persistence.user.UserProfileCacheRx;
import com.tradehero.th.utils.broadcast.BroadcastUtils;
import dagger.Lazy;
import javax.inject.Inject;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import rx.Observer;
import rx.android.observables.AndroidObservable;
import rx.android.schedulers.AndroidSchedulers;

public class OnBoardDialogFragment extends BaseDialogFragment
{
    @Inject CurrentUserId currentUserId;
    @Inject UserServiceWrapper userServiceWrapper;
    @Inject WatchlistServiceWrapper watchlistServiceWrapper;
    @Inject @FirstShowOnBoardDialog TimingIntervalPreference firstShowOnBoardDialogPreference;
    @Inject UserProfileCacheRx userProfileCache;
    @Inject ExchangeSectorCompactListCacheRx exchangeSectorCompactListCache;
    @Inject LeaderboardUserListCache leaderboardUserListCache;
    @Inject Lazy<DashboardNavigator> navigator;
    @Inject BroadcastUtils broadcastUtils;

    @InjectView(R.id.next_button) Button nextButton;
    @InjectView(R.id.done_button) Button doneButton;
    @InjectView(R.id.exchange_switcher) ViewSwitcher mExchangeSwitcher;
    @InjectView(R.id.hero_switcher) ViewSwitcher mHeroSwitcher;
    @InjectView(R.id.stock_switcher) ViewSwitcher mStockSwitcher;

    //exchange
    @NotNull OnBoardPickExchangeSectorViewHolder exchangeSectorViewHolder;

    //hero
    @NotNull OnBoardPickHeroViewHolder heroViewHolder;
    @Nullable DTOCacheNew.Listener<SuggestHeroesListType, LeaderboardUserDTOList> leaderboardUserListCacheListener;

    //stock
    @Inject SecurityCompactListCacheRx securityCompactListCache;
    @NotNull OnBoardPickStockViewHolder stockViewHolder;

    public static OnBoardDialogFragment showOnBoardDialog(FragmentManager fragmentManager)
    {
        OnBoardDialogFragment dialogFragment = new OnBoardDialogFragment();
        dialogFragment.show(fragmentManager, OnBoardDialogFragment.class.getName());
        return dialogFragment;
    }

    @Override public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        //exchange
        exchangeSectorViewHolder = new OnBoardPickExchangeSectorViewHolder(getActivity());
        //hero
        heroViewHolder = new OnBoardPickHeroViewHolder(getActivity());
        leaderboardUserListCacheListener = new OnBoardPickHeroLeaderboardCacheListener();
        //stock
        stockViewHolder = new OnBoardPickStockViewHolder(getActivity());
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
        fetchUserProfile();
        fetchExchangeSectors();
    }

    protected void fetchUserProfile()
    {
        AndroidObservable.bindFragment(this, userProfileCache.get(currentUserId.toUserBaseKey()))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new OnBoardPickExchangeUserProfileObserver());
    }

    protected class OnBoardPickExchangeUserProfileObserver
        implements Observer<Pair<UserBaseKey, UserProfileDTO>>
    {
        @Override public void onNext(Pair<UserBaseKey, UserProfileDTO> pair)
        {
            exchangeSectorViewHolder.setUserProfile(pair.second);
        }

        @Override public void onCompleted()
        {
        }

        @Override public void onError(Throwable e)
        {
            THToast.show(R.string.error_fetch_your_user_profile);
        }
    }

    protected void fetchExchangeSectors()
    {
        ExchangeSectorCompactKey key = new ExchangeSectorCompactKey();
        AndroidObservable.bindFragment(
                this,
                exchangeSectorCompactListCache.get(key))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new OnBoardPickExchangeSectorObserver());
    }

    protected class OnBoardPickExchangeSectorObserver
            implements Observer<Pair<ExchangeSectorCompactKey, ExchangeSectorCompactListDTO>>
    {
        @Override public void onNext(
                Pair<ExchangeSectorCompactKey, ExchangeSectorCompactListDTO> pair)
        {
            exchangeSectorViewHolder.setExchangeSector(pair.second);
            mExchangeSwitcher.setDisplayedChild(1);
        }

        @Override public void onCompleted()
        {
        }

        @Override public void onError(Throwable e)
        {
            THToast.show(R.string.market_on_board_error_fetch_exchange_sector);
        }
    }

    @NotNull public OnBoardPrefDTO getOnBoardPrefs()
    {
        return exchangeSectorViewHolder.getOnBoardPrefs();
    }

    protected class OnBoardPickHeroLeaderboardCacheListener implements DTOCacheNew.Listener<SuggestHeroesListType, LeaderboardUserDTOList>
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
            AndroidObservable.bindFragment(this,
                    securityCompactListCache.get(exchangeSectorSecurityListType))
                    .subscribeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<Pair<SecurityListType, SecurityCompactDTOList>>()
                    {
                        @Override public void onCompleted()
                        {
                        }

                        @Override public void onError(Throwable e)
                        {
                            THToast.show(R.string.error_fetch_security_list_info);
                        }

                        @Override public void onNext(Pair<SecurityListType, SecurityCompactDTOList> pair)
                        {
                            mStockSwitcher.setDisplayedChild(1);
                            stockViewHolder.setStocks(pair.second);
                            submitHeroes();
                        }
                    });
        }
    }

    @Override public void onDestroyView()
    {
        detachLeaderboardUserListCache();
        exchangeSectorViewHolder.detachView();
        heroViewHolder.detachView();
        stockViewHolder.detachView();
        ButterKnife.reset(this);
        super.onDestroyView();
    }

    @Override public void onSaveInstanceState(@NotNull Bundle outState)
    {
        super.onSaveInstanceState(outState);
        detachLeaderboardUserListCache();
    }

    @Override public void onDismiss(DialogInterface dialog)
    {
        super.onDismiss(dialog);
        broadcastUtils.nextPlease();
    }

    @Override public void onDestroy()
    {
        leaderboardUserListCacheListener = null;
        super.onDestroy();
    }

    @SuppressWarnings("UnusedDeclaration")
    @OnClick(R.id.close)
    public void onCloseClicked(View view)
    {
        dismiss();
        firstShowOnBoardDialogPreference.justHandled();
    }

    @SuppressWarnings("UnusedDeclaration")
    @OnClick(R.id.done_button)
    public void onDoneClicked(View view)
    {
        dismiss();
        submitStockWatchlist();
        userProfileCache.invalidate(currentUserId.toUserBaseKey());
        navigator.get().goToTab(RootFragmentType.ME);
        firstShowOnBoardDialogPreference.justHandled();
    }

    public void submitHeroes()
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
                    new SecurityIntegerIdListForm(stocksList, null), null);
        }
    }

    @SuppressWarnings("UnusedDeclaration")
    @OnClick(R.id.next_button)
    public void onNextClicked(View view)
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
