package com.tradehero.th.fragments.chinabuild.fragment.competition;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.squareup.picasso.Picasso;
import com.tradehero.common.persistence.DTOCacheNew;
import com.tradehero.common.persistence.prefs.StringPreference;
import com.tradehero.common.utils.THToast;
import com.tradehero.common.widget.dialog.THDialog;
import com.tradehero.th.R;
import com.tradehero.th.activities.ActivityHelper;
import com.tradehero.th.adapters.LeaderboardListAdapter;
import com.tradehero.th.api.competition.CompetitionDTOUtil;
import com.tradehero.th.api.competition.ProviderId;
import com.tradehero.th.api.competition.key.CompetitionId;
import com.tradehero.th.api.leaderboard.LeaderboardDTO;
import com.tradehero.th.api.leaderboard.LeaderboardUserDTO;
import com.tradehero.th.api.leaderboard.LeaderboardUserDTOList;
import com.tradehero.th.api.leaderboard.competition.CompetitionLeaderboardDTO;
import com.tradehero.th.api.leaderboard.competition.CompetitionLeaderboardId;
import com.tradehero.th.api.leaderboard.key.PagedLeaderboardKey;
import com.tradehero.th.api.portfolio.PortfolioCompactDTO;
import com.tradehero.th.api.portfolio.PortfolioId;
import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.fragments.base.DashboardFragment;
import com.tradehero.th.fragments.chinabuild.cache.PortfolioCompactNewCache;
import com.tradehero.th.fragments.chinabuild.data.UserCompetitionDTO;
import com.tradehero.th.fragments.chinabuild.dialog.ShareSheetDialogLayout;
import com.tradehero.th.fragments.chinabuild.fragment.BindGuestUserFragment;
import com.tradehero.th.fragments.chinabuild.fragment.message.DiscoveryDiscussSendFragment;
import com.tradehero.th.fragments.chinabuild.fragment.portfolio.PortfolioFragment;
import com.tradehero.th.fragments.chinabuild.fragment.test.WebViewSimpleFragment;
import com.tradehero.th.fragments.chinabuild.fragment.userCenter.UserMainPage;
import com.tradehero.th.fragments.chinabuild.listview.SecurityListView;
import com.tradehero.th.misc.exception.THException;
import com.tradehero.th.models.leaderboard.key.LeaderboardDefKeyKnowledge;
import com.tradehero.th.models.number.THSignedNumber;
import com.tradehero.th.models.number.THSignedPercentage;
import com.tradehero.th.persistence.competition.CompetitionCache;
import com.tradehero.th.persistence.leaderboard.CompetitionLeaderboardCache;
import com.tradehero.th.persistence.prefs.ShareSheetTitleCache;
import com.tradehero.th.persistence.user.UserProfileCache;
import com.tradehero.th.utils.ProgressDialogUtil;
import dagger.Lazy;
import javax.inject.Inject;
import org.jetbrains.annotations.NotNull;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import timber.log.Timber;

/**
 * Created by huhaiping on 14-9-9. 比赛详情页
 */
public class CompetitionDetailFragment extends DashboardFragment
{
    public static final String BUNDLE_COMPETITION_DTO = "bundle_competition_dto";
    public static final String BUNDLE_COMPETITION_ID = "bundle_competition_id";

    @Inject Lazy<Picasso> picasso;
    @Inject CompetitionDTOUtil competitionDTOUtil;
    @Inject CompetitionLeaderboardCache competitionLeaderboardCache;
    protected DTOCacheNew.Listener<CompetitionLeaderboardId, CompetitionLeaderboardDTO> competitionLeaderboardCacheListener;

    @Inject Lazy<CompetitionCache> competitionCacheLazy;
    private Callback<UserCompetitionDTO> callbackEnrollUGC;
    private Callback<UserCompetitionDTO> callbackgetCompetition;
    private Callback<LeaderboardDTO> callbackMySelfRank;

