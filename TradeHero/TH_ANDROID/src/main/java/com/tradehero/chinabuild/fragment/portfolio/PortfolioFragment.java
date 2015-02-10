package com.tradehero.chinabuild.fragment.portfolio;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.handmark.pulltorefresh.library.pulltorefresh.PullToRefreshBase;
import com.tradehero.chinabuild.data.PositionInterface;
import com.tradehero.chinabuild.data.PositionLockedItem;
import com.tradehero.chinabuild.data.SecurityPositionItem;
import com.tradehero.chinabuild.fragment.competition.CompetitionSecuritySearchFragment;
import com.tradehero.chinabuild.fragment.security.SecurityDetailFragment;
import com.tradehero.chinabuild.fragment.userCenter.UserMainPage;
import com.tradehero.chinabuild.listview.SecurityListView;
import com.tradehero.common.persistence.DTOCacheNew;
import com.tradehero.common.persistence.prefs.BooleanPreference;
import com.tradehero.common.utils.THToast;
import com.tradehero.th.R;
import com.tradehero.th.adapters.MyTradePositionListAdapter;
import com.tradehero.th.api.leaderboard.position.PerPagedLeaderboardMarkUserId;
import com.tradehero.th.api.portfolio.OwnedPortfolioId;
import com.tradehero.th.api.portfolio.PortfolioCompactDTO;
import com.tradehero.th.api.portfolio.PortfolioDTO;
import com.tradehero.th.api.position.GetPositionsDTO;
import com.tradehero.th.api.position.GetPositionsDTOKey;
import com.tradehero.th.api.position.PositionDTO;
import com.tradehero.th.api.security.SecurityCompactDTO;
import com.tradehero.th.api.security.SecurityId;
import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.fragments.base.DashboardFragment;
import com.tradehero.th.models.number.THSignedMoney;
import com.tradehero.th.models.number.THSignedNumber;
import com.tradehero.th.models.number.THSignedPercentage;
import com.tradehero.th.models.portfolio.DisplayablePortfolioFetchAssistant;
import com.tradehero.th.network.retrofit.MiddleCallback;
import com.tradehero.th.network.service.PositionServiceWrapper;
import com.tradehero.th.network.service.UserServiceWrapper;
import com.tradehero.th.persistence.portfolio.PortfolioCompactListCache;
import com.tradehero.th.persistence.position.GetPositionsCache;
import com.tradehero.th.persistence.prefs.BindGuestUser;
import com.tradehero.th.persistence.user.UserProfileCache;
import com.tradehero.th.utils.AlertDialogUtil;
import com.tradehero.th.utils.metrics.Analytics;
import com.tradehero.th.utils.metrics.AnalyticsConstants;
import com.tradehero.th.utils.metrics.events.MethodEvent;
import dagger.Lazy;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

import javax.inject.Inject;
import javax.inject.Provider;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by huhaiping on 14-9-14. 个人持仓页。比赛持仓页
 */
public class PortfolioFragment extends DashboardFragment
{
    public static final String BUNLDE_NEED_SHOW_MAINPAGE = "bundle_need_show_mainpage";
    public static final String BUNDLE_LEADERBOARD_USER_MARK_ID = "bundle_leaderboard_user_mark_id";
    public static final String BUNLDE_SHOW_PROFILE_USER_ID = "bundle_show_profile_user_id";
    public static final String BUNLDE_PORTFOLIO_DTO = "bunlde_portfolio_dto";
    public static final String BUNLDE_COMPETITION_ID = "bundle_competition_id";
    public long leaderboardUserMarkId = 0;//通过比赛排名进入比赛持仓
    public int portfolioUserKey = 0;//通过查看他人主账户进入持仓，需要知道UserID
    public PortfolioCompactDTO portfolioCompactDTO;//直接查看portforlioCompactDTO
    public int competitionId;

    @Inject Analytics analytics;

    @Inject Lazy<GetPositionsCache> getPositionsCache;
    @Nullable protected DTOCacheNew.Listener<GetPositionsDTOKey, GetPositionsDTO> fetchGetPositionsDTOListener;
    protected GetPositionsDTOKey getPositionsDTOKey;//

    @Inject Lazy<UserProfileCache> userProfileCache;
    private DTOCacheNew.Listener<UserBaseKey, UserProfileDTO> currentUserProfileCacheListener;

