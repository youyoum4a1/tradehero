package com.tradehero.chinabuild.fragment.discovery;

import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import com.squareup.picasso.Picasso;
import com.tradehero.th.R;
import com.tradehero.th.activities.MainActivity;
import com.tradehero.th.fragments.base.DashboardFragment;
import com.viewpagerindicator.CirclePageIndicator;
import dagger.Lazy;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Discovery Square fragment
 *
 * Created by palmer on 15/1/26.
 */
public class DiscoverySquareFragment extends DashboardFragment implements View.OnClickListener
{

    //Square Reward Views
    private LinearLayout rewardLL;

    //Square Recent Views
    private LinearLayout recentLL;

    //Square Favorite Views
    private LinearLayout favoriteLL;

    //Square Novice Views
    private LinearLayout noviceLL;

    @Inject Lazy<Picasso> picasso;
    @InjectView(R.id.rlTopBanner) RelativeLayout rlTopBanner;
    @InjectView(R.id.pager) ViewPager pager;
    @InjectView(R.id.indicator) CirclePageIndicator indicator;
    @InjectView(R.id.btnBannerClose) Button btnBannerClose;
    private List<View> views = new ArrayList();
    private Timer timer;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.discovery_square, container, false);
        ButterKnife.inject(this, view);
        recentLL = (LinearLayout) view.findViewById(R.id.linearlayout_square_recent);
        rewardLL = (LinearLayout) view.findViewById(R.id.linearlayout_square_reward);
        favoriteLL = (LinearLayout) view.findViewById(R.id.linearlayout_square_favorite);
        noviceLL = (LinearLayout) view.findViewById(R.id.linearlayout_square_learning);
        recentLL.setOnClickListener(this);
        rewardLL.setOnClickListener(this);
        favoriteLL.setOnClickListener(this);
        noviceLL.setOnClickListener(this);
        initTopBanner();
        return view;
    }

    @Override
    public void onClick(View view)
    {
        int viewId = view.getId();
        switch (viewId)
        {
            case R.id.linearlayout_square_recent:
                gotoDashboard(DiscoveryRecentNewsFragment.class, new Bundle());
                break;
            case R.id.linearlayout_square_reward:
                gotoDashboard(DiscoveryRewardFragment.class, new Bundle());
                break;
            case R.id.linearlayout_square_favorite:
                break;
            case R.id.linearlayout_square_learning:
                break;
        }
    }

    @OnClick(R.id.btnBannerClose)
    public void onClickBanner()
    {
        dismissTopBanner();
        MainActivity.SHOW_ADVERTISEMENT = false;
    }

    public void dismissTopBanner()
    {
        Animation animation = AnimationUtils.loadAnimation(getActivity(),R.anim.alpha_out);
        animation.setAnimationListener(new Animation.AnimationListener()
        {
            @Override public void onAnimationStart(Animation animation){}
            @Override public void onAnimationEnd(Animation animation)
            {
                rlTopBanner.setVisibility(View.GONE);
            }
            @Override public void onAnimationRepeat(Animation animation){}
        });
        rlTopBanner.startAnimation(animation);
    }

    private void initTopBanner()
    {
        if(!MainActivity.SHOW_ADVERTISEMENT){
            return;
        }
        LayoutInflater layoutInflater = getActivity().getLayoutInflater();
        views = new ArrayList();
        rlTopBanner.setVisibility(View.VISIBLE);

        for (int i = 0; i < 3; i++)
        {
            View view = layoutInflater.inflate(R.layout.search_square_top_banner_item, null);
            ImageView imageView = (ImageView) view.findViewById(R.id.imgTopBannerItem);
            picasso.get()
                    .load("http://s10.sinaimg.cn/orignal/4a9f5ee165a82e42a0459.jpg")
                    .placeholder(R.drawable.default_image)
                    .error(R.drawable.default_image)
                    .into(imageView);
            views.add(view);
            view.setOnClickListener(new View.OnClickListener()
            {
                @Override public void onClick(View view)
                {
                }
            });
        }

        pager.setAdapter(pageAdapter);
        indicator.setViewPager(pager);
        startTimer();

        pager.setOnTouchListener(new View.OnTouchListener()
        {
            public boolean onTouch(View v, MotionEvent event)
            {
                if (event.getAction() == MotionEvent.ACTION_UP)
                {
                    startTimer();
                }
                else
                {
                    stopTimer();
                }
                return false;
            }
        });
    }

    PagerAdapter pageAdapter = new PagerAdapter()
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



    public void stopTimer() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }

    public void startTimer() {
        stopTimer();
        timer = new Timer();
        timer.schedule(new TimerTask() {
            public void run() {
                if(getActivity()==null){
                    stopTimer();
                    return;
                }
                getActivity().runOnUiThread(new Runnable()
                {
                    public void run()
                    {
                        pager.setCurrentItem((pager.getCurrentItem() + 1) % pageAdapter.getCount());
                    }
                });
            }
        }, 1000, 3000);
    }


}
