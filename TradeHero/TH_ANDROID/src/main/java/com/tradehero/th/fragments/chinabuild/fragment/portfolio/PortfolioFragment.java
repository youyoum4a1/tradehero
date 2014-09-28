package com.tradehero.th.fragments.chinabuild.fragment.portfolio;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.tradehero.common.persistence.DTOCacheNew;
import com.tradehero.common.persistence.prefs.BooleanPreference;
import com.tradehero.common.utils.THToast;
import com.tradehero.th.R;
import com.tradehero.th.activities.ActivityHelper;
import com.tradehero.th.adapters.MyTradePositionListAdapter;
import com.tradehero.th.api.leaderboard.position.LeaderboardMarkUserId;
import com.tradehero.th.api.portfolio.OwnedPortfolioId;
import com.tradehero.th.api.portfolio.PortfolioCompactDTO;
import com.tradehero.th.api.position.GetPositionsDTO;
import com.tradehero.th.api.position.GetPositionsDTOKey;
import com.tradehero.th.api.position.PositionDTO;
import com.tradehero.th.api.security.SecurityCompactDTO;
import com.tradehero.th.api.security.SecurityId;
import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.fragments.base.DashboardFragment;
import com.tradehero.th.fragments.chinabuild.data.PositionInterface;
import com.tradehero.th.fragments.chinabuild.data.PositionLockedItem;
import com.tradehero.th.fragments.chinabuild.data.SecurityPositionItem;
import com.tradehero.th.fragments.chinabuild.data.WatchPositionItem;
import com.tradehero.th.fragments.chinabuild.fragment.BindGuestUserFragment;
import com.tradehero.th.fragments.chinabuild.fragment.security.SecurityDetailFragment;
import com.tradehero.th.fragments.chinabuild.fragment.userCenter.UserMainPage;
import com.tradehero.th.fragments.chinabuild.listview.SecurityListView;
import com.tradehero.th.misc.exception.THException;
import com.tradehero.th.models.portfolio.DisplayablePortfolioFetchAssistant;
import com.tradehero.th.network.retrofit.MiddleCallback;
import com.tradehero.th.network.service.UserServiceWrapper;
import com.tradehero.th.persistence.portfolio.PortfolioCompactListCache;
import com.tradehero.th.persistence.position.GetPositionsCache;
import com.tradehero.th.persistence.prefs.ShareDialogKey;
import com.tradehero.th.persistence.user.UserProfileCache;
import com.tradehero.th.utils.AlertDialogUtil;
import dagger.Lazy;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Provider;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import retrofit.RetrofitError;
import retrofit.client.Response;
import timber.log.Timber;

/**
 * Created by huhaiping on 14-9-14. 个人持仓页。比赛持仓页
 */
public class PortfolioFragment extends DashboardFragment
{
    public static final String BUNDLE_LEADERBOARD_USER_MARK_ID = "bundle_leaderboard_user_mark_id";
    public static final String BUNLDE_SHOW_PROFILE_USER_ID = "bundle_show_profile_user_id";
    public static final String BUNLDE_PORTFOLIO_DTO = "bunlde_portfolio_dto";
    public static final String BUNLDE_COMPETITION_ID = "bundle_competition_id";
    public long leaderboardUserMarkId = 0;//通过比赛排名进入比赛持仓
    public int portfolioUserKey = 0;//通过查看他人主账户进入持仓，需要知道UserID
    public PortfolioCompactDTO portfolioCompactDTO;//直接查看portforlioCompactDTO
    public int competitionId;

    @Inject Lazy<GetPositionsCache> getPositionsCache;
    @Nullable protected DTOCacheNew.Listener<GetPositionsDTOKey, GetPositionsDTO> fetchGetPositionsDTOListener;
    protected GetPositionsDTOKey getPositionsDTOKey;//