    @Inject protected PortfolioCompactNewCache portfolioCompactNewCache;
    private DTOCacheNew.Listener<PortfolioId, PortfolioCompactDTO> portfolioCompactNewFetchListener;

    public UserCompetitionDTO userCompetitionDTO;
    public int competitionId;
    private ProgressDialog mTransactionDialog;
    @Inject ProgressDialogUtil progressDialogUtil;

    private PortfolioCompactDTO portfolioCompactDTO;

    protected CompetitionLeaderboardDTO competitionLeaderboardDTO;
    @Inject CurrentUserId currentUserId;
    @Inject Lazy<UserProfileCache> userProfileCache;
    private DTOCacheNew.Listener<UserBaseKey, UserProfileDTO> userProfileCacheListener;
    private UserProfileDTO mUserProfileDTO;

    @InjectView(R.id.listRanks) SecurityListView listRanks;//比赛排名
    private LeaderboardListAdapter adapter;
    private int currentPage = 1;
    private int PER_PAGE = 20;
    private Dialog mShareSheetDialog;
    @Inject @ShareSheetTitleCache StringPreference mShareSheetTitleCache;

    @InjectView(R.id.tvCompetitionDetailMore) TextView tvCompetitionDetailMore;//比赛详情
    @InjectView(R.id.tvCompetitionCreator) TextView tvCompetitionCreator;//创建人
    @InjectView(R.id.tvCompetitionExchange) TextView tvCompetitionExchange;//比赛交易所
    @InjectView(R.id.tvCompetitionPeriod) TextView tvCompetitionPeriod;//比赛周期
    @InjectView(R.id.tvCompetitionIntro) TextView tvCompetitionIntro;//比赛介绍
    @InjectView(R.id.tvGotoCompetition) TextView tvGotoCompetition;//去比赛

    @InjectView(R.id.includeMyPosition) RelativeLayout includeMyPosition;//我的比赛数据行
    @InjectView(R.id.tvUserRank) TextView tvUserRank;//我的排名
    @InjectView(R.id.tvUserExtraValue) TextView tvUserExtraValue;//我的收益率
    @InjectView(R.id.tvUserName) TextView tvUserName;//我的名字
    @InjectView(R.id.imgUserHead) ImageView imgUserHead;//我的头像
    @InjectView(R.id.imgRightArrow) ImageView imgRightArrow;

    @InjectView(R.id.llCompetitionLeaderboardTitle) LinearLayout llCompetitionLeaderboardTitle;//比赛排名 TITLE
    @InjectView(R.id.tvLeaderboardTime) TextView tvLeaderboardTime;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        getBundleCompetition();
        callbackEnrollUGC = new EnrollUGCCallback();
        callbackgetCompetition = new GetCompetitionDetailCallback();
        callbackMySelfRank = new MySelfRanCallbakc();
        competitionLeaderboardCacheListener = createCompetitionLeaderboardListener();
        portfolioCompactNewFetchListener = createPortfolioCompactNewFetchListener();
        userProfileCacheListener = createUserProfileFetchListener();

