package com.tradehero.th.fragments.chinabuild.fragment.competition;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.tradehero.common.persistence.DTOCacheNew;
import com.tradehero.common.utils.THToast;
import com.tradehero.th.adapters.LeaderboardListAdapter;
import com.tradehero.th.api.competition.CompetitionDTOUtil;
import com.tradehero.th.api.competition.ProviderId;
import com.tradehero.th.api.competition.key.CompetitionId;
import com.tradehero.th.api.leaderboard.LeaderboardUserDTO;
import com.tradehero.th.api.leaderboard.LeaderboardUserDTOList;
import com.tradehero.th.api.leaderboard.competition.CompetitionLeaderboardDTO;
import com.tradehero.th.api.leaderboard.competition.CompetitionLeaderboardId;
import com.tradehero.th.api.leaderboard.key.LeaderboardKey;
import com.tradehero.th.api.leaderboard.key.PagedLeaderboardKey;
import com.tradehero.th.fragments.base.DashboardFragment;
import com.tradehero.th.fragments.chinabuild.data.UserCompetitionDTO;
import com.tradehero.th.fragments.chinabuild.fragment.portfolio.PortfolioFragment;
import com.tradehero.th.fragments.chinabuild.listview.SecurityListView;
import com.tradehero.th.misc.exception.THException;
import com.tradehero.th.models.leaderboard.LeaderboardDefDTOKnowledge;
import com.tradehero.th.models.leaderboard.key.LeaderboardDefKeyKnowledge;
import com.tradehero.th.persistence.competition.CompetitionCache;
import com.tradehero.th.persistence.leaderboard.CompetitionLeaderboardCache;
import com.tradehero.th.utils.ProgressDialogUtil;
import com.tradehero.th2.R;
import dagger.Lazy;
import javax.inject.Inject;
import javax.sound.sampled.Port;
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

    @Inject CompetitionDTOUtil competitionDTOUtil;
    @Inject CompetitionLeaderboardCache competitionLeaderboardCache;
    protected DTOCacheNew.Listener<CompetitionLeaderboardId, CompetitionLeaderboardDTO> competitionLeaderboardCacheListener;

    @Inject Lazy<CompetitionCache> competitionCacheLazy;
    private Callback<UserCompetitionDTO> callbackEnrollUGC;

    public UserCompetitionDTO userCompetitionDTO;
    private ProgressDialog mTransactionDialog;
    @Inject ProgressDialogUtil progressDialogUtil;

    protected CompetitionLeaderboardDTO competitionLeaderboardDTO;

    @InjectView(R.id.listRanks) SecurityListView listRanks;//比赛排名
    private LeaderboardListAdapter adapter;
    private int currentPage = 0;
    private int PER_PAGE = 20;

    @InjectView(R.id.tvCompetitionDetailMore) TextView tvCompetitionDetailMore;//比赛详情
    @InjectView(R.id.tvCompetitionCreator) TextView tvCompetitionCreator;//创建人
    @InjectView(R.id.tvCompetitionExchange) TextView tvCompetitionExchange;//比赛交易所
    @InjectView(R.id.tvCompetitionPeriod) TextView tvCompetitionPeriod;//比赛周期
    @InjectView(R.id.tvCompetitionIntro) TextView tvCompetitionIntro;//比赛介绍
    @InjectView(R.id.tvGotoCompetition) TextView tvGotoCompetition;//去比赛

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        getBundleCompetitionDTO();
        callbackEnrollUGC = new EnrollUGCCallback();
        competitionLeaderboardCacheListener = createCompetitionLeaderboardListener();
    }

    public void getBundleCompetitionDTO()
    {
        Bundle bundle = getArguments();
        if (bundle != null)
        {
            this.userCompetitionDTO = (UserCompetitionDTO) bundle.getSerializable(BUNDLE_COMPETITION_DTO);
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        super.onCreateOptionsMenu(menu, inflater);
        setHeadViewMiddleMain("比赛详情");
        setHeadViewRight0("邀请好友");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.competition_detail_layout, container, false);
        ButterKnife.inject(this, view);
        initView();
        return view;
    }

    private void initView()
    {
        initCompetition();
        initRankList();
    }

    private void initRankList()
    {
        listRanks.setMode(PullToRefreshBase.Mode.BOTH);
        listRanks.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>()
        {
            @Override public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView)
            {
                fetchCompetitionLeaderboard();
            }

            @Override public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView)
            {
                //fetchCompetitionLeaderboardMore();
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
        bundle.putLong(PortfolioFragment.BUNDLE_LEADERBOARD_USER_MARK_ID,userDTO.lbmuId);
        pushFragment(PortfolioFragment.class, bundle);

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
        }

        fetchCompetitionLeaderboard();
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
        competitionLeaderboardCacheListener = null;
        super.onDestroy();
    }

    @Override public void onResume()
    {
        super.onResume();
    }

    @OnClick(R.id.tvGotoCompetition)
    public void onGotoCompetitionClicked()
    {
        if (!userCompetitionDTO.isEnrolled)
        {
            //去报名
            toJoinCompetition();//去报名
        }
        else
        {
            toPlayCompetition();//去比赛
        }
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
        currentPage = 0;
        detachCompetitionLeaderboardCache();
        CompetitionLeaderboardId key = competitionDTOUtil.getCompetitionLeaderboardId(new ProviderId(CompetitionUtils.UGC_PROVIDER_ID),
                new CompetitionId(userCompetitionDTO.id), currentPage + 1, PER_PAGE);
        competitionLeaderboardCache.register(key, competitionLeaderboardCacheListener);
        competitionLeaderboardCache.getOrFetchAsync(key);
    }

    private void setListData(CompetitionLeaderboardId key, LeaderboardUserDTOList listData)
    {
        if (key.page == PagedLeaderboardKey.FIRST_PAGE)
        {
            adapter = new LeaderboardListAdapter(getActivity(), listData, LeaderboardDefKeyKnowledge.COMPETITION);
            listRanks.setAdapter(adapter);
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
}
