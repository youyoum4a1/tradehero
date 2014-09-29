package com.tradehero.th.fragments.chinabuild;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import com.tradehero.common.persistence.DTOCacheNew;
import com.tradehero.th.R;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.fragments.chinabuild.fragment.AbsBaseFragment;
import com.tradehero.th.fragments.chinabuild.fragment.discovery.DiscoveryHotTopicFragment;
import com.tradehero.th.fragments.chinabuild.fragment.discovery.DiscoveryRecentNewsFragment;
import com.tradehero.th.fragments.chinabuild.fragment.discovery.DiscoveryStockGodNewsFragment;
import com.tradehero.th.fragments.chinabuild.fragment.message.DiscoveryDiscussSendFragment;
import com.tradehero.th.fragments.chinabuild.fragment.message.NotificationFragment;
import com.tradehero.th.persistence.user.UserProfileCache;
import com.viewpagerindicator.TabPageIndicator;
import dagger.Lazy;
import javax.inject.Inject;
import org.jetbrains.annotations.NotNull;
import timber.log.Timber;

public class MainTabFragmentDiscovery extends AbsBaseFragment
{
    @InjectView(R.id.pager) ViewPager pager;
    @InjectView(R.id.indicator) TabPageIndicator indicator;
    @InjectView(R.id.btnNotification) Button btnNotification;

    private FragmentPagerAdapter adapter;

    @InjectView(R.id.tvCreateTimeLine) TextView tvCreateTimeLine;
    @InjectView(R.id.tvNotificationCount) TextView tvNotificationCount;

    @Inject Lazy<UserProfileCache> userProfileCache;
    private DTOCacheNew.Listener<UserBaseKey, UserProfileDTO> userProfileCacheListener;


    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        userProfileCacheListener = createUserProfileFetchListener();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.main_tab_fragment_discovery_layout, container, false);
        ButterKnife.inject(this, view);
        initView();
        tvNotificationCount.setVisibility(View.GONE);
        return view;
    }

    private void initView()
    {
        adapter = new CustomAdapter(getChildFragmentManager());
        pager.setAdapter(adapter);
        pager.setOffscreenPageLimit(5);
        indicator.setViewPager(pager);
    }

    @OnClick(R.id.btnNotification)
    public void onButtonNoticifation()
    {
        enterNotificationFragment();
    }

    private void enterNotificationFragment()
    {
        gotoDashboard(NotificationFragment.class.getName());
    }

    @Override public void onStop()
    {
        super.onStop();
    }

    @Override public void onDestroyView()
    {
        detachUserProfileCache();
        ButterKnife.reset(this);
        super.onDestroyView();
    }

    @Override public void onDestroy()
    {
        super.onDestroy();
    }

    @Override public void onResume()
    {
        fetchUserProfile(false);
        super.onResume();
    }

    private static final String[] CONTENT = new String[] {"最新动态", "热门话题", "股神动态"};

    class CustomAdapter extends FragmentPagerAdapter
    {
        public CustomAdapter(FragmentManager fm)
        {
            super(fm);
        }

        @Override
        public Fragment getItem(int position)
        {
            switch (position)
            {
                case 0:
                    return new DiscoveryRecentNewsFragment();

                case 1:
                    return new DiscoveryHotTopicFragment();

                case 2:
                    return new DiscoveryStockGodNewsFragment();
            }
            return null;
        }

        @Override
        public CharSequence getPageTitle(int position)
        {
            return CONTENT[position % CONTENT.length].toUpperCase();
        }

        @Override
        public int getCount()
        {
            return CONTENT.length;
        }
    }

    @OnClick(R.id.tvCreateTimeLine)
    public void createTimeLine()
    {
        Timber.d("tvCreateTimeLine!!");
        gotoDashboard(DiscoveryDiscussSendFragment.class.getName());
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
            showNotification(value);
        }

        @Override public void onErrorThrown(@NotNull UserBaseKey key, @NotNull Throwable error)
        {

        }
    }

    public void showNotification(UserProfileDTO value)
    {
        if (value.unreadNotificationsCount > 0)
        {
            tvNotificationCount.setVisibility(View.VISIBLE);
            tvNotificationCount.setText(value.getUnReadNotificationCount());
        }
        else
        {
            tvNotificationCount.setVisibility(View.GONE);
        }
    }
    private void detachUserProfileCache()
    {
        userProfileCache.get().unregister(userProfileCacheListener);
    }

    protected void fetchUserProfile(boolean force)
    {
        detachUserProfileCache();
        userProfileCache.get().register(currentUserId.toUserBaseKey(), userProfileCacheListener);
        userProfileCache.get().getOrFetchAsync(currentUserId.toUserBaseKey(),force);
    }

}
