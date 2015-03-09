package com.tradehero.chinabuild.fragment.competition;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.squareup.picasso.Picasso;
import com.tradehero.chinabuild.cache.CompetitionListType;
import com.tradehero.chinabuild.cache.CompetitionListTypeVip;
import com.tradehero.chinabuild.cache.CompetitionNewCache;
import com.tradehero.chinabuild.data.UserCompetitionDTO;
import com.tradehero.chinabuild.data.UserCompetitionDTOList;
import com.tradehero.common.persistence.DTOCacheNew;
import com.tradehero.common.utils.THToast;
import com.tradehero.th.R;
import com.tradehero.th.fragments.base.DashboardFragment;
import com.tradehero.th.utils.metrics.Analytics;
import com.tradehero.th.utils.metrics.AnalyticsConstants;
import com.tradehero.th.utils.metrics.events.MethodEvent;
import com.viewpagerindicator.CirclePageIndicator;
import dagger.Lazy;
import org.jetbrains.annotations.NotNull;

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

    @Inject Lazy<Picasso> picasso;
    @Inject Analytics analytics;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        competitionListCacheListenerVip = new CompetitionListCacheListener();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.competition_mine_layout, container, false);
        ButterKnife.inject(this, view);

        fetchVipCompetition(false);//获取官方推荐比赛
        return view;
    }

    @Override public void onDestroyView()
    {
        ButterKnife.reset(this);
        detachVipCompetition();
        super.onDestroyView();
    }

    private void initCompetitionAdv(UserCompetitionDTOList userCompetitionDTOs)
    {
        this.userCompetitionVipDTOs = userCompetitionDTOs;
        int sizeVip = userCompetitionDTOs.size();
        LayoutInflater layoutInflater = getActivity().getLayoutInflater();
        views = new ArrayList<View>();
        if (sizeVip > 0) llCompetitionAdv.setVisibility(View.VISIBLE);
        for (int i = 0; i < sizeVip; i++)
        {
            View view = layoutInflater.inflate(R.layout.competition_adv_item, null);
            ImageView imgCompetitionAdv = (ImageView) view.findViewById(R.id.imgCompetitionAdv);
            picasso.get()
                    .load(userCompetitionVipDTOs.get(i).bannerUrl)
                    .placeholder(R.drawable.default_image)
                    .error(R.drawable.default_image)
                    .into(imgCompetitionAdv);
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

        startScrol();
    }

    private void gotoCompetitionDetailFragment(UserCompetitionDTO userCompetitionDTO){
        Bundle bundle = new Bundle();
        bundle.putSerializable(CompetitionDetailFragment.BUNDLE_COMPETITION_DTO, userCompetitionDTO);
        gotoDashboard(CompetitionMainFragment.class.getName(), bundle);
    }


    private void startScrol(){
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

        @Override public void onErrorThrown(@NotNull CompetitionListType key, @NotNull Throwable error)
        {
            THToast.show(getString(R.string.error_network_connection));
        }

    }

    private PagerAdapter pageAdapter = new PagerAdapter()
    {
        @Override
        public void destroyItem(View container, int position, Object object)
        {
            ((ViewPager) container).removeView(views.get(position));
        }

        @Override
        public Object instantiateItem(View container, int position)
        {
            ((ViewPager) container).addView(views.get(position));
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
}
