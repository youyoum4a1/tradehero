package com.tradehero.chinabuild.fragment.competition;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.*;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import com.handmark.pulltorefresh.library.pulltorefresh.PullToRefreshBase;
import com.handmark.pulltorefresh.library.pulltorefresh.PullToRefreshListView;
import com.squareup.picasso.Picasso;
import com.tradehero.chinabuild.cache.PortfolioCompactNewCache;
import com.tradehero.chinabuild.data.UserCompetitionDTO;
import com.tradehero.chinabuild.data.sp.THSharePreferenceManager;
import com.tradehero.chinabuild.fragment.portfolio.PortfolioFragment;
import com.tradehero.chinabuild.fragment.web.WebViewFragment;
import com.tradehero.common.persistence.DTOCacheNew;
import com.tradehero.common.persistence.prefs.StringPreference;
import com.tradehero.common.utils.THToast;
import com.tradehero.common.widget.BetterViewAnimator;
import com.tradehero.th.R;
import com.tradehero.th.activities.AuthenticationActivity;
import com.tradehero.th.activities.DashboardActivity;
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
import com.tradehero.th.base.DashboardNavigatorActivity;
import com.tradehero.th.fragments.DashboardNavigator;
import com.tradehero.th.misc.exception.THException;
import com.tradehero.th.models.leaderboard.key.LeaderboardDefKeyKnowledge;
import com.tradehero.th.models.number.THSignedNumber;
import com.tradehero.th.models.number.THSignedPercentage;
import com.tradehero.th.persistence.competition.CompetitionCache;
import com.tradehero.th.persistence.leaderboard.CompetitionLeaderboardCache;
import com.tradehero.th.persistence.prefs.ShareSheetTitleCache;
import com.tradehero.th.persistence.user.UserProfileCache;
import com.tradehero.th.utils.AlertDialogUtil;
import com.tradehero.th.utils.DaggerUtils;
import com.tradehero.th.utils.ProgressDialogUtil;
import com.tradehero.th.utils.metrics.Analytics;
import com.tradehero.th.utils.metrics.AnalyticsConstants;
import com.tradehero.th.utils.metrics.events.MethodEvent;
import com.tradehero.th.widget.GuideView;
import com.tradehero.th.widget.TradeHeroProgressBar;
import dagger.Lazy;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

import javax.inject.Inject;

/**
 * Created by huhaiping on 14-9-9. 比赛详情页
 */
public class CompetitionDetailFragment extends Fragment
{
    public static final String BUNDLE_COMPETITION_DTO = "bundle_competition_dto";
    public static final String BUNDLE_COMPETITION_ID = "bundle_competition_id";

    @Inject Lazy<Picasso> picasso;
    @Inject CompetitionDTOUtil competitionDTOUtil;
    @Inject CompetitionLeaderboardCache competitionLeaderboardCache;
    protected DTOCacheNew.Listener<CompetitionLeaderboardId, CompetitionLeaderboardDTO> competitionLeaderboardCacheListener;

    @Inject Lazy<CompetitionCache> competitionCacheLazy;
    private Callback<UserCompetitionDTO> callbackEnrollUGC;
    private Callback<UserCompetitionDTO> callbackGetCompetition;
    private Callback<LeaderboardDTO> callbackMySelfRank;

    @Inject Analytics analytics;

    @Inject protected PortfolioCompactNewCache portfolioCompactNewCache;
    private DTOCacheNew.Listener<PortfolioId, PortfolioCompactDTO> portfolioCompactNewFetchListener;

    public UserCompetitionDTO userCompetitionDTO;
    public int competitionId;
    @Inject ProgressDialogUtil progressDialogUtil;

    @Inject protected AlertDialogUtil alertDialogUtil;

    private PortfolioCompactDTO portfolioCompactDTO;

    protected CompetitionLeaderboardDTO competitionLeaderboardDTO;
    @Inject CurrentUserId currentUserId;
    @Inject Lazy<UserProfileCache> userProfileCache;
    private DTOCacheNew.Listener<UserBaseKey, UserProfileDTO> userProfileCacheListener;
    private UserProfileDTO mUserProfileDTO;

    @Inject @ShareSheetTitleCache StringPreference mShareSheetTitleCache;