    @Inject CurrentUserId currentUserId;
    @Inject Lazy<AlertDialogUtil> alertDialogUtilLazy;
    private MiddleCallback<UserProfileDTO> freeFollowMiddleCallback;

    private UserBaseKey showUserBaseKey;
    private UserProfileDTO currentUserProfileDTO;
    @Inject Lazy<PortfolioCompactListCache> portfolioCompactListCache;
    @Inject Lazy<UserServiceWrapper> userServiceWrapperLazy;

    @Inject Provider<DisplayablePortfolioFetchAssistant> displayablePortfolioFetchAssistantProvider;

    @InjectView(R.id.listPortfilio) SecurityListView listView;
    private MyTradePositionListAdapter adapter;

    public static final int PORTFOLIO_TYPE_MINE = 0;
    public static final int PORTFOLIO_TYPE_OTHER_USER = 1;
    public static final int PORTFOLIO_TYPE_COMPETITION = 2;

    public int portfolio_type = 0;
    public boolean isNeedShowMainPage = true;

    @Inject @BindGuestUser BooleanPreference mBindGuestUserDialogKeyPreference;

    private int user_id = 0;
    private int portfolio_id = 0;

    private int currentPage = 1;
    private final int perPage = 20;

    private MiddleCallback<GetPositionsDTO> getPositionDTOCallback;
    @Inject Lazy<PositionServiceWrapper> positionServiceWrapper;

    private String dialogContent;

    @InjectView(R.id.llUserAccountHead) LinearLayout llUserAccountHead;
    @InjectView(R.id.tvWatchListItemROI) TextView tvItemROI;
    @InjectView(R.id.tvWatchListItemAllAmount) TextView tvItemAllAmount;
    @InjectView(R.id.tvWatchListItemDynamicAmount) TextView tvItemDynamicAmount;
    @InjectView(R.id.tvWatchListItemCash) TextView tvItemCash;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        fetchGetPositionsDTOListener = createGetPositionsCacheListener();
        currentUserProfileCacheListener = createCurrentUserProfileFetchListener();
        initArgment();
        adapter = new MyTradePositionListAdapter(getActivity());
        isNeedShowMainPage = getIsNeedShowPortfolio();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        super.onCreateOptionsMenu(menu, inflater);

        if (portfolioUserKey != 0)
        {
            if (isNeedShowMainPage)
            {
                setHeadViewRight0("TA的主页");
                analytics.addEventAuto(new MethodEvent(AnalyticsConstants.CHINA_BUILD_BUTTON_CLICKED, AnalyticsConstants.BUTTON_PORTFOLIO_MAIN_PAGE));
            }
        }

        if (portfolio_type == PORTFOLIO_TYPE_MINE)
        {
            setHeadViewMiddleMain("我的持仓");
        }
        else
        {
            setHeadViewMiddleMain("TA的持仓");
        }

        if (portfolio_type == PORTFOLIO_TYPE_MINE)
        {
            setHeadViewRight0("去比赛");
            analytics.addEventAuto(
                    new MethodEvent(AnalyticsConstants.CHINA_BUILD_BUTTON_CLICKED, AnalyticsConstants.BUTTON_PORTFOLIO_GOTO_COMPETITION));
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.portfolio_layout, container, false);
        ButterKnife.inject(this, view);