    @Inject Lazy<UserProfileCache> userProfileCache;
    //private DTOCacheNew.Listener<UserBaseKey, UserProfileDTO> userProfileCacheListener;
    private DTOCacheNew.Listener<UserBaseKey, UserProfileDTO> currentUserProfileCacheListener;

    @Inject CurrentUserId currentUserId;

    @Inject Lazy<AlertDialogUtil> alertDialogUtilLazy;
    private MiddleCallback<UserProfileDTO> freeFollowMiddleCallback;

    private UserBaseKey showUserBaseKey;
    private UserProfileDTO currentUserProfileDTO;
    @Inject Lazy<PortfolioCompactListCache> portfolioCompactListCache;
    @Inject Lazy<UserServiceWrapper> userServiceWrapperLazy;

    private DisplayablePortfolioFetchAssistant displayablePortfolioFetchAssistant;
    @Inject Provider<DisplayablePortfolioFetchAssistant> displayablePortfolioFetchAssistantProvider;

    @InjectView(R.id.listPortfilio) SecurityListView listView;
    private MyTradePositionListAdapter adapter;

    public static final int PORTFOLIO_TYPE_MINE = 0;
    public static final int PORTFOLIO_TYPE_OTHER_USER = 1;
    public static final int PORTFOLIO_TYPE_COMPETITION = 2;

    public int portfolio_type = 0;

