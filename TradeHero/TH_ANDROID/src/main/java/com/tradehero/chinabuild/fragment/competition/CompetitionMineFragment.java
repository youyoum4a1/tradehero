package com.tradehero.chinabuild.fragment.competition;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.handmark.pulltorefresh.library.pulltorefresh.PullToRefreshBase;
import com.handmark.pulltorefresh.library.pulltorefresh.PullToRefreshListView;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.tradehero.chinabuild.cache.CompetitionListType;
import com.tradehero.chinabuild.cache.CompetitionListTypeVip;
import com.tradehero.chinabuild.cache.CompetitionNewCache;
import com.tradehero.chinabuild.data.CompetitionDataItem;
import com.tradehero.chinabuild.data.CompetitionInterface;
import com.tradehero.chinabuild.data.UserCompetitionDTO;
import com.tradehero.chinabuild.data.UserCompetitionDTOList;
import com.tradehero.chinabuild.utils.UniversalImageLoader;
import com.tradehero.common.persistence.DTOCacheNew;
import com.tradehero.common.utils.THToast;
import com.tradehero.th.R;
import com.tradehero.th.adapters.CompetitionListAdapter;
import com.tradehero.th.fragments.base.DashboardFragment;
import com.tradehero.th.misc.exception.THException;
import com.tradehero.th.network.service.CompetitionServiceWrapper;
import com.tradehero.metrics.Analytics;
import com.tradehero.th.utils.metrics.AnalyticsConstants;
import com.tradehero.th.utils.metrics.events.MethodEvent;
import com.tradehero.th.widget.TradeHeroProgressBar;
import com.viewpagerindicator.CirclePageIndicator;
import dagger.Lazy;
import org.jetbrains.annotations.NotNull;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

/**
 * 我参加的比赛，默认显示正在进行的比赛。
 */
public class CompetitionMineFragment extends DashboardFragment {

    @InjectView(R.id.llCompetitionAdv) RelativeLayout llCompetitionAdv;//广告栏
    @InjectView(R.id.pager) ViewPager pager;
    @InjectView(R.id.indicator) CirclePageIndicator indicator;
    private int count;
    private boolean isStartedScroll = false;
    private UserCompetitionDTOList userCompetitionVipDTOs;
    private List<View> views = new ArrayList();
    private CompetitionListCacheListener competitionListCacheListenerVip;

    @Inject Lazy<CompetitionNewCache> competitionNewCacheLazy;

    @Inject Analytics analytics;

    private CompetitionListAdapter adapterList;

    //Footer View
    private View footerView;
    private TextView tvMoreCompetition;
    private RelativeLayout noJoinAnyCompetitionLayout;

    private TradeHeroProgressBar progressBar;
    private PullToRefreshListView listCompetitions;

    @Inject Lazy<CompetitionServiceWrapper> competitionServiceWrapper;
    private final int perPage = 20;
    private int page = 0;

    private boolean noMoreHistory = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        competitionListCacheListenerVip = new CompetitionListCacheListener();
        adapterList = new CompetitionListAdapter(getActivity(), CompetitionUtils.COMPETITION_PAGE_MINE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.competition_mine_layout, container, false);
        ButterKnife.inject(this, view);
        footerView = inflater.inflate(R.layout.competition_mine_footer, null);
        initFooterView();