        listView.setAdapter(adapter);
        listView.setMode(PullToRefreshBase.Mode.DISABLED);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override public void onItemClick(AdapterView<?> adapterView, View view, int id, long position)
            {
                PositionInterface item = adapter.getItem((int) position);
                dealSecurityItem(item);
                analytics.addEventAuto(new MethodEvent(AnalyticsConstants.BUTTON_PORTFOLIO_POSITION_CLICKED, "" + position));
            }
        });
        listView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {

            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {

            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                getDataFromNormalUserMore();
            }
        });

        showUserPortfolioHead();

        fetchCurrentUserProfile();
        getDataFromNormalUser();
        return view;
    }

    public void showUserPortfolioHead()
    {
        llUserAccountHead.setVisibility(portfolio_type == PORTFOLIO_TYPE_MINE ? View.VISIBLE : View.GONE);
        if (portfolio_type == PORTFOLIO_TYPE_MINE)
        {
            if (portfolioCompactDTO instanceof PortfolioDTO)
            {
                displayPortfolio((PortfolioDTO) portfolioCompactDTO);
            }
        }
    }

    public void startLoading()
    {
        if (getActivity() != null)
        {
            alertDialogUtilLazy.get().showProgressDialog(getActivity(), "加载中");
        }
    }

    public void dealSecurityItem(PositionInterface item)
    {
        if (item instanceof SecurityPositionItem)
        {
            if (portfolio_type == PORTFOLIO_TYPE_MINE && ((SecurityPositionItem) item).type == SecurityPositionItem.TYPE_ACTIVE)
            {//只有我的比赛持仓才需要直接跳转至股票详情页面
                enterSecurityToSecurityDetail(((SecurityPositionItem) item).security.getSecurityId(), ((SecurityPositionItem) item).security.name,
                        ((SecurityPositionItem) item).position);
            }
            else
            {//其他只需要跳转到持仓详情页
                enterSecurityToPortfolio(((SecurityPositionItem) item).security.getSecurityId(), ((SecurityPositionItem) item).security.name,
                        ((SecurityPositionItem) item).position);
            }
        }
        else if (item instanceof PositionLockedItem)
        {
            if (currentUserProfileDTO != null && currentUserProfileDTO.isVisitor && currentUserProfileDTO.getAllHeroCount() >= 5)
            {
                dialogContent = getActivity().getResources().getString(R.string.guest_user_dialog_summary);
                showSuggestLoginDialogFragment(dialogContent);
                return;
            }

            analytics.addEventAuto(new MethodEvent(AnalyticsConstants.CHINA_BUILD_BUTTON_CLICKED, AnalyticsConstants.BUTTON_PORTFOLIO_FOLLOW_USER));
            freeFollow(showUserBaseKey);
        }
    }

    @Override public void onClickHeadRight0()
    {
        super.onClickHeadRight0();
        if (portfolio_type == PORTFOLIO_TYPE_MINE)
        {
            toPlayCompetition();
        }
        else
        {
            enterUserMainPage();
        }
    }

    public void toPlayCompetition()
    {
        Bundle bundle = new Bundle();
        bundle.putInt(CompetitionSecuritySearchFragment.BUNLDE_COMPETITION_ID, competitionId);
        pushFragment(CompetitionSecuritySearchFragment.class, bundle);
    }

    public void enterUserMainPage()
    {
        Bundle bundle = new Bundle();
        bundle.putInt(UserMainPage.BUNDLE_USER_BASE_KEY, portfolioUserKey);
        bundle.putBoolean(UserMainPage.BUNDLE_NEED_SHOW_PROFILE, false);
        pushFragment(UserMainPage.class, bundle);
    }

    public void setPortfolioInfo(int user_id, int porfolio_id)
    {
        this.user_id = user_id;
        this.portfolio_id = porfolio_id;
    }

    public void enterSecurityToSecurityDetail(SecurityId securityId, String securityName, PositionDTO positionDTO)
    {
        Bundle bundle = new Bundle();
        bundle.putBundle(SecurityDetailFragment.BUNDLE_KEY_SECURITY_ID_BUNDLE, securityId.getArgs());
        bundle.putString(SecurityDetailFragment.BUNDLE_KEY_SECURITY_NAME, securityName);
        bundle.putInt(SecurityDetailFragment.BUNDLE_KEY_COMPETITION_ID_BUNDLE, competitionId);
        pushFragment(SecurityDetailFragment.class, bundle);
    }

    public void enterSecurityToPortfolio(SecurityId securityId, String securityName, PositionDTO positionDTO)
    {
        Bundle bundle = new Bundle();
        bundle.putBundle(SecurityDetailFragment.BUNDLE_KEY_SECURITY_ID_BUNDLE, securityId.getArgs());
        bundle.putString(SecurityDetailFragment.BUNDLE_KEY_SECURITY_NAME, securityName);
        bundle.putInt(SecurityDetailFragment.BUNDLE_KEY_COMPETITION_ID_BUNDLE, competitionId);
        PositionDetailFragment.putPositionDTOKey(bundle, positionDTO.getPositionDTOKey());
        OwnedPortfolioId ownedPortfolioId = new OwnedPortfolioId(user_id, portfolio_id);
        if (ownedPortfolioId != null)
        {
            PositionDetailFragment.putApplicablePortfolioId(bundle, ownedPortfolioId);
        }
        pushFragment(PositionDetailFragment.class, bundle);
    }

    @Override public void onDestroyView()
    {
        detachGetPositionsTask();
        detachCurrentUserProfileCache();
        detachFreeFollowMiddleCallback();
        detachGetPositionMiddleCallback();
        ButterKnife.reset(this);
        super.onDestroyView();
    }

    @Override public void onDestroy()
    {
        fetchGetPositionsDTOListener = null;
        super.onDestroy();
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
            {   //来自比赛的榜单进入持仓
                portfolio_type = PORTFOLIO_TYPE_COMPETITION;
                showUserBaseKey = new UserBaseKey(portfolioUserKey);
                getPositionsDTOKey = new PerPagedLeaderboardMarkUserId((int) leaderboardUserMarkId, 1, perPage);
            }
            else if (portfolioUserKey != 0)
            {
                //来自股神持仓，股神的主账户持仓
                portfolio_type = PORTFOLIO_TYPE_OTHER_USER;
                showUserBaseKey = new UserBaseKey(portfolioUserKey);
            }
            else if (portfolioCompactDTO != null)
            {   //来自比赛的持仓，我的当前比赛持仓
                portfolio_type = PORTFOLIO_TYPE_MINE;
                getPositionsFromPortfolio(portfolioCompactDTO);
                setPortfolioInfo(portfolioCompactDTO.userId, portfolioCompactDTO.id);
            }
        }
    }

    public void getDataFromNormalUser()
    {
        //来自股神持仓，股神的主账户持仓
        currentPage = 1;
        if (portfolio_type == PORTFOLIO_TYPE_OTHER_USER)
        {
            getPositionDirectly(showUserBaseKey, currentPage);
        }
        else
        {
            getPositionsDTOKey = new PerPagedLeaderboardMarkUserId((int) leaderboardUserMarkId, currentPage, perPage);
            if (getPositionsDTOKey != null && getPositionsDTOKey.isValid())
            {
                detachGetPositionsTask();
                getPositionsCache.get().register(getPositionsDTOKey, fetchGetPositionsDTOListener);
                getPositionsCache.get().getOrFetchAsync(getPositionsDTOKey, true);
            }
        }
    }

    public void getDataFromNormalUserMore(){
        //来自股神持仓，股神的主账户持仓
        currentPage++;
        if (portfolio_type == PORTFOLIO_TYPE_OTHER_USER)
        {
            getPositionDirectly(showUserBaseKey, currentPage);
        }
        else
        {
            if (getPositionsDTOKey != null && getPositionsDTOKey.isValid())
            {
                detachGetPositionsTask();
                getPositionsCache.get().register(getPositionsDTOKey, fetchGetPositionsDTOListener);
                getPositionsCache.get().getOrFetchAsync(getPositionsDTOKey, true);
            }
        }
    }

    private void getPositionsFromPortfolio(PortfolioCompactDTO portfolioCompactDTO)
    {
        getPositionsDTOKey = new OwnedPortfolioId(portfolioCompactDTO.userId, portfolioCompactDTO.id);
        fetchSimplePage(true);
    }

    public boolean getIsNeedShowPortfolio()
    {
        Bundle bundle = getArguments();
        if (bundle != null)
        {
            this.isNeedShowMainPage = getArguments().getBoolean(BUNLDE_NEED_SHOW_MAINPAGE, true);
        }
        return isNeedShowMainPage;
    }

    @NotNull protected DTOCacheNew.Listener<GetPositionsDTOKey, GetPositionsDTO> createGetPositionsCacheListener()
    {
        return new GetPositionsListener();
    }

    private void linkWith(GetPositionsDTO value)
    {
        try
        {//来自比赛的profolio信息从GetPositionDTO里获取
            user_id = value.positions.get(0).userId;
            portfolio_id = value.positions.get(0).portfolioId;
        } catch (Exception e)
        {
            e.printStackTrace();
        }
        initPositionSecurity(value);
    }

    protected class GetPositionsListener
            implements DTOCacheNew.HurriedListener<GetPositionsDTOKey, GetPositionsDTO>
    {
        @Override public void onPreCachedDTOReceived(
                @NotNull GetPositionsDTOKey key,
                @NotNull GetPositionsDTO value)
        {
            linkWith(value);
            finished();
        }

        @Override public void onDTOReceived(
                @NotNull GetPositionsDTOKey key,
                @NotNull GetPositionsDTO value)
        {
            linkWith(value);
            finished();
        }

        @Override public void onErrorThrown(
                @NotNull GetPositionsDTOKey key,
                @NotNull Throwable error)
        {
            if(currentPage>1) {
                currentPage--;
            }else{
                currentPage = 1;
            }
            finished();
        }

        public void finished()
        {
            listView.setMode(PullToRefreshBase.Mode.PULL_FROM_END);
            listView.onRefreshComplete();
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
            detachGetPositionsTask();
            getPositionsCache.get().register(getPositionsDTOKey, fetchGetPositionsDTOListener);
            getPositionsCache.get().getOrFetchAsync(getPositionsDTOKey, force);
        }

        if (adapter != null && adapter.getCount() == 0)
        {
            startLoading();
        }
    }

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
                        list.add(new SecurityPositionItem(securityCompactDTO, listData.get(i), SecurityPositionItem.TYPE_ACTIVE));
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
                    list.add(new SecurityPositionItem(securityCompactDTO, listData.get(i), SecurityPositionItem.TYPE_CLOSED));
                }
            }
            if (adapter != null)
            {
                if(currentPage == 1) {
                    adapter.setSecurityPositionListClosed(list);
                }else{
                    adapter.addSecurityPositionListClosed(list);
                }
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

        @Override public void onErrorThrown(@NotNull UserBaseKey key, @NotNull Throwable error){}

    }

    public void setCurrentUserDTO(UserProfileDTO userDTO)
    {
        this.currentUserProfileDTO = userDTO;
        fetchSimplePage(false);
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
            getDataFromNormalUser();
        }

        @Override public void failure(RetrofitError retrofitError)
        {
            THToast.show(R.string.error_network_connection);
            alertDialogUtilLazy.get().dismissProgressDialog();
        }
    }

    public class GetPositionCallback implements Callback<GetPositionsDTO>
    {

        @Override public void success(GetPositionsDTO getPositionsDTO, Response response)
        {
            linkWith(getPositionsDTO);
            alertDialogUtilLazy.get().dismissProgressDialog();
            onFinish();
        }

        @Override public void failure(RetrofitError retrofitError)
        {
            alertDialogUtilLazy.get().dismissProgressDialog();
            if(currentPage>1) {
                currentPage--;
            }else{
                currentPage = 1;
            }
            onFinish();
        }

        private void onFinish(){
            listView.setMode(PullToRefreshBase.Mode.PULL_FROM_END);
            listView.onRefreshComplete();
        }

    }

    private void detachGetPositionMiddleCallback()
    {
        if (getPositionDTOCallback != null)
        {
            getPositionDTOCallback.setPrimaryCallback(null);
        }
        getPositionDTOCallback = null;
    }

    protected void getPositionDirectly(@NotNull UserBaseKey heroId, int currentPage)
    {
        detachGetPositionMiddleCallback();
        getPositionDTOCallback =
                positionServiceWrapper.get()
                        .getPositionsDirect(heroId.key, currentPage, perPage, new GetPositionCallback());
    }

    private void displayPortfolio(PortfolioDTO portfolio)
    {

        if (portfolio == null) return;

        if (portfolio.roiSinceInception != null)
        {
            THSignedNumber roi = THSignedPercentage.builder(portfolio.roiSinceInception * 100)
                    .withSign()
                    .signTypeArrow()
                    .build();
            tvItemROI.setText(roi.toString());
            tvItemROI.setTextColor(getResources().getColor(roi.getColorResId()));
        }

        String valueString = String.format("%s %,.0f", portfolio.getNiceCurrency(), portfolio.totalValue);
        tvItemAllAmount.setText(valueString);

        Double pl = portfolio.plSinceInception;
        if (pl == null)
        {
            pl = 0.0;
        }
        THSignedNumber thPlSinceInception = THSignedMoney.builder(pl)
                .withSign()
                .signTypePlusMinusAlways()
                .currency(portfolio.getNiceCurrency())
                .build();
        tvItemDynamicAmount.setText(thPlSinceInception.toString());
        tvItemDynamicAmount.setTextColor(thPlSinceInception.getColor());

        String vsCash = String.format("%s %,.0f", portfolio.getNiceCurrency(), portfolio.cashBalance);
        tvItemCash.setText(vsCash);
    }
}