    @InjectView(R.id.tvCompetitionDetailMore) TextView tvCompetitionDetailMore;//比赛详情
    @InjectView(R.id.tvCompetitionExchange) TextView tvCompetitionExchange;//比赛交易所
    @InjectView(R.id.tvCompetitionPeriod) TextView tvCompetitionPeriod;//比赛周期
    @InjectView(R.id.tvCompetitionIntro) TextView tvCompetitionIntro;//比赛介绍
    @InjectView(R.id.imageview_edit_competition_intro) ImageView ivEditCompetitionIntro;

    private RelativeLayout includeMyPosition;//我的比赛数据行
    private TextView tvUserRank;//我的排名
    private TextView tvUserExtraValue;//我的收益率
    private TextView tvUserName;//我的名字
    private ImageView imgUserHead;//我的头像
    private LinearLayout llCompetitionLeaderboardTitle;//比赛排名 TITLE
    private TextView tvLeaderboardTime;
    private TextView tvGotoCompetition;
    private TextView tvJoinCompetition;
    private int tvJoinCompetitionHeight;
    private int tvJoinCompetitionWidth;
    private int tvJoinCompetitionY;
    private int guideCompetitionEditIntroHeight;
    private RelativeLayout layoutJoinCompetition;

    @InjectView(R.id.btnCollegeSelect) Button btnCollegeSelect;
    @InjectView(R.id.tradeheroprogressbar_competition_detail) TradeHeroProgressBar progressBar;
    @InjectView(R.id.bvaViewAll) BetterViewAnimator betterViewAnimator;
    @InjectView(R.id.rlRankAll) RelativeLayout rlRankAll;
    @InjectView(R.id.imgEmpty) ImageView imgEmpty;
    @InjectView(R.id.listRanks)PullToRefreshListView listRanks;//比赛排名
    private LeaderboardListAdapter adapter;
    private int currentPage = 1;
    private int PER_PAGE = 20;

    private boolean isShowHeadLine = false;

    private LinearLayout mRefreshView;