        initViews(view);
        if(adapterList==null || adapterList.getCount()<=0){
            showProgressBar();
            retrieveMineOpenCompetitions();
            tvMoreCompetition.setVisibility(View.GONE);
        }else{
            if(noMoreHistory){
                tvMoreCompetition.setVisibility(View.GONE);
            }else{
                tvMoreCompetition.setVisibility(View.VISIBLE);
            }
        }
        fetchVipCompetition(false);//获取官方推荐比赛
        return view;
    }

    @Override public void onDestroyView()
    {
        ButterKnife.reset(this);
        detachVipCompetition();
        super.onDestroyView();
    }

    private void retrieveMineOpenCompetitions(){
        competitionServiceWrapper.get().retrieveMyOpenCompetitions(new CompetitionsMineOpenCallback());
    }

    private void retrieveMineCloseCompetitionsMore(){
        page++;
        competitionServiceWrapper.get().retrieveMyClosedCompetitions(perPage, page, new CompetitionMineClosedCallback());
    }

    private class CompetitionsMineOpenCallback implements Callback<UserCompetitionDTOList>{
        @Override
        public void success(UserCompetitionDTOList userCompetitionDTOs, Response response) {
            if(adapterList!=null && tvMoreCompetition!=null) {
                adapterList.setMyCompetitionDtoList(userCompetitionDTOs);
                page = 0;
                noMoreHistory = false;
            }
            onFinish();
        }

        @Override
        public void failure(RetrofitError retrofitError) {
            THException exception = new THException(retrofitError);
            THToast.show(exception.getMessage());
            onFinish();
        }

        private void onFinish(){
            if(adapterList.getCount()> 0 && noJoinAnyCompetitionLayout!=null){
                if(noJoinAnyCompetitionLayout.getVisibility()==View.VISIBLE){
                    noJoinAnyCompetitionLayout.setVisibility(View.GONE);
                }
            }else{
                if(noJoinAnyCompetitionLayout.getVisibility()==View.GONE){
                    noJoinAnyCompetitionLayout.setVisibility(View.VISIBLE);
                }
            }
            if(listCompetitions!=null){
                listCompetitions.onRefreshComplete();
            }
            if(tvMoreCompetition!=null && tvMoreCompetition.getVisibility()!=View.VISIBLE) {
                tvMoreCompetition.setVisibility(View.VISIBLE);
            }

            dismissProgressBar();
        }
    }

    private class CompetitionMineClosedCallback implements Callback<UserCompetitionDTOList>{


        @Override
        public void success(UserCompetitionDTOList userCompetitionDTOs, Response response) {
            if(adapterList!=null){
                adapterList.addMyCompetitionDtoList(userCompetitionDTOs);
                if(userCompetitionDTOs.size()<perPage && tvMoreCompetition!=null){
                    tvMoreCompetition.setVisibility(View.GONE);
                    noMoreHistory = true;
                }
            }
            onFinish();
        }

        @Override
        public void failure(RetrofitError retrofitError) {
            THException exception = new THException(retrofitError);
            THToast.show(exception.getMessage());
            if(page > 0){
                page--;
            }
            onFinish();
        }

        private void onFinish(){
            if(tvMoreCompetition!=null){
                tvMoreCompetition.setClickable(true);
            }
            dismissProgressBar();
        }
    }

    private void initViews(View view){
        progressBar = (TradeHeroProgressBar)view.findViewById(R.id.tradeheroprogressbar_competition);
        listCompetitions = (PullToRefreshListView)view.findViewById(R.id.listCompetitions);
        listCompetitions.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
        listCompetitions.setAdapter(adapterList);
        listCompetitions.getRefreshableView().addFooterView(footerView);
        listCompetitions.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                retrieveMineOpenCompetitions();
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) { }
        });
        listCompetitions.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int id, long position) {
                if(position<0){
                    return;
                }
                CompetitionInterface item = adapterList.getItem((int) position);
                if (item instanceof CompetitionDataItem) {
                    gotoCompetitionDetailFragment(((CompetitionDataItem) item).userCompetitionDTO);
                }
            }
        });
    }

    private void initFooterView(){
        tvMoreCompetition = (TextView)footerView.findViewById(R.id.textview_competition_mine_more);
        tvMoreCompetition.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showProgressBar();
                tvMoreCompetition.setClickable(false);
                retrieveMineCloseCompetitionsMore();
            }
        });
        noJoinAnyCompetitionLayout = (RelativeLayout)footerView.findViewById(R.id.relativelayout_not_join_any_competition);
        if(noJoinAnyCompetitionLayout.getVisibility() == View.VISIBLE) {
            noJoinAnyCompetitionLayout.setVisibility(View.GONE);
        }
    }

    private void initCompetitionAdv(UserCompetitionDTOList userCompetitionDTOs)
    {
        this.userCompetitionVipDTOs = userCompetitionDTOs;
        int sizeVip = userCompetitionDTOs.size();
        LayoutInflater layoutInflater = getActivity().getLayoutInflater();
        views = new ArrayList();
        if (sizeVip > 0) llCompetitionAdv.setVisibility(View.VISIBLE);
        for (int i = 0; i < sizeVip; i++)
        {
            View view = layoutInflater.inflate(R.layout.competition_adv_item, null);
            ImageView imgCompetitionAdv = (ImageView) view.findViewById(R.id.imgCompetitionAdv);
            ImageLoader.getInstance().displayImage(userCompetitionVipDTOs.get(i).bannerUrl, imgCompetitionAdv, UniversalImageLoader.getAdvertisementImageLoaderOptions());
            views.add(view);
            view.setOnClickListener(new View.OnClickListener()
            {
                @Override public void onClick(View view)
                {
                    int position = pager.getCurrentItem();
                    gotoCompetitionDetailFragment(userCompetitionVipDTOs.get(position));

                    analytics.addEvent(new MethodEvent(AnalyticsConstants.BUTTON_COMPETITION_DETAIL_BANNER, "" + position));
                }
            });
        }

        pager.setAdapter(pageAdapter);
        indicator.setViewPager(pager);

        startScroll();
    }

    private void gotoCompetitionDetailFragment(UserCompetitionDTO userCompetitionDTO){
        Bundle bundle = new Bundle();
        bundle.putSerializable(CompetitionDetailFragment.BUNDLE_COMPETITION_DTO, userCompetitionDTO);
        gotoDashboard(CompetitionMainFragment.class, bundle);
    }


    private void startScroll(){
        if(isStartedScroll)return;
        final Handler handler = new Handler();
        Runnable runnable = new Runnable()
        {
            @Override
            public void run()
            {
                try {
                    pager.setCurrentItem(count++ % pageAdapter.getCount(), true);
                    handler.postDelayed(this, 3000);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        handler.postDelayed(runnable, 3000);
        isStartedScroll = true;
    }

    protected class CompetitionListCacheListener implements DTOCacheNew.Listener<CompetitionListType, UserCompetitionDTOList> {
        @Override
        public void onDTOReceived(@NotNull CompetitionListType key, @NotNull UserCompetitionDTOList value) {
            if (key instanceof CompetitionListTypeVip) {
                initCompetitionAdv(value);
            }
        }

        @Override public void onErrorThrown(@NotNull CompetitionListType key, @NotNull Throwable error) {
            THToast.show(getString(R.string.error_network_connection));
        }

    }

    private PagerAdapter pageAdapter = new PagerAdapter() {
        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView(views.get(position));
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            container.addView(views.get(position));
            return views.get(position);
        }

        @Override
        public boolean isViewFromObject(View arg0, Object arg1)
        {
            return arg0 == arg1;
        }

        @Override
        public int getCount()
        {
            return (views == null) ? 0 : views.size();
        }
    };

    public void fetchVipCompetition(boolean refresh)
    {
        detachVipCompetition();
        CompetitionListTypeVip vipKey = new CompetitionListTypeVip();
        competitionNewCacheLazy.get().register(vipKey, competitionListCacheListenerVip);
        competitionNewCacheLazy.get().getOrFetchAsync(vipKey, refresh);
    }

    protected void detachVipCompetition()
    {
        competitionNewCacheLazy.get().unregister(competitionListCacheListenerVip);
    }

    private void showProgressBar(){
        if(progressBar!=null){
            progressBar.setVisibility(View.VISIBLE);
            progressBar.startLoading();
        }
    }

    private void dismissProgressBar(){
        if(progressBar!=null){
            progressBar.stopLoading();
            progressBar.setVisibility(View.GONE);
        }
    }
}