    private PortfolioCompactDTO defaultPortfolio;
    @Inject @ShareDialogKey BooleanPreference mShareDialogKeyPreference;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        fetchGetPositionsDTOListener = createGetPositionsCacheListener();
        //userProfileCacheListener = createUserProfileCacheListener();
        currentUserProfileCacheListener = createCurrentUserProfileFetchListener();
        initArgment();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        super.onCreateOptionsMenu(menu, inflater);
        setHeadViewMiddleMain("TA的持仓");
        if (portfolioUserKey != 0)
        {
            setHeadViewRight0("TA的主页");
        }
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

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override public void onItemClick(AdapterView<?> adapterView, View view, int id, long position)
            {
                PositionInterface item = adapter.getItem((int) position);
                dealSecurityItem(item);
            }
        });

        fetchCurrentUserProfile();

        startLoadding();

        return view;
    }

    public void startLoadding()
    {
        //alertDialogUtilLazy.get().dismissProgressDialog();
        if (getActivity() != null)
        {
            alertDialogUtilLazy.get().showProgressDialog(getActivity(), "加载中");
        }
    }

    public void dealSecurityItem(PositionInterface item)
    {
        if (item instanceof SecurityPositionItem)
        {
            enterSecurity(((SecurityPositionItem) item).security.getSecurityId(), ((SecurityPositionItem) item).security.name , ((SecurityPositionItem) item).position);
        }
        else if (item instanceof WatchPositionItem)
        {
            enterSecurity(((WatchPositionItem) item).watchlistPosition.securityDTO.getSecurityId(),
                    ((WatchPositionItem) item).watchlistPosition.securityDTO.name);
        }
        else if (item instanceof PositionLockedItem)
        {
            Timber.d("Clicked follow user!!!");
            if (mShareDialogKeyPreference.get())
            {
                if (currentUserProfileDTO != null && currentUserProfileDTO.isVisitor
                        && (currentUserProfileDTO.heroIds.size() == 3) || (currentUserProfileDTO.heroIds.size() == 11))
                {
                    alertDialogUtil.popWithOkCancelButton(getActivity(), R.string.app_name,
                            R.string.guest_user_dialog_summary,
                            R.string.ok, R.string.cancel, new DialogInterface.OnClickListener()
                    {
                        @Override public void onClick(DialogInterface dialog, int which)
                        {
                            Bundle args = new Bundle();
                            args.putString(DashboardFragment.BUNDLE_OPEN_CLASS_NAME,
                                    BindGuestUserFragment.class.getName());
                            ActivityHelper.launchDashboard(getActivity(), args);
                        }
                    });
                }
            }
            freeFollow(showUserBaseKey);
        }
    }

    @Override public void onClickHeadRight0()
    {
        super.onClickHeadRight0();
        enterUserMainPage();
    }

    public void enterUserMainPage()
    {
        Bundle bundle = new Bundle();
        bundle.putInt(UserMainPage.BUNDLE_USER_BASE_KEY, portfolioUserKey);
        pushFragment(UserMainPage.class, bundle);
    }

    public void enterSecurity(SecurityId securityId, String securityName)
    {
        Bundle bundle = new Bundle();
        bundle.putBundle(SecurityDetailFragment.BUNDLE_KEY_SECURITY_ID_BUNDLE, securityId.getArgs());
        bundle.putString(SecurityDetailFragment.BUNDLE_KEY_SECURITY_NAME, securityName);
        bundle.putInt(SecurityDetailFragment.BUNDLE_KEY_COMPETITION_ID_BUNDLE, competitionId);
        pushFragment(SecurityDetailFragment.class, bundle);
    }

    public void enterSecurity(SecurityId securityId, String securityName,PositionDTO positionDTO)
    {
        if(defaultPortfolio == null )return;
        Bundle bundle = new Bundle();
        bundle.putBundle(SecurityDetailFragment.BUNDLE_KEY_SECURITY_ID_BUNDLE, securityId.getArgs());
        bundle.putString(SecurityDetailFragment.BUNDLE_KEY_SECURITY_NAME, securityName);
        bundle.putInt(SecurityDetailFragment.BUNDLE_KEY_COMPETITION_ID_BUNDLE, competitionId);
        PositionDetailFragment.putPositionDTOKey(bundle, positionDTO.getPositionDTOKey());
        OwnedPortfolioId ownedPortfolioId = new OwnedPortfolioId(defaultPortfolio.userId,defaultPortfolio.id);
        if (ownedPortfolioId != null)
        {
            PositionDetailFragment.putApplicablePortfolioId(bundle, ownedPortfolioId);
        }
        pushFragment(PositionDetailFragment.class, bundle);
        //pushFragment(SecurityDetailFragment.class, bundle);
    }

    @Override public void onStop()
    {
        super.onStop();
    }

    @Override public void onDestroyView()
    {
        detachGetPositionsTask();
        detachCurrentUserProfileCache();
        detachFreeFollowMiddleCallback();
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
        fetchCurrentUserProfile();
        //fetchSimplePage(false);
        //displayablePortfolioFetchAssistant.fetch(getUserBaseKeys());
    }

    public void initArgment()
    {
        Bundle bundle = getArguments();
        if (bundle != null)
        {
            leaderboardUserMarkId = bundle.getLong(BUNDLE_LEADERBOARD_USER_MARK_ID, 0);
            portfolioUserKey = bundle.getInt(BUNLDE_SHOW_PROFILE_USER_ID, 0);
            portfolioCompactDTO = (PortfolioCompactDTO) bundle.getSerializable(BUNLDE_PORTFOLIO_DTO);
            competitionId = bundle.getInt(BUNLDE_COMPETITION_ID);
            if (leaderboardUserMarkId != 0)
            { //来自比赛的持仓
                portfolio_type = PORTFOLIO_TYPE_COMPETITION;
                showUserBaseKey = new UserBaseKey(portfolioUserKey);
                getPositionsDTOKey = new LeaderboardMarkUserId((int) leaderboardUserMarkId);
            }
            else if (portfolioUserKey != 0)
            { //来自股神持仓，股神的主账户持仓
                portfolio_type = PORTFOLIO_TYPE_OTHER_USER;
                showUserBaseKey = new UserBaseKey(portfolioUserKey);
                getDefaultPortfolio();
            }
            else if (portfolioCompactDTO != null)
            {//来自比赛的持仓，我的当前比赛持仓
                portfolio_type = PORTFOLIO_TYPE_MINE;
                getPositionsFromPortfolio(portfolioCompactDTO);
            }
        }
    }

    private void getPositionsFromPortfolio(PortfolioCompactDTO portfolioCompactDTO)
    {
        getPositionsDTOKey = new OwnedPortfolioId(portfolioCompactDTO.userId, portfolioCompactDTO.id);
        fetchSimplePage(true);
    }

    private void getDefaultPortfolio()
    {
        PortfolioCompactDTO defaultPortfolio = portfolioCompactListCache.get().getDefaultPortfolio(showUserBaseKey);
        this.defaultPortfolio = defaultPortfolio;
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

    private void FetchedDefaultPortfolio()
    {
        PortfolioCompactDTO defaultPortfolio = portfolioCompactListCache.get().getDefaultPortfolio(showUserBaseKey);
        this.defaultPortfolio = defaultPortfolio;
        if (defaultPortfolio != null)
        {
            getPositionsDTOKey = new OwnedPortfolioId(showUserBaseKey.key, defaultPortfolio.id);
            fetchSimplePage(false);
        }
    }

    private void refreshPortfolioList()
    {
        displayablePortfolioFetchAssistant = displayablePortfolioFetchAssistantProvider.get();
        portfolioCompactListCache.get().invalidate(showUserBaseKey);
        displayablePortfolioFetchAssistant.fetch(getUserBaseKeys());
        displayablePortfolioFetchAssistant.setFetchedListener(new DisplayablePortfolioFetchAssistant.OnFetchedListener()
        {
            @Override public void onFetched()
            {
                FetchedDefaultPortfolio();
            }
        });
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
            finished();
            //showResultIfNecessary();
        }

        @Override public void onDTOReceived(
                @NotNull GetPositionsDTOKey key,
                @NotNull GetPositionsDTO value)
        {
            linkWith(value, true);
            finished();
            //showResultIfNecessary();
        }

        @Override public void onErrorThrown(
                @NotNull GetPositionsDTOKey key,
                @NotNull Throwable error)
        {
            Timber.d(error, "Error fetching the positionList info %s", key);
            finished();
        }

        public void finished()
        {
            alertDialogUtilLazy.get().dismissProgressDialog();
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
            startLoadding();
            detachGetPositionsTask();
            getPositionsCache.get().register(getPositionsDTOKey, fetchGetPositionsDTOListener);
            getPositionsCache.get().getOrFetchAsync(getPositionsDTOKey, force);
        }
    }

    //protected void detachUserProfileCache()
    //{
    //    userProfileCache.get().unregister(userProfileCacheListener);
    //}
    //
    //protected void fetchUserProfile(boolean force)
    //{
    //    detachUserProfileCache();
    //    userProfileCache.get().register(showUserBaseKey, userProfileCacheListener);
    //    userProfileCache.get().getOrFetchAsync(showUserBaseKey, force);
    //}

    private void detachCurrentUserProfileCache()
    {
        userProfileCache.get().unregister(currentUserProfileCacheListener);
    }

    protected void fetchCurrentUserProfile()
    {
        detachCurrentUserProfileCache();
        userProfileCache.get().register(currentUserId.toUserBaseKey(), currentUserProfileCacheListener);
        userProfileCache.get().getOrFetchAsync(currentUserId.toUserBaseKey());
    }

    //protected DTOCacheNew.Listener<UserBaseKey, UserProfileDTO> createUserProfileCacheListener()
    //{
    //    return new TimelineFragmentUserProfileCacheListener();
    //}
    //
    //protected class TimelineFragmentUserProfileCacheListener implements DTOCacheNew.Listener<UserBaseKey, UserProfileDTO>
    //{
    //    @Override public void onDTOReceived(@NotNull UserBaseKey key, @NotNull UserProfileDTO value)
    //    {
    //        linkWith(value);
    //    }
    //
    //    @Override public void onErrorThrown(@NotNull UserBaseKey key, @NotNull Throwable error)
    //    {
    //        THToast.show(getString(R.string.error_fetch_user_profile));
    //    }
    //}
    //private void linkWith(UserProfileDTO value)
    //{
    //    Timber.d("UserProfileDTO 获取成功！");
    //}

    private void initPositionSecurity(GetPositionsDTO value)
    {
        initPositionSecurityOpened(value);
        initPositionSecurityClosed(value);
    }

    public boolean isNeedShowLock()
    {
        return portfolio_type != PORTFOLIO_TYPE_MINE;
    }

    private void initPositionSecurityOpened(GetPositionsDTO psList)
    {

        if (isNeedShowLock() && (!isFollowUserOrMe()))
        {
            if (adapter != null)
            {
                adapter.setSecurityPositionListLocked(true);
            }
        }
        else
        {
            if (adapter != null)
            {
                adapter.setSecurityPositionListLocked(false);
            }

            if (psList != null && psList.openPositionsCount > 0)
            {
                ArrayList<SecurityPositionItem> list = new ArrayList<SecurityPositionItem>();
                List<PositionDTO> listData = psList.getOpenPositions();
                int sizePosition = listData.size();
                for (int i = 0; i < sizePosition; i++)
                {
                    SecurityCompactDTO securityCompactDTO = psList.getSecurityCompactDTO(listData.get(i));
                    if (securityCompactDTO != null && securityCompactDTO.id > 0)
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

    private void initPositionSecurityClosed(GetPositionsDTO psList)
    {
        if (psList != null && psList.closedPositionsCount > 0)
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
                adapter.setSecurityPositionListClosed(list);
            }
        }
    }

    protected DTOCacheNew.Listener<UserBaseKey, UserProfileDTO> createCurrentUserProfileFetchListener()
    {
        return new CurrentUserProfileFetchListener();
    }

    protected class CurrentUserProfileFetchListener implements DTOCacheNew.Listener<UserBaseKey, UserProfileDTO>
    {
        @Override
        public void onDTOReceived(@NotNull UserBaseKey key, @NotNull UserProfileDTO value)
        {
            setCurrentUserDTO(value);
        }

        @Override public void onErrorThrown(@NotNull UserBaseKey key, @NotNull Throwable error)
        {

        }
    }

    public void setCurrentUserDTO(UserProfileDTO userDTO)
    {
        this.currentUserProfileDTO = userDTO;
        fetchSimplePage(true);
    }

    public boolean isFollowUserOrMe()
    {
        if (showUserBaseKey != null && currentUserId != null)
        {
            if (showUserBaseKey.key.equals(currentUserId.toUserBaseKey().getUserId()))
            {
                return true;
            }
        }

        if (currentUserProfileDTO != null)
        {
            return currentUserProfileDTO.isFollowingUser(showUserBaseKey);
        }
        else
        {
            return false;
        }
    }

    private void detachFreeFollowMiddleCallback()
    {
        if (freeFollowMiddleCallback != null)
        {
            freeFollowMiddleCallback.setPrimaryCallback(null);
        }
        freeFollowMiddleCallback = null;
    }

    protected void freeFollow(@NotNull UserBaseKey heroId)
    {
        alertDialogUtilLazy.get().showProgressDialog(getActivity(), getActivity().getString(
                R.string.following_this_hero));
        detachFreeFollowMiddleCallback();
        freeFollowMiddleCallback =
                userServiceWrapperLazy.get()
                        .freeFollow(heroId, new FreeFollowCallback());
    }

    public class FreeFollowCallback implements retrofit.Callback<UserProfileDTO>
    {
        @Override public void success(UserProfileDTO userProfileDTO, Response response)
        {
            currentUserProfileDTO = userProfileDTO;
            alertDialogUtilLazy.get().dismissProgressDialog();
            userProfileCache.get().put(userProfileDTO.getBaseKey(), userProfileDTO);
            fetchSimplePage(true);
        }

        @Override public void failure(RetrofitError retrofitError)
        {
            THToast.show(new THException(retrofitError));
            alertDialogUtilLazy.get().dismissProgressDialog();
        }
    }
}
