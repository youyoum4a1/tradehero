package com.tradehero.th.fragments.portfolio;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import com.tradehero.common.persistence.prefs.BooleanPreference;
import com.tradehero.common.utils.THToast;
import com.tradehero.th.R;
import com.tradehero.th.api.portfolio.OwnedPortfolioId;
import com.tradehero.th.api.portfolio.PortfolioCompactDTOList;
import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.fragments.base.BaseLiveFragmentUtil;
import com.tradehero.th.fragments.base.DashboardFragment;
import com.tradehero.th.fragments.position.PositionListFragment;
import com.tradehero.th.persistence.portfolio.PortfolioCompactListCacheRx;
import com.tradehero.th.persistence.prefs.IsLiveLogIn;
import com.tradehero.th.persistence.prefs.IsLiveTrading;
import com.tradehero.th.widget.OffOnViewSwitcherEvent;
import javax.inject.Inject;
import rx.functions.Action1;

public class PortfolioMainFragment extends DashboardFragment
{
    public static final int CODE_PROMPT = 1;
    private static final String USER_BASE_KEY_BUNDLE_KEY = PortfolioMainFragment.class.getName() + ".userBaseKey";

    @Inject @IsLiveTrading BooleanPreference isLiveTrading;
    @Inject CurrentUserId currentUserId;

    // TODO: For Dummy, please check is necessary for RELEASE
    @Inject @IsLiveLogIn BooleanPreference isLiveLogIn;
    @Inject protected PortfolioCompactListCacheRx portfolioCompactListCache;
    private OwnedPortfolioId myStockId;

    protected UserBaseKey shownUserBaseKey;
    private BaseLiveFragmentUtil liveFragmentUtil;

    @Override public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        shownUserBaseKey = getUserBaseKey(getArguments());
        if (shownUserBaseKey == null)
        {
            shownUserBaseKey = currentUserId.toUserBaseKey();
        }

        portfolioCompactListCache.get(shownUserBaseKey).subscribe(new Action1<Pair<UserBaseKey, PortfolioCompactDTOList>>()
        {
            @Override public void call(Pair<UserBaseKey, PortfolioCompactDTOList> userBaseKeyPortfolioCompactDTOListPair)
            {
                myStockId = userBaseKeyPortfolioCompactDTOListPair.second.getDefaultPortfolio().getOwnedPortfolioId();
            }
        });
    }

    @Nullable @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.fragment_portfolio_main, container, false);
    }

    @Override public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        super.onCreateOptionsMenu(menu, inflater);
        setActionBarTitle(R.string.dashboard_portfolios);
    }

    @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        liveFragmentUtil = BaseLiveFragmentUtil.createFor(this, view);

        if (isLiveTrading.get())
        {
            handleIsLive();
        }
        else
        {
            handleIsVirtual();
        }

        THToast.show(isLiveTrading.get().toString());
    }

    @Override public boolean shouldShowLiveTradingToggle()
    {
        return true;
    }

    @Override public void onLiveTradingChanged(OffOnViewSwitcherEvent event)
    {
        super.onLiveTradingChanged(event);
        if (event.isOn && event.isFromUser)
        {
            if (isLiveLogIn.get())
            {
                handleIsLive();
            }
            else
            {
                liveFragmentUtil.launchLiveLogin();
            }
        }
        else if (!event.isOn && event.isFromUser)
        {
            handleIsVirtual();
        }
    }

    @Override public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        liveFragmentUtil.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CODE_PROMPT && resultCode == Activity.RESULT_OK)
        {
            if (isLiveTrading.get())
            {
                handleIsLive();
            }
        }
    }

    @Nullable protected static UserBaseKey getUserBaseKey(@NonNull Bundle args)
    {
        Bundle userBundle = args.getBundle(USER_BASE_KEY_BUNDLE_KEY);
        if (userBundle != null)
        {
            return new UserBaseKey(userBundle);
        }
        return null;
    }

    private void handleIsVirtual()
    {
        Bundle args = new Bundle();
        Fragment created = Fragment.instantiate(getActivity(), PortfolioListFragment.class.getName(), args);
        getChildFragmentManager().beginTransaction().replace(R.id.portfolio_fragment_container, created).commitAllowingStateLoss();
    }

    private void handleIsLive()
    {
        Bundle args = new Bundle();

        if (myStockId.userId.equals(currentUserId.get()))
        {
            PositionListFragment.putApplicablePortfolioId(args, myStockId);
        }

        PositionListFragment.putGetPositionsDTOKey(args, myStockId);
        PositionListFragment.putShownUser(args, myStockId.getUserBaseKey());
        Fragment created = Fragment.instantiate(getActivity(), PositionListFragment.class.getName(), args);
        getChildFragmentManager().beginTransaction().replace(R.id.portfolio_fragment_container, created).commitAllowingStateLoss();
    }
}
