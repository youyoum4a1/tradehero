package com.tradehero.th.fragments.social.follower;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTabHost;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TabHost;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.android.common.SlidingTabLayout;
import com.tradehero.th.BottomTabs;
import com.tradehero.th.R;
import com.tradehero.th.api.discussion.DiscussionType;
import com.tradehero.th.api.discussion.MessageType;
import com.tradehero.th.api.social.FollowerSummaryDTO;
import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.fragments.DashboardTabHost;
import com.tradehero.th.fragments.base.DashboardFragment;
import com.tradehero.th.models.social.follower.AllHeroTypeResourceDTO;
import com.tradehero.th.models.social.follower.FreeHeroTypeResourceDTO;
import com.tradehero.th.models.social.follower.HeroTypeResourceDTO;
import com.tradehero.th.models.social.follower.HeroTypeResourceDTOFactory;
import com.tradehero.th.models.social.follower.PremiumHeroTypeResourceDTO;
import com.tradehero.th.persistence.social.HeroType;
import com.tradehero.th.persistence.user.UserProfileCacheRx;
import com.tradehero.th.utils.GraphicUtil;
import com.tradehero.th.widget.THTabView;
import dagger.Lazy;
import java.text.MessageFormat;
import java.util.ArrayList;
import javax.inject.Inject;
import timber.log.Timber;