        adapter = new LeaderboardListAdapter(getActivity());
    }

    public void getBundleCompetition()
    {
        Bundle bundle = getArguments();
        if (bundle != null)
        {
            this.userCompetitionDTO = (UserCompetitionDTO) bundle.getSerializable(BUNDLE_COMPETITION_DTO);
            if (userCompetitionDTO != null)
            {
                competitionId = userCompetitionDTO.id;
            }
            else
            {
                this.competitionId = bundle.getInt(BUNDLE_COMPETITION_ID, 0);
            }
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        super.onCreateOptionsMenu(menu, inflater);
        setHeadViewMiddleMain("比赛详情");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.competition_detail_layout, container, false);
        ButterKnife.inject(this, view);
        if (userCompetitionDTO != null)
        {
            initView();
        }
        else
        {
            fetchCompetitionDetail();
        }
        return view;
    }

    private void noFoundCompetition()
    {
        THToast.show("没有找到比赛");
        popCurrentFragment();
    }

    private void initView()
    {
        includeMyPosition.setVisibility(userCompetitionDTO.isEnrolled ? View.VISIBLE : View.GONE);
        initCompetition();
        initRankList();
        getMySelfRank();
        tvCompetitionDetailMore.setVisibility(userCompetitionDTO.detailUrl == null ? View.GONE : View.VISIBLE);

        if (userCompetitionDTO != null && userCompetitionDTO.isEnrolled && userCompetitionDTO.isOngoing)
        {//比赛我参加了，并且还没结束。
            setHeadViewRight0("邀请好友");
        }
    }

    private void initRankList()
    {
        listRanks.setAdapter(adapter);
        listRanks.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
        listRanks.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>()
        {
            @Override public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView)
            {
                fetchCompetitionLeaderboard();
            }

            @Override public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView)
            {
                fetchCompetitionLeaderboardMore();
            }
        });
        listRanks.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override public void onItemClick(AdapterView<?> adapterView, View view, int i, long position)
            {
                LeaderboardUserDTO userDTO = (LeaderboardUserDTO) adapter.getItem((int) position);
                enterPortfolio(userDTO);
            }
        });
    }

    /*
    进入持仓页面
     */
    private void enterPortfolio(LeaderboardUserDTO userDTO)
    {
        Bundle bundle = new Bundle();
        bundle.putInt(PortfolioFragment.BUNLDE_SHOW_PROFILE_USER_ID, userDTO.id);
        bundle.putLong(PortfolioFragment.BUNDLE_LEADERBOARD_USER_MARK_ID, userDTO.lbmuId);
        bundle.putInt(PortfolioFragment.BUNLDE_COMPETITION_ID, userCompetitionDTO.id);
        pushFragment(PortfolioFragment.class, bundle);
    }

    /*
    进入持仓页面
     */
    private void enterPortfolio()
    {
        if (this.portfolioCompactDTO != null)
        {
            Bundle bundle = new Bundle();
            bundle.putSerializable(PortfolioFragment.BUNLDE_PORTFOLIO_DTO, this.portfolioCompactDTO);
            bundle.putInt(PortfolioFragment.BUNLDE_COMPETITION_ID, userCompetitionDTO.id);
            pushFragment(PortfolioFragment.class, bundle);
        }
    }

    private void initCompetition()
    {
        if (userCompetitionDTO != null)
        {
            //tvCompetitionDetailMore.setText(userCompetitionDTO.description);
            tvCompetitionIntro.setText(userCompetitionDTO.description);
            tvCompetitionCreator.setText(userCompetitionDTO.hostUserName);
            tvCompetitionPeriod.setText(userCompetitionDTO.getDisplayDatePeriod());
            tvCompetitionExchange.setText(userCompetitionDTO.getDisplayExchangeShort());
            tvGotoCompetition.setText(userCompetitionDTO.isEnrolled ? "去比赛" : "我要报名");
            if (!userCompetitionDTO.isOngoing)
            {
                tvGotoCompetition.setText("已结束");
                tvGotoCompetition.setEnabled(false);
                tvGotoCompetition.setTextColor(getResources().getColor(R.color.black));
            }
            tvCompetitionCreator.setOnClickListener(new View.OnClickListener()
            {
                @Override public void onClick(View view)
                {
                    openUserProfile(userCompetitionDTO.hostUserId);
                }
            });
        }

        if (adapter != null && adapter.getCount() == 0)
        {
            fetchCompetitionLeaderboard();
        }
    }

    private void openUserProfile(int userId)
    {
        if (userId >= 0)
        {
            Bundle bundle = new Bundle();
            bundle.putInt(UserMainPage.BUNDLE_USER_BASE_KEY, userId);
            pushFragment(UserMainPage.class, bundle);
        }
    }

    @Override public void onStop()
    {
        super.onStop();
    }

    @Override public void onDestroyView()
    {
        //ButterKnife.reset(this);
        detachCompetitionLeaderboardCache();
        detachPortfolioCompactNewCache();
        detachUserProfileCache();
        super.onDestroyView();
    }

    @Override public void onDestroy()
    {
        competitionLeaderboardCacheListener = null;
        portfolioCompactNewFetchListener = null;
        userProfileCacheListener = null;
        callbackEnrollUGC = null;
        callbackgetCompetition = null;
        callbackMySelfRank = null;
        super.onDestroy();
    }

    @Override public void onResume()
    {
        super.onResume();
        refreshStatus();
    }

    public void refreshStatus()
    {
        if(userCompetitionDTO == null)return;

        if (!userCompetitionDTO.isOngoing)
        {
            getMySelfRank();
        }
        else if (userCompetitionDTO.isEnrolled)//只有参加了才去拿portfolio
        {
            fetchPortfolioCompactNew();
        }
        fetchUserProfile();
        setLeaderboardHeadLine();
    }

    private void detachUserProfileCache()
    {
        userProfileCache.get().unregister(userProfileCacheListener);
    }

    protected void fetchUserProfile()
    {
        detachUserProfileCache();
        userProfileCache.get().register(currentUserId.toUserBaseKey(), userProfileCacheListener);
        userProfileCache.get().getOrFetchAsync(currentUserId.toUserBaseKey());
    }

    @OnClick(R.id.tvGotoCompetition)
    public void onGotoCompetitionClicked()
    {
        if (userCompetitionDTO.isOngoing)
        {
            if (!userCompetitionDTO.isEnrolled)
            {
                if (mUserProfileDTO != null && mUserProfileDTO.isVisitor)
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
                else
                {
                    //去报名
                    toJoinCompetition();//去报名
                }
            }
            else
            {
                toPlayCompetition();//去比赛
            }
        }
    }

    @OnClick(R.id.tvCompetitionDetailMore)
    public void onDetailClicked()
    {
        Bundle bundle = new Bundle();
        String url = userCompetitionDTO.detailUrl;

        bundle.putString(WebViewSimpleFragment.BUNDLE_WEBVIEW_URL, url);
        bundle.putString(WebViewSimpleFragment.BUNDLE_WEBVIEW_TITLE, userCompetitionDTO.name);
        pushFragment(WebViewSimpleFragment.class, bundle);
    }

    @OnClick(R.id.includeMyPosition)
    public void onClickMyPosition()
    {
        if (userCompetitionDTO.isOngoing)
        {
            Timber.d("进入我的持仓页面");
            enterPortfolio();
        }
    }

    //通过competitionId去获取比赛详情
    public void fetchCompetitionDetail()
    {
        if (competitionId == 0)
        {
            noFoundCompetition();
            return;
        }

        mTransactionDialog = progressDialogUtil.show(CompetitionDetailFragment.this.getActivity(),
                R.string.processing, R.string.alert_dialog_please_wait);
        competitionCacheLazy.get().getCompetitionDetail(competitionId, callbackgetCompetition);

    }

    public void toPlayCompetition()
    {
        Timber.d("去比赛");
        Bundle bundle = new Bundle();
        bundle.putInt(CompetitionSecuritySearchFragment.BUNLDE_COMPETITION_ID, userCompetitionDTO.id);
        pushFragment(CompetitionSecuritySearchFragment.class, bundle);
    }

    public void toJoinCompetition()
    {
        mTransactionDialog = progressDialogUtil.show(CompetitionDetailFragment.this.getActivity(),
                R.string.processing, R.string.alert_dialog_please_wait);
        competitionCacheLazy.get().enrollUGCompetition(userCompetitionDTO.id, callbackEnrollUGC);
    }

    public void getMySelfRank()
    {
        competitionCacheLazy.get().getMySelfRank(userCompetitionDTO.leaderboardId, currentUserId.toUserBaseKey().getUserId(), callbackMySelfRank);
    }



    protected class GetCompetitionDetailCallback implements retrofit.Callback<UserCompetitionDTO>
    {

        @Override
        public void success(UserCompetitionDTO userCompetitionDTO, Response response)
        {
            onFinish();
            if (response.getStatus() == 200)
            {
                CompetitionDetailFragment.this.userCompetitionDTO = userCompetitionDTO;
                initView();
                refreshStatus();
            }
        }

        @Override public void failure(RetrofitError retrofitError)
        {
            onFinish();
            if (retrofitError != null)
            {
                Timber.e(retrofitError, "Reporting the error to Crashlytics %s", retrofitError.getBody());
            }

            noFoundCompetition();
            //THException thException = new THException(retrofitError);
            //THToast.show(thException);
        }

        private void onFinish()
        {
            if (mTransactionDialog != null)
            {
                mTransactionDialog.dismiss();
            }
            mTransactionDialog.dismiss();
        }
    }

    protected class EnrollUGCCallback implements retrofit.Callback<UserCompetitionDTO>
    {

        @Override
        public void success(UserCompetitionDTO userCompetitionDTO, Response response)
        {
            onFinish();
            if (response.getStatus() == 200)
            {
                THToast.show("报名成功！");
                //popCurrentFragment();
                CompetitionDetailFragment.this.userCompetitionDTO = userCompetitionDTO;
                initCompetition();
                fetchPortfolioCompactNew();
            }
        }

        private void onFinish()
        {
            if (mTransactionDialog != null)
            {
                mTransactionDialog.dismiss();
            }
            mTransactionDialog.dismiss();
        }

        @Override public void failure(RetrofitError retrofitError)
        {
            onFinish();
            if (retrofitError != null)
            {
                Timber.e(retrofitError, "Reporting the error to Crashlytics %s", retrofitError.getBody());
            }
            THException thException = new THException(retrofitError);
            THToast.show(thException);
        }
    }

    private void displayMySelfRank(LeaderboardDTO leaderboardDTO)
    {
        int ordinaPosition = -1;
        try
        {
            ordinaPosition = leaderboardDTO.users.get(0).ordinalPosition;
        } catch (Exception e)
        {

        }

        if (!userCompetitionDTO.isOngoing)
        {
            //比赛结束后显示自己的最终ROI
            if (leaderboardDTO != null)
            {
                LeaderboardUserDTO dto = null;
                try
                {
                    dto = leaderboardDTO.users.get(0);
                } catch (Exception e)
                {

                }

                if (dto != null)
                {
                    THSignedNumber roi = THSignedPercentage.builder(leaderboardDTO.users.get(0).roiInPeriod * 100)
                            .withSign()
                            .signTypeArrow()
                            .build();
                    tvUserExtraValue.setText(roi.toString());
                    tvUserExtraValue.setTextColor(getResources().getColor(roi.getColorResId()));
                }
            }
        }

        if (ordinaPosition != -1)
        {
            if (ordinaPosition < 3)
            {
                tvUserRank.setText("");
                tvUserRank.setBackgroundResource(LeaderboardListAdapter.RANK_RES[ordinaPosition]);
            }
            else if (ordinaPosition > 1000)
            {
                tvUserRank.setText(" - - ");
            }
            else
            {
                tvUserRank.setBackgroundDrawable(null);
                tvUserRank.setText(String.valueOf(ordinaPosition + 1));
            }
        }
        else
        {
            tvUserRank.setText(" - - ");
        }
    }

    protected class MySelfRanCallbakc implements retrofit.Callback<LeaderboardDTO>
    {
        @Override
        public void success(LeaderboardDTO leaderboardDTO, Response response)
        {
            onFinish();
            if (response.getStatus() == 200)
            {
                Timber.d("");
                displayMySelfRank(leaderboardDTO);
            }
        }

        private void onFinish()
        {

        }

        @Override public void failure(RetrofitError retrofitError)
        {
            onFinish();
            if (retrofitError != null)
            {
                Timber.e(retrofitError, "Reporting the error to Crashlytics %s", retrofitError.getBody());
            }
        }
    }

    protected DTOCacheNew.Listener<CompetitionLeaderboardId, CompetitionLeaderboardDTO> createCompetitionLeaderboardListener()
    {
        return new CompetitionLeaderboardCacheListener();
    }

    protected class CompetitionLeaderboardCacheListener implements DTOCacheNew.Listener<CompetitionLeaderboardId, CompetitionLeaderboardDTO>
    {
        @Override public void onDTOReceived(@NotNull CompetitionLeaderboardId key, @NotNull final CompetitionLeaderboardDTO value)
        {
            competitionLeaderboardDTO = value;
            LeaderboardUserDTOList userDTOs = competitionLeaderboardDTO.leaderboard.users;
            if (userDTOs != null)
            {
                setListData(key, userDTOs);
            }
            //competitionAdapter.setCompetitionLeaderboardDTO(value);
            //competitionAdapter.notifyDataSetChanged();
            //updateCurrentRankHeaderView();
        }

        @Override public void onErrorThrown(@NotNull CompetitionLeaderboardId key, @NotNull Throwable error)
        {
            Timber.d("CompetitionLeaderboardCacheListener failure!");
        }
    }

    protected void detachCompetitionLeaderboardCache()
    {
        competitionLeaderboardCache.unregister(competitionLeaderboardCacheListener);
    }

    protected void fetchCompetitionLeaderboard()
    {
        detachCompetitionLeaderboardCache();
        currentPage = 1;
        CompetitionLeaderboardId key = competitionDTOUtil.getCompetitionLeaderboardId(new ProviderId(CompetitionUtils.UGC_PROVIDER_ID),
                new CompetitionId(userCompetitionDTO.id), currentPage, PER_PAGE);
        competitionLeaderboardCache.register(key, competitionLeaderboardCacheListener);
        competitionLeaderboardCache.getOrFetchAsync(key);
    }

    public void setLeaderboardHeadLine()
    {
        if (!userCompetitionDTO.isOngoing)
        {
            imgRightArrow.setVisibility(View.GONE);
        }

        if (adapter.getCount() != 0)
        {
            llCompetitionLeaderboardTitle.setVisibility(View.VISIBLE);
            tvLeaderboardTime.setText("(截止至" + competitionLeaderboardDTO.leaderboard.getMarkUTCString() + ")");
        }
    }

    protected void fetchCompetitionLeaderboardMore()
    {
        detachCompetitionLeaderboardCache();
        CompetitionLeaderboardId key = competitionDTOUtil.getCompetitionLeaderboardId(new ProviderId(CompetitionUtils.UGC_PROVIDER_ID),
                new CompetitionId(userCompetitionDTO.id), currentPage, PER_PAGE);
        competitionLeaderboardCache.register(key, competitionLeaderboardCacheListener);
        competitionLeaderboardCache.getOrFetchAsync(key);
    }

    private void setListData(CompetitionLeaderboardId key, LeaderboardUserDTOList listData)
    {
        if (key.page == PagedLeaderboardKey.FIRST_PAGE)
        {
            adapter.setListData(listData);
            adapter.setLeaderboardType(LeaderboardDefKeyKnowledge.COMPETITION);
            if (listData != null && listData.size() > 0)
            {
                setLeaderboardHeadLine();
            }
        }
        else
        {
            if (adapter != null)
            {
                adapter.addItems(listData);
            }
        }
        listRanks.onRefreshComplete();

        //如果返回数据已经为空了，说明没有了下一页。
        if (listData.size() > 0)
        {
            currentPage += 1;
        }
        else
        {

        }
        adapter.notifyDataSetChanged();
    }

    private void detachPortfolioCompactNewCache()
    {
        portfolioCompactNewCache.unregister(portfolioCompactNewFetchListener);
    }

    private void fetchPortfolioCompactNew()
    {
        detachPortfolioCompactNewCache();
        PortfolioId key = new PortfolioId(userCompetitionDTO.id);
        portfolioCompactNewCache.register(key, portfolioCompactNewFetchListener);
        portfolioCompactNewCache.getOrFetchAsync(key);
    }

    protected DTOCacheNew.Listener<PortfolioId, PortfolioCompactDTO> createPortfolioCompactNewFetchListener()
    {
        return new BasePurchaseManagementPortfolioCompactNewFetchListener();
    }

    protected class BasePurchaseManagementPortfolioCompactNewFetchListener implements DTOCacheNew.Listener<PortfolioId, PortfolioCompactDTO>
    {
        protected BasePurchaseManagementPortfolioCompactNewFetchListener()
        {
        }

        @Override public void onDTOReceived(@NotNull PortfolioId key, @NotNull PortfolioCompactDTO value)
        {
            linkWith(value);
        }

        @Override public void onErrorThrown(@NotNull PortfolioId key, @NotNull Throwable error)
        {
            //THToast.show(R.string.error_fetch_portfolio_list_info);
        }
    }

    protected DTOCacheNew.Listener<UserBaseKey, UserProfileDTO> createUserProfileFetchListener()
    {
        return new UserProfileFetchListener();
    }

    protected class UserProfileFetchListener implements DTOCacheNew.Listener<UserBaseKey, UserProfileDTO>
    {
        @Override
        public void onDTOReceived(@NotNull UserBaseKey key, @NotNull UserProfileDTO value)
        {
            linkWith(value);
        }

        @Override public void onErrorThrown(@NotNull UserBaseKey key, @NotNull Throwable error)
        {

        }
    }

    private void linkWith(UserProfileDTO value)
    {
        if (value == null) return;
        mUserProfileDTO = value;
        tvUserName.setText(value.displayName);

        picasso.get()
                .load(value.picture)
                .placeholder(R.drawable.superman_facebook)
                .error(R.drawable.superman_facebook)
                .into(imgUserHead);
    }

    private void linkWith(PortfolioCompactDTO value)
    {
        if (value == null) return;
        this.portfolioCompactDTO = value;
        this.portfolioCompactDTO.userId = currentUserId.toUserBaseKey().getUserId();
        if (value != null && value.roiSinceInception != null && userCompetitionDTO.isOngoing)
        {
            THSignedNumber roi = THSignedPercentage.builder(value.roiSinceInception * 100)
                    .withSign()
                    .signTypeArrow()
                    .build();
            tvUserExtraValue.setText(roi.toString());
            tvUserExtraValue.setTextColor(getResources().getColor(roi.getColorResId()));
        }
    }

    public void inviteFriendsToCompetition()
    {
        Bundle bundle = new Bundle();
        bundle.putSerializable(DiscoveryDiscussSendFragment.BUNDLE_KEY_COMPETITION, userCompetitionDTO);
        pushFragment(DiscoveryDiscussSendFragment.class, bundle);
    }

    @Override public void onClickHeadRight0()
    {
        mShareSheetTitleCache.set(getString(R.string.share_detial_contest,
                userCompetitionDTO.name, currentUserId.get().toString(), userCompetitionDTO.id));
        ShareSheetDialogLayout contentView = (ShareSheetDialogLayout) LayoutInflater.from(getActivity())
                .inflate(R.layout.share_sheet_local_dialog_layout, null);
        contentView.setLocalSocialClickedListener(
                new ShareSheetDialogLayout.OnLocalSocialClickedListener()
                {
                    @Override public void onShareRequestedClicked()
                    {
                        inviteFriendsToCompetition();
                        if (mShareSheetDialog != null)
                        {
                            mShareSheetDialog.dismiss();
                        }
                    }
                });
        mShareSheetDialog = THDialog.showUpDialog(getActivity(), contentView);
    }
}
