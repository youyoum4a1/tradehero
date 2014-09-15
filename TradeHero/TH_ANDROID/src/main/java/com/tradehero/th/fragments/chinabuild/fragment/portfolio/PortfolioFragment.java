package com.tradehero.th.fragments.chinabuild.fragment.portfolio;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.tradehero.common.persistence.DTOCacheNew;
import com.tradehero.common.utils.THToast;
import com.tradehero.th.adapters.MyTradePositionListAdapter;
import com.tradehero.th.api.leaderboard.position.LeaderboardMarkUserId;
import com.tradehero.th.api.portfolio.OwnedPortfolioId;
import com.tradehero.th.api.portfolio.PortfolioCompactDTO;
import com.tradehero.th.api.position.GetPositionsDTO;
import com.tradehero.th.api.position.GetPositionsDTOKey;
import com.tradehero.th.api.position.PositionDTO;
import com.tradehero.th.api.security.SecurityCompactDTO;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.fragments.base.DashboardFragment;
import com.tradehero.th.fragments.chinabuild.data.SecurityPositionItem;
import com.tradehero.th.fragments.chinabuild.listview.SecurityListView;
import com.tradehero.th.models.portfolio.DisplayablePortfolioFetchAssistant;
import com.tradehero.th.persistence.portfolio.PortfolioCompactListCache;
import com.tradehero.th.persistence.position.GetPositionsCache;
import com.tradehero.th.persistence.user.UserProfileCache;
import com.tradehero.th2.R;
import dagger.Lazy;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Provider;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import timber.log.Timber;

/**
 * Created by huhaiping on 14-9-14. 个人持仓页。比赛持仓页
 */
public class PortfolioFragment extends DashboardFragment
{

    public static final String BUNDLE_LEADERBOARD_USER_MARK_ID = "bundle_leaderboard_user_mark_id";
    public static final String BUNLDE_SHOW_PROFILE_USER_ID = "bundle_show_profile_user_id";
    public long leaderboardUserMarkId = 0;//通过比赛排名进入比赛持仓
    public int portfolioUserKey = 0;//通过查看他人主账户进入持仓，需要知道UserID

    @Inject Lazy<GetPositionsCache> getPositionsCache;
    @Nullable protected DTOCacheNew.Listener<GetPositionsDTOKey, GetPositionsDTO> fetchGetPositionsDTOListener;
    protected GetPositionsDTOKey getPositionsDTOKey;//

    @Inject Lazy<UserProfileCache> userProfileCache;
    private DTOCacheNew.Listener<UserBaseKey, UserProfileDTO> userProfileCacheListener;
    private UserBaseKey showUserBaseKey;
    @Inject Lazy<PortfolioCompactListCache> portfolioCompactListCache;

    private DisplayablePortfolioFetchAssistant displayablePortfolioFetchAssistant;
    @Inject Provider<DisplayablePortfolioFetchAssistant> displayablePortfolioFetchAssistantProvider;