public class FollowerManagerFragment extends DashboardFragment /*BasePurchaseManagerFragment*/
        implements View.OnClickListener, OnFollowersLoadedListener
{
    private static final String BUNDLE_KEY_HERO_ID =
            FollowerManagerFragment.class.getName() + ".heroId";

    @InjectView(R.id.send_message_broadcast) View broadcastView;
    @InjectView(R.id.viewpager) ViewPager mViewPager;
    @InjectView(R.id.tabs) SlidingTabLayout mSlidingLayout;

    private FollowerManagerFragmentViewPagerAdapter mViewPagerAdapter;

    @Inject CurrentUserId currentUserId;
    @Inject HeroTypeResourceDTOFactory heroTypeResourceDTOFactory;
    @Inject Lazy<UserProfileCacheRx> userProfileCache;
    @Inject GraphicUtil graphicUtil;

    private UserBaseKey heroId;
    @Inject @BottomTabs Lazy<DashboardTabHost> dashboardTabHost;
    private ArrayList<HeroTypeResourceDTO> cachedResourceDTOs;

    public static void putHeroId(Bundle args, UserBaseKey heroId)
    {
        args.putBundle(BUNDLE_KEY_HERO_ID, heroId.getArgs());
    }

    public static UserBaseKey getHeroId(Bundle args)
    {
        return new UserBaseKey(args.getBundle(BUNDLE_KEY_HERO_ID));
    }

    @Override public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        this.heroId = getHeroId(getArguments());
    }

    @Override public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        super.onCreateOptionsMenu(menu, inflater);

        setActionBarTitle(getTitle());
    }

    private int getTitle()
    {
        if (isCurrentUser())
        {
            return R.string.manage_my_followers_title;
        }
        else
        {
            return R.string.manage_followers_title;
        }
    }

    @Override public boolean onOptionsItemSelected(MenuItem item)
    {
        Fragment f = getCurrentFragment();
        if (f != null)
        {
            boolean handled = getCurrentFragment().onOptionsItemSelected(item);
            if (handled)
            {
                return true;
            }
        }

        return super.onOptionsItemSelected(item);
    }

    @Override public void onPrepareOptionsMenu(Menu menu)
    {
        Fragment f = getCurrentFragment();
        if (f != null)
        {
            getCurrentFragment().onPrepareOptionsMenu(menu);
        }

        super.onPrepareOptionsMenu(menu);
    }

    @Override public void onOptionsMenuClosed(android.view.Menu menu)
    {
        Fragment f = getCurrentFragment();
        if (f != null)
        {
            getCurrentFragment().onOptionsMenuClosed(menu);
        }

        super.onOptionsMenuClosed(menu);
    }

    @Override public void onDestroyOptionsMenu()
    {
        Fragment f = getCurrentFragment();
        if (f != null)
        {
            f.onDestroyOptionsMenu();
        }

        super.onDestroyOptionsMenu();
    }

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_follower_manager, container, false);
        ButterKnife.inject(this, view);
        addTabs();
        return view;
    }

    @Override public void onViewCreated(View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        setMessageLayoutShown(true);
        showSendMessageLayoutIfNecessary();
    }

    @Override public void onResume()
    {
        super.onResume();
        dashboardTabHost.get().setOnTranslate((x, y) -> broadcastView.setTranslationY(y));
    }

    @Override public void onPause()
    {
        dashboardTabHost.get().setOnTranslate(null);
        super.onPause();
    }

    @Override public void onDestroyView()
    {
        broadcastView.setOnClickListener(null);
        Timber.d("onDestroyView");
        super.onDestroyView();
    }

    private boolean isCurrentUser()
    {
        UserBaseKey heroId = getHeroId(getArguments());
        if (heroId != null && heroId.key != null && currentUserId != null)
        {
            return (heroId.key.intValue() == currentUserId.toUserBaseKey().key.intValue());
        }
        return false;
    }

    private void showSendMessageLayoutIfNecessary()
    {
        if (isCurrentUser())
        {
            broadcastView.setVisibility(View.VISIBLE);
        }
        else
        {
            broadcastView.setVisibility(View.GONE);
        }
    }

    private void setMessageLayoutShown(boolean shown)
    {
        if (shown)
        {
            broadcastView.setOnClickListener(this);
        }
        else
        {
            broadcastView.setOnClickListener(null);
        }
        broadcastView.setVisibility(shown ? View.VISIBLE : View.GONE);
    }

    private Fragment getCurrentFragment()
    {
        return mViewPagerAdapter.getItem(mViewPager.getCurrentItem());
    }

    private void addTabs()
    {
        mViewPagerAdapter = new FollowerManagerFragmentViewPagerAdapter(getChildFragmentManager());
        mViewPager.setAdapter(mViewPagerAdapter);
        mSlidingLayout.setViewPager(mViewPager);
    }

    protected ArrayList<HeroTypeResourceDTO> getTabResourceDTOs()
    {
        if (cachedResourceDTOs == null)
        {
            cachedResourceDTOs = heroTypeResourceDTOFactory.getListOfHeroType();
        }
        return cachedResourceDTOs;
    }

    private void changeTabTitle(HeroTypeResourceDTO resourceDTO, int count)
    {
        //THTabView titleView = (THTabView) mTabHost.getTabWidget()
        //        .getChildTabViewAt(resourceDTO.followerTabIndex);
        //String title = MessageFormat.format(getString(resourceDTO.followerTabTitleRes), count);
        //titleView.setTitle(title);
    }

    @Override public void onFollowerLoaded(int page, FollowerSummaryDTO value)
    {
        if (!isDetached())
        {
            //remove the function to send message
            //setMessageLayoutShown(false);
            int paid = value.getPaidFollowerCount();
            int free = value.getFreeFollowerCount();

            changeTabTitle(new PremiumHeroTypeResourceDTO(), paid);
            changeTabTitle(new FreeHeroTypeResourceDTO(), free);
            changeTabTitle(new AllHeroTypeResourceDTO(), paid + free);

            updateUserProfileCache(value);
        }
    }

    private void updateUserProfileCache(FollowerSummaryDTO value)
    {
        // TODO synchronization problem
        UserBaseKey userBaseKey = currentUserId.toUserBaseKey();
        UserProfileDTO userProfileDTO = userProfileCache.get().getValue(currentUserId.toUserBaseKey());
        if (userProfileDTO != null)
        {
            userProfileDTO.paidFollowerCount = value.getPaidFollowerCount();
            userProfileDTO.freeFollowerCount = value.getFreeFollowerCount();
            userProfileDTO.allFollowerCount = userProfileDTO.paidFollowerCount + userProfileDTO.freeFollowerCount;
            userProfileCache.get().onNext(userBaseKey, userProfileDTO);
        }
    }

    @Override public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.send_message_broadcast:
                goToMessagePage(DiscussionType.BROADCAST_MESSAGE);
                break;
            default:
                break;
        }
    }

    private void goToMessagePage(DiscussionType discussionType)
    {
        int page = mViewPager.getCurrentItem();
        HeroType followerType = HeroType.fromId(page);

        Bundle args = new Bundle();

        args.putInt(SendMessageFragment.KEY_DISCUSSION_TYPE, discussionType.value);
        MessageType messageType;
        switch (followerType)
        {
            case ALL:
                messageType = MessageType.BROADCAST_ALL_FOLLOWERS;
                break;
            case PREMIUM:
                messageType = MessageType.BROADCAST_PAID_FOLLOWERS;
                break;
            case FREE:
                messageType = MessageType.BROADCAST_FREE_FOLLOWERS;
                break;
            default:
                throw new IllegalStateException("unknown followerType!");
        }

        args.putInt(SendMessageFragment.KEY_MESSAGE_TYPE, messageType.typeId);
        Timber.d("goToMessagePage index:%d, tabIndex:%d, followerType:%s, discussionType:%s", page,
                page, followerType, discussionType);
        navigator.get().pushFragment(SendMessageFragment.class, args);
    }

    protected class FollowerManagerFragmentViewPagerAdapter extends FragmentPagerAdapter
    {

        public FollowerManagerFragmentViewPagerAdapter(FragmentManager fm)
        {
            super(fm);
        }

        @Override public Fragment getItem(int i)
        {
            Bundle args = new Bundle();
            FollowerManagerTabFragment.putHeroId(args, heroId);

            return Fragment.instantiate(getActivity(), getTabResourceDTOs().get(i).followerContentFragmentClass.getName(), args);
        }

        @Override public int getCount()
        {
            return getTabResourceDTOs().size();
        }

        @Override public CharSequence getPageTitle(int position)
        {
            return MessageFormat.format(getString(getTabResourceDTOs().get(position).followerTabTitleRes), 0);
        }
    }
}