    //Edit introduction of Competition
    private Dialog editCompetitionDlg;
    private EditText etCompetitionIntro;
    private TextView tvCancel;
    private TextView tvConfirm;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getBundleCompetition();
        callbackEnrollUGC = new EnrollUGCCallback();
        callbackGetCompetition = new GetCompetitionDetailCallback();
        callbackMySelfRank = new MySelfRanCallback();
        competitionLeaderboardCacheListener = createCompetitionLeaderboardListener();
        portfolioCompactNewFetchListener = createPortfolioCompactNewFetchListener();
        userProfileCacheListener = createUserProfileFetchListener();
    }

    @Override public void onAttach(Activity activity){
        super.onAttach(activity);
        DaggerUtils.inject(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.competition_detail_layout, container, false);
        ButterKnife.inject(this, view);
        initResources();
        mRefreshView = (LinearLayout) inflater.inflate(R.layout.competition_detail_listview_header, null);
        tvCompetitionDetailMore.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);

        listRanks.setEmptyView(imgEmpty);
        adapter = new LeaderboardListAdapter(getActivity());
        initRankList();
        initRoot(mRefreshView);

        includeMyPosition.setVisibility(isShowHeadLine ? View.VISIBLE : View.GONE);

        if (userCompetitionDTO != null) {
            initCompetitionTitle();
        }
        fetchCompetitionDetail();

        if (adapter.getCount() == 0) {
            betterViewAnimator.setDisplayedChildByLayoutId(R.id.tradeheroprogressbar_competition_detail);
            progressBar.startLoading();
        } else {
            betterViewAnimator.setDisplayedChildByLayoutId(R.id.rlRankAll);
        }
        tvCompetitionIntro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(creatorIsMe()) {
                    showDlgEditCompetitionIntro();
                }
            }
        });
        return view;
    }

    public void initRoot(View view) {
        includeMyPosition = (RelativeLayout) view.findViewById(R.id.includeMyPosition);//我的比赛数据行
        tvUserRank = (TextView) view.findViewById(R.id.tvUserRank);//我的排名
        tvUserExtraValue = (TextView) view.findViewById(R.id.tvUserExtraValue);//我的收益率
        tvUserName = (TextView) view.findViewById(R.id.tvUserName);//我的名字
        imgUserHead = (ImageView) view.findViewById(R.id.imgUserHead);//我的头像
        tvGotoCompetition = (TextView)view.findViewById(R.id.tvGotoCompetition);
        tvGotoCompetition.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(userCompetitionDTO==null){
                    return;
                }
                onGotoCompetitionClicked();
            }
        });
        tvJoinCompetition = (TextView)view.findViewById(R.id.tvJoinCompetition);
        tvJoinCompetition.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onJoinCompetitionClicked();
            }
        });
        layoutJoinCompetition = (RelativeLayout)view.findViewById(R.id.relativelayout_join_competition);
        llCompetitionLeaderboardTitle = (LinearLayout) view.findViewById(R.id.llCompetitionLeaderboardTitle);//比赛排名 TITLE
        tvLeaderboardTime = (TextView) view.findViewById(R.id.tvLeaderboardTime);
        includeMyPosition.setOnClickListener(new View.OnClickListener()
        {
            @Override public void onClick(View view)
            {
                onClickMyPosition();
            }
        });
    }

    public void getBundleCompetition() {
        Bundle bundle = getArguments();
        if (bundle != null) {
            this.userCompetitionDTO = (UserCompetitionDTO) bundle.getSerializable(BUNDLE_COMPETITION_DTO);
            if (userCompetitionDTO != null) {
                competitionId = userCompetitionDTO.id;
            } else {
                this.competitionId = bundle.getInt(BUNDLE_COMPETITION_ID, 0);
            }
        }
    }

    private void noFoundCompetition() {
        if (getActivity() != null) {
            popCurrentFragment();
        }
    }

    private void initView() {
        includeMyPosition.setVisibility(userCompetitionDTO.isEnrolled ? View.VISIBLE : View.GONE);
        isShowHeadLine = userCompetitionDTO.isEnrolled;
        initCompetition();
        listRanks.setMode(PullToRefreshBase.Mode.BOTH);
        getMySelfRank();
        tvCompetitionDetailMore.setVisibility(userCompetitionDTO.detailUrl == null ? View.GONE : View.VISIBLE);
    }

    private void initResources(){
        tvJoinCompetitionHeight = (int)getActivity().getResources().getDimension(R.dimen.btn_join_competition_height);
        tvJoinCompetitionWidth = (int)getActivity().getResources().getDimension(R.dimen.btn_join_competition_width);
        tvJoinCompetitionY = (int)getActivity().getResources().getDimension(R.dimen.btn_join_competition_y);
        guideCompetitionEditIntroHeight = (int)getActivity().getResources().getDimension(R.dimen.guide_competition_edit_intro);
    }

    private void initRankList() {
        ListView lv = listRanks.getRefreshableView();
        lv.addHeaderView(mRefreshView);
        listRanks.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            @Override public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                fetchCompetitionLeaderboard();
                refreshStatus();
            }

            @Override public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                fetchCompetitionLeaderboardMore();
            }
        });
        listRanks.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long position) {
                if (position >= 0) {
                    LeaderboardUserDTO userDTO = (LeaderboardUserDTO) adapter.getItem((int) position);
                    enterPortfolio(userDTO);
                    analytics.addEvent(new MethodEvent(AnalyticsConstants.BUTTON_COMPETITION_DETAIL_RANK_POSITION, "" + position));
                }
            }
        });
        listRanks.setAdapter(adapter);
    }

    /*
    进入持仓页面
     */
    private void enterPortfolio(LeaderboardUserDTO userDTO) {
        Bundle bundle = new Bundle();
        bundle.putInt(PortfolioFragment.BUNLDE_SHOW_PROFILE_USER_ID, userDTO.id);
        bundle.putLong(PortfolioFragment.BUNDLE_LEADERBOARD_USER_MARK_ID, userDTO.lbmuId);
        bundle.putInt(PortfolioFragment.BUNLDE_COMPETITION_ID, userCompetitionDTO.id);
        pushFragment(PortfolioFragment.class, bundle);
    }

    /*
    进入持仓页面
     */
    private void enterPortfolio() {
        if (this.portfolioCompactDTO != null) {
            Bundle bundle = new Bundle();
            bundle.putSerializable(PortfolioFragment.BUNLDE_PORTFOLIO_DTO, this.portfolioCompactDTO);
            bundle.putInt(PortfolioFragment.BUNLDE_COMPETITION_ID, userCompetitionDTO.id);
            pushFragment(PortfolioFragment.class, bundle);
        }
    }

    private void initCompetitionTitle() {
        if(creatorIsMe()){
            ivEditCompetitionIntro.setVisibility(View.VISIBLE);
        }else{
            ivEditCompetitionIntro.setVisibility(View.GONE);
        }
        tvCompetitionIntro.setText(userCompetitionDTO.getHostUserName() + ": " + userCompetitionDTO.description);
        tvCompetitionPeriod.setText(userCompetitionDTO.getDisplayDatePeriod());
        tvCompetitionExchange.setText(userCompetitionDTO.getDisplayExchangeShort());
    }

    private void initCompetition() {
        if (userCompetitionDTO != null) {
            initCompetitionTitle();
            if(userCompetitionDTO.isEnrolled ){
                layoutJoinCompetition.setVisibility(View.GONE);
                includeMyPosition.setVisibility(View.VISIBLE);
                tvGotoCompetition.setText("去比赛");
                if (!userCompetitionDTO.isOngoing) {
                    tvGotoCompetition.setText("已结束");
                    tvGotoCompetition.setEnabled(false);
                    tvGotoCompetition.setTextColor(getActivity().getResources().getColor(R.color.black));
                }
            }else{
                layoutJoinCompetition.setVisibility(View.VISIBLE);
                includeMyPosition.setVisibility(View.GONE);
            }
        }

        if (adapter != null && adapter.getCount() == 0) {
            fetchCompetitionLeaderboard();
        }
        setSchollView();
    }

    @Override public void onDestroyView() {
        detachCompetitionLeaderboardCache();
        detachPortfolioCompactNewCache();
        detachUserProfileCache();
        super.onDestroyView();
    }

    @Override public void onPause() {
        super.onPause();
        if (listRanks != null) {
            listRanks.onRefreshComplete();
        }
    }

    @Override public void onDestroy() {
        competitionLeaderboardCacheListener = null;
        portfolioCompactNewFetchListener = null;
        userProfileCacheListener = null;
        callbackEnrollUGC = null;
        callbackGetCompetition = null;
        callbackMySelfRank = null;
        super.onDestroy();
    }

    @Override public void onResume() {
        super.onResume();
        if(isShowEditIntroCompetitionGuideView()){
            showEditIntroCompetitionGuideView();
        }else if(THSharePreferenceManager.isGuideAvailable(getActivity(), THSharePreferenceManager.GUIDE_COMPETITION_JOIN)) {
            showJoinCompetitionGuideView();
        }
        setLeaderboardHeadLine();
    }

    private void showJoinCompetitionGuideView() {
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if(layoutJoinCompetition.getVisibility() != View.VISIBLE){
                    return;
                }
                //Show Guide View
                int width = tvJoinCompetitionWidth;
                int height = tvJoinCompetitionHeight;
                int position_x = ((DashboardActivity) getActivity()).SCREEN_W /2;
                int position_y = tvJoinCompetitionY + ((DashboardActivity) getActivity()).getStatusBarHeight();
                int radius = (int) Math.sqrt(width * width / 4 + height * height / 4);
                ((DashboardActivity) getActivity()).showGuideView(position_x,
                        position_y, radius, GuideView.TYPE_GUIDE_COMPETITION_JOIN);
            }
        }, 1000);
    }

    private void showEditIntroCompetitionGuideView(){
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                ((DashboardActivity) getActivity()).showGuideView(guideCompetitionEditIntroHeight, GuideView.TYPE_GUIDE_COMPETITION_EDIT);
            }
        }, 1000);
    }

    private boolean isShowEditIntroCompetitionGuideView(){
        if(userCompetitionDTO == null){
            return false;
        }
        if(creatorIsMe() && THSharePreferenceManager.isGuideAvailable(getActivity(), THSharePreferenceManager.GUIDE_COMPETITION_INTRO_EDIT)){
            return true;
        }
        return false;
    }



    private void refreshStatus() {
        if (userCompetitionDTO == null){
            return;
        }
        getMySelfRank();
        //只有参加了才去拿portfolio
        if (userCompetitionDTO.isEnrolled){
            fetchPortfolioCompactNew();
        }
        fetchUserProfile();
    }

    private void detachUserProfileCache()
    {
        userProfileCache.get().unregister(userProfileCacheListener);
    }

    protected void fetchUserProfile() {
        detachUserProfileCache();
        userProfileCache.get().register(currentUserId.toUserBaseKey(), userProfileCacheListener);
        userProfileCache.get().getOrFetchAsync(currentUserId.toUserBaseKey());
    }

    //通过competitionId去获取比赛详情
    public void fetchCompetitionDetail() {
        if (competitionId == 0) {
            noFoundCompetition();
            return;
        }
        competitionCacheLazy.get().getCompetitionDetail(competitionId, callbackGetCompetition);
    }

    public void toPlayCompetition()
    {
        if (userCompetitionDTO == null)
        {
            THToast.show("没有找到比赛");
            return;
        }
        Bundle bundle = new Bundle();
        bundle.putInt(CompetitionSecuritySearchFragment.BUNLDE_COMPETITION_ID, userCompetitionDTO.id);
        pushFragment(CompetitionSecuritySearchFragment.class, bundle);
        analytics.addEventAuto(new MethodEvent(AnalyticsConstants.CHINA_BUILD_BUTTON_CLICKED, AnalyticsConstants.BUTTON_COMPETITION_DETAIL_GOTO));
    }

    public void toJoinCompetition() {
        competitionCacheLazy.get().enrollUGCompetition(userCompetitionDTO.id, callbackEnrollUGC);
        analytics.addEventAuto(new MethodEvent(AnalyticsConstants.CHINA_BUILD_BUTTON_CLICKED, AnalyticsConstants.BUTTON_COMPETITION_DETAIL_JOIN));
    }

    public void getMySelfRank() {
        if (leaderboardDTO == null) {
            competitionCacheLazy.get().getMySelfRank(userCompetitionDTO.leaderboardId, currentUserId.toUserBaseKey().getUserId(), callbackMySelfRank);
        } else {
            displayMySelfRank(leaderboardDTO);
        }
    }

    protected class GetCompetitionDetailCallback implements retrofit.Callback<UserCompetitionDTO> {

        @Override
        public void success(UserCompetitionDTO userCompetitionDTO, Response response) {
            onFinish();
            if (response.getStatus() == 200) {
                if (getActivity() == null) return;
                CompetitionDetailFragment.this.userCompetitionDTO = userCompetitionDTO;
                initView();
                refreshStatus();
            }
        }

        @Override public void failure(RetrofitError retrofitError) {
            onFinish();
            noFoundCompetition();
        }

        private void onFinish() {
            if (progressBar != null) {
                progressBar.stopLoading();
            }
        }
    }

    protected class EnrollUGCCallback implements retrofit.Callback<UserCompetitionDTO> {
        @Override
        public void success(UserCompetitionDTO userCompetitionDTO, Response response) {
            if (response.getStatus() == 200) {
                THToast.show("报名成功！");
                CompetitionDetailFragment.this.userCompetitionDTO = userCompetitionDTO;
                initCompetition();
                fetchPortfolioCompactNew();
            }
        }

        @Override public void failure(RetrofitError retrofitError)  {
            THException thException = new THException(retrofitError);
            THToast.show(thException);
        }
    }

    private LeaderboardDTO leaderboardDTO;

    private void displayMySelfRank(LeaderboardDTO leaderboardDTO) {
        this.leaderboardDTO = leaderboardDTO;
        int ordinaPosition = -1;
        try {
            ordinaPosition = leaderboardDTO.users.get(0).ordinalPosition;
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (ordinaPosition != -1) {
            if (ordinaPosition < 3) {
                tvUserRank.setText("");
                tvUserRank.setBackgroundResource(LeaderboardListAdapter.RANK_RES[ordinaPosition]);
            } else if (ordinaPosition > 1000) {
                tvUserRank.setText(">1K ");
            } else {
                tvUserRank.setBackgroundDrawable(null);
                tvUserRank.setText(String.valueOf(ordinaPosition + 1));
            }
        } else {
            tvUserRank.setText(" - - ");
        }
    }

    protected class MySelfRanCallback implements retrofit.Callback<LeaderboardDTO> {
        @Override
        public void success(LeaderboardDTO leaderboardDTO, Response response) {
            if (response.getStatus() == 200) {
                if (getActivity() == null) return;
                displayMySelfRank(leaderboardDTO);
            }
        }

        @Override public void failure(RetrofitError retrofitError) { }
    }

    protected DTOCacheNew.Listener<CompetitionLeaderboardId, CompetitionLeaderboardDTO> createCompetitionLeaderboardListener() {
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
            onFinish();
        }

        @Override public void onErrorThrown(@NotNull CompetitionLeaderboardId key, @NotNull Throwable error)
        {
            onFinish();
        }

        public void onFinish()
        {
            betterViewAnimator.setDisplayedChildByLayoutId(R.id.rlRankAll);
            listRanks.onRefreshComplete();
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

    public void setLeaderboardHeadLine() {
        if (adapter.hasLeaderboard) {
            llCompetitionLeaderboardTitle.setVisibility(View.VISIBLE);
            tvLeaderboardTime.setText("(截止至" + competitionLeaderboardDTO.leaderboard.getMarkUTCString() + ")");
        } else {
            llCompetitionLeaderboardTitle.setVisibility(View.GONE);
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

    private void setListData(CompetitionLeaderboardId key, LeaderboardUserDTOList listData) {
        if (key.page == PagedLeaderboardKey.FIRST_PAGE) {
            adapter.setListData(listData);
            adapter.setLeaderboardType(
                    userCompetitionDTO.isForSchool ? LeaderboardDefKeyKnowledge.COMPETITION_FOR_SCHOOL : LeaderboardDefKeyKnowledge.COMPETITION);
            if (listData != null && listData.size() > 0) {
                setLeaderboardHeadLine();
            }
        } else {
            if (adapter != null) {
                adapter.addItems(listData);
            }
        }

        listRanks.onRefreshComplete();

        //如果返回数据已经为空了，说明没有了下一页。
        if (listData.size() > 0) {
            currentPage += 1;
        }
        adapter.notifyDataSetChanged();
    }

    private void detachPortfolioCompactNewCache() {
        portfolioCompactNewCache.unregister(portfolioCompactNewFetchListener);
    }

    private void fetchPortfolioCompactNew() {
        detachPortfolioCompactNewCache();
        PortfolioId key = new PortfolioId(userCompetitionDTO.id);
        portfolioCompactNewCache.register(key, portfolioCompactNewFetchListener);
        portfolioCompactNewCache.getOrFetchAsync(key);
    }

    protected DTOCacheNew.Listener<PortfolioId, PortfolioCompactDTO> createPortfolioCompactNewFetchListener() {
        return new BasePurchaseManagementPortfolioCompactNewFetchListener();
    }

    protected class BasePurchaseManagementPortfolioCompactNewFetchListener implements DTOCacheNew.Listener<PortfolioId, PortfolioCompactDTO> {
        protected BasePurchaseManagementPortfolioCompactNewFetchListener() { }

        @Override public void onDTOReceived(@NotNull PortfolioId key, @NotNull PortfolioCompactDTO value) {
            linkWith(value);
        }

        @Override public void onErrorThrown(@NotNull PortfolioId key, @NotNull Throwable error) { }
    }

    protected DTOCacheNew.Listener<UserBaseKey, UserProfileDTO> createUserProfileFetchListener() {
        return new UserProfileFetchListener();
    }

    protected class UserProfileFetchListener implements DTOCacheNew.Listener<UserBaseKey, UserProfileDTO> {
        @Override
        public void onDTOReceived(@NotNull UserBaseKey key, @NotNull UserProfileDTO value)
        {
            linkWith(value);
        }
        @Override
        public void onErrorThrown(@NotNull UserBaseKey key, @NotNull Throwable error) { }
    }

    private void linkWith(UserProfileDTO value) {
        if (value == null) return;
        mUserProfileDTO = value;
        tvUserName.setText(value.getDisplayName());
        picasso.get()
                .load(value.picture)
                .placeholder(R.drawable.superman_facebook)
                .error(R.drawable.superman_facebook)
                .into(imgUserHead);
        //设置是否显示 高校选择按钮
        setSchollView();
    }

    public void setSchollView() {
        if (mUserProfileDTO == null || userCompetitionDTO == null) return;
        boolean showSchoolButton = false;
        if (userCompetitionDTO != null && userCompetitionDTO.isEnrolled && userCompetitionDTO.isOngoing && userCompetitionDTO.isForSchool && (!mUserProfileDTO.isHaveSchool())) {
            showSchoolButton = true;
        }
        btnCollegeSelect.setVisibility(showSchoolButton ? View.VISIBLE : View.GONE);
    }

    private void linkWith(PortfolioCompactDTO value)
    {
        if (value == null) return;
        this.portfolioCompactDTO = value;
        this.portfolioCompactDTO.userId = currentUserId.toUserBaseKey().getUserId();
        if (value != null && value.roiSinceInception != null /*&& userCompetitionDTO.isOngoing*/)
        {
            THSignedNumber roi = THSignedPercentage.builder(value.roiSinceInception * 100)
                    .withSign()
                    .signTypeArrow()
                    .build();
            tvUserExtraValue.setText(roi.toString());
            tvUserExtraValue.setTextColor(getResources().getColor(roi.getColorResId()));
        }
    }

    @OnClick(R.id.btnCollegeSelect)
    public void onCollegeSelect()
    {
        pushFragment(CompetitionCollegeFragment.class, null);
    }

    private void onGotoCompetitionClicked() {
        if(mUserProfileDTO==null){
            return;
        }
        if (userCompetitionDTO.isOngoing) {
            if (mUserProfileDTO.isVisitor) {
                alertDialogUtil.popWithOkCancelButton(getActivity(), R.string.app_name,
                        R.string.guest_user_dialog_summary,
                        R.string.ok, R.string.cancel, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (getActivity() == null) {
                                    return;
                                }
                                Intent gotoAuthticationIntent = new Intent(getActivity(), AuthenticationActivity.class);
                                startActivity(gotoAuthticationIntent);
                                getActivity().finish();
                            }
                        });
            }else{
                toPlayCompetition();//去比赛
            }
        }
    }

    private void onJoinCompetitionClicked(){
        if(mUserProfileDTO==null){
            return;
        }
        if (userCompetitionDTO.isOngoing) {
            if (mUserProfileDTO.isVisitor) {
                alertDialogUtil.popWithOkCancelButton(getActivity(), R.string.app_name,
                        R.string.guest_user_dialog_summary,
                        R.string.ok, R.string.cancel, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (getActivity() == null) {
                                    return;
                                }
                                Intent gotoAuthticationIntent = new Intent(getActivity(), AuthenticationActivity.class);
                                startActivity(gotoAuthticationIntent);
                                getActivity().finish();
                            }
                        });
            }else{
                toJoinCompetition();//报名比赛
            }
        }
    }

    @OnClick(R.id.tvCompetitionDetailMore)
    public void onDetailClicked() {
        Bundle bundle = new Bundle();
        String url = userCompetitionDTO.detailUrl;
        bundle.putString(WebViewFragment.BUNDLE_WEBVIEW_URL, url);
        bundle.putString(WebViewFragment.BUNDLE_WEBVIEW_TITLE, userCompetitionDTO.name);
        pushFragment(WebViewFragment.class, bundle);
    }

    public void onClickMyPosition() {
        if (userCompetitionDTO.isOngoing) {
            enterPortfolio();
        }
    }

    private Fragment pushFragment(@NotNull Class fragmentClass, Bundle args) {
        return getDashboardNavigator().pushFragment(fragmentClass, args);
    }

    private DashboardNavigator getDashboardNavigator() {
        @Nullable DashboardNavigatorActivity activity = ((DashboardNavigatorActivity) getActivity());
        if (activity != null) {
            return activity.getDashboardNavigator();
        }
        return null;
    }

    private void popCurrentFragment() {
        DashboardNavigator navigator = getDashboardNavigator();
        if (navigator != null) {
            navigator.popFragment();
        }
    }

    private boolean creatorIsMe(){
        if(currentUserId.toUserBaseKey().getUserId() == userCompetitionDTO.hostUserId){
            return true;
        }
        return false;
    }

    private void showDlgEditCompetitionIntro(){
        if(editCompetitionDlg==null){
            editCompetitionDlg = new Dialog(getActivity());
            editCompetitionDlg.requestWindowFeature(Window.FEATURE_NO_TITLE);
            editCompetitionDlg.setCanceledOnTouchOutside(false);
            editCompetitionDlg.setCancelable(false);
            editCompetitionDlg.setContentView(R.layout.edit_dialog_layout);
            etCompetitionIntro = (EditText)editCompetitionDlg.findViewById(R.id.edittext_intro_edit);
            tvCancel = (TextView)editCompetitionDlg.findViewById(R.id.textview_intro_edit_cancel);
            tvCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                   editCompetitionDlg.dismiss();
                }
            });
            tvConfirm = (TextView)editCompetitionDlg.findViewById(R.id.textview_intro_edit_ok);
            tvConfirm.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    editCompetitionDlg.dismiss();
                }
            });
        }
        if(userCompetitionDTO==null){
            etCompetitionIntro.setText("");
        }else{
            etCompetitionIntro.setText(userCompetitionDTO.description);
        }
        if(!editCompetitionDlg.isShowing()) {
            editCompetitionDlg.show();
        }
    }
}