    @InjectView(R.id.listPortfilio) SecurityListView listView;
    private MyTradePositionListAdapter adapter;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        fetchGetPositionsDTOListener = createGetPositionsCacheListener();
        userProfileCacheListener = createUserProfileCacheListener();
        initArgment();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        super.onCreateOptionsMenu(menu, inflater);
        setHeadViewMiddleMain("TA的持仓");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.portfolio_layout, container, false);
        ButterKnife.inject(this, view);

        displayablePortfolioFetchAssistant = displayablePortfolioFetchAssistantProvider.get();
        displayablePortfolioFetchAssistant.setFetchedListener(
                new DisplayablePortfolioFetchAssistant.OnFetchedListener()
                {
                    @Override public void onFetched()
                    {
                        getDefaultPortfolio();
                    }
                });

        adapter = new MyTradePositionListAdapter(getActivity());
        listView.setAdapter(adapter);
        listView.setMode(PullToRefreshBase.Mode.DISABLED);

        return view;
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
        fetchGetPositionsDTOListener = null;
        super.onDestroy();
    }

    @Override public void onResume()
    {
        super.onResume();
        fetchSimplePage(false);
        //displayablePortfolioFetchAssistant.fetch(getUserBaseKeys());
    }

    public void initArgment()
    {
        Bundle bundle = getArguments();
        if (bundle != null)
        {
            leaderboardUserMarkId = bundle.getLong(BUNDLE_LEADERBOARD_USER_MARK_ID, 0);
            portfolioUserKey = bundle.getInt(BUNLDE_SHOW_PROFILE_USER_ID, 0);
            if (leaderboardUserMarkId != 0)
            {
                //THToast.show("leaderboardUserMarkId:  " + leaderboardUserMarkId);
                getPositionsDTOKey = new LeaderboardMarkUserId((int) leaderboardUserMarkId);
            }
            else if (portfolioUserKey != 0)
            {
                showUserBaseKey = new UserBaseKey(portfolioUserKey);
                getDefaultPortfolio();
            }
        }
    }

    private void getDefaultPortfolio()
    {
        PortfolioCompactDTO defaultPortfolio = portfolioCompactListCache.get().getDefaultPortfolio(showUserBaseKey);
        if (defaultPortfolio != null)
        {
            getPositionsDTOKey = new OwnedPortfolioId(showUserBaseKey.key, defaultPortfolio.id);
            fetchSimplePage(false);
        }
        else
        {
            refreshPortfolioList();
        }
    }

    private void refreshPortfolioList()
    {
        displayablePortfolioFetchAssistant = displayablePortfolioFetchAssistantProvider.get();
        portfolioCompactListCache.get().invalidate(showUserBaseKey);
        displayablePortfolioFetchAssistant.fetch(getUserBaseKeys());
    }

    protected List<UserBaseKey> getUserBaseKeys()
    {
        List<UserBaseKey> list = new ArrayList<>();
        list.add(showUserBaseKey);
        return list;
    }

    @NotNull protected DTOCacheNew.Listener<GetPositionsDTOKey, GetPositionsDTO> createGetPositionsCacheListener()
    {
        return new GetPositionsListener();
    }

    private void linkWith(GetPositionsDTO value, boolean display)
    {
        Timber.d("");
        initPositionSecurity(value);
    }

    protected class GetPositionsListener
            implements DTOCacheNew.HurriedListener<GetPositionsDTOKey, GetPositionsDTO>
    {
        @Override public void onPreCachedDTOReceived(
                @NotNull GetPositionsDTOKey key,
                @NotNull GetPositionsDTO value)
        {
            linkWith(value, true);
            //showResultIfNecessary();
        }

        @Override public void onDTOReceived(
                @NotNull GetPositionsDTOKey key,
                @NotNull GetPositionsDTO value)
        {
            linkWith(value, true);
            //showResultIfNecessary();
        }

        @Override public void onErrorThrown(
                @NotNull GetPositionsDTOKey key,
                @NotNull Throwable error)
        {
            Timber.d(error, "Error fetching the positionList info %s", key);
        }
    }

    protected void detachGetPositionsTask()
    {
        getPositionsCache.get().unregister(fetchGetPositionsDTOListener);
    }

    protected void fetchSimplePage(boolean force)
    {
        if (getPositionsDTOKey != null && getPositionsDTOKey.isValid())
        {
            detachGetPositionsTask();
            getPositionsCache.get().register(getPositionsDTOKey, fetchGetPositionsDTOListener);
            getPositionsCache.get().getOrFetchAsync(getPositionsDTOKey, force);
        }
    }

    protected void detachUserProfileCache()
    {
        userProfileCache.get().unregister(userProfileCacheListener);
    }

    protected void fetchUserProfile(boolean force)
    {
        detachUserProfileCache();
        userProfileCache.get().register(showUserBaseKey, userProfileCacheListener);
        userProfileCache.get().getOrFetchAsync(showUserBaseKey, force);
    }

    private void linkWith(UserProfileDTO value)
    {
        Timber.d("UserProfileDTO 获取成功！");
    }

    protected DTOCacheNew.Listener<UserBaseKey, UserProfileDTO> createUserProfileCacheListener()
    {
        return new TimelineFragmentUserProfileCacheListener();
    }

    protected class TimelineFragmentUserProfileCacheListener implements DTOCacheNew.Listener<UserBaseKey, UserProfileDTO>
    {
        @Override public void onDTOReceived(@NotNull UserBaseKey key, @NotNull UserProfileDTO value)
        {
            linkWith(value);
        }

        @Override public void onErrorThrown(@NotNull UserBaseKey key, @NotNull Throwable error)
        {
            THToast.show(getString(R.string.error_fetch_user_profile));
        }
    }

    private void initPositionSecurity(GetPositionsDTO value)
    {
        //initPositionSecurityOpened(value);
        initPositionSecurityClosed(value);
    }

    private void initPositionSecurityOpened(GetPositionsDTO psList)
    {
        if (psList != null && psList.openPositionsCount > 0)
        {
            ArrayList<SecurityPositionItem> list = new ArrayList<SecurityPositionItem>();
            List<PositionDTO> listData = psList.getOpenPositions();
            int sizePosition = listData.size();
            for (int i = 0; i < sizePosition; i++)
            {
                SecurityCompactDTO securityCompactDTO = psList.getSecurityCompactDTO(listData.get(i));
                if (securityCompactDTO != null)
                {
                    list.add(new SecurityPositionItem(securityCompactDTO, listData.get(i)));
                }
            }
            if (adapter != null)
            {
                adapter.setSecurityPositionList(list);
            }
        }
    }

    private void initPositionSecurityClosed(GetPositionsDTO psList)
    {
        if (psList != null && psList.openPositionsCount > 0)
        {
            ArrayList<SecurityPositionItem> list = new ArrayList<SecurityPositionItem>();
            List<PositionDTO> listData = psList.getClosedPositions();
            int sizePosition = listData.size();
            for (int i = 0; i < sizePosition; i++)
            {
                SecurityCompactDTO securityCompactDTO = psList.getSecurityCompactDTO(listData.get(i));
                if (securityCompactDTO != null)
                {
                    list.add(new SecurityPositionItem(securityCompactDTO, listData.get(i)));
                }
            }
            if (adapter != null)
            {
                adapter.setSecurityPositionList(list);
            }
        }
    }
}
