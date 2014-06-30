package com.tradehero.th.fragments.social.follower;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTabHost;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TabHost;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.tradehero.thm.R;
import com.tradehero.th.activities.DashboardActivity;
import com.tradehero.th.api.discussion.DiscussionType;
import com.tradehero.th.api.discussion.MessageType;
import com.tradehero.th.api.social.FollowerSummaryDTO;
import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.fragments.base.DashboardFragment;
import com.tradehero.th.models.social.follower.AllHeroTypeResourceDTO;
import com.tradehero.th.models.social.follower.FreeHeroTypeResourceDTO;
import com.tradehero.th.models.social.follower.HeroTypeResourceDTO;
import com.tradehero.th.models.social.follower.HeroTypeResourceDTOFactory;
import com.tradehero.th.models.social.follower.PremiumHeroTypeResourceDTO;
import com.tradehero.th.persistence.social.FollowerSummaryCache;
import com.tradehero.th.persistence.social.HeroType;
import com.tradehero.th.persistence.user.UserProfileCache;
import dagger.Lazy;
import java.text.MessageFormat;
import java.util.ArrayList;
import javax.inject.Inject;
import timber.log.Timber;

public class FollowerManagerFragment extends DashboardFragment /*BasePurchaseManagerFragment*/
        implements View.OnClickListener, OnFollowersLoadedListener
{
    static final int FRAGMENT_LAYOUT_ID = 10000;
    private static final String BUNDLE_KEY_HERO_ID =
            FollowerManagerFragment.class.getName() + ".heroId";

    /** parent layout of broadcastView and whisperView */
    @InjectView(R.id.send_message_layout) View messageLayout;
    @InjectView(R.id.send_message_broadcast) View broadcastView;
    @InjectView(R.id.send_message_whisper) View whisperView;

    @Inject FollowerSummaryCache followerSummaryCache;
    @Inject CurrentUserId currentUserId;
    @Inject HeroTypeResourceDTOFactory heroTypeResourceDTOFactory;
    @Inject Lazy<UserProfileCache> userProfileCache;

    private UserBaseKey heroId;

    @InjectView(android.R.id.tabhost) FragmentTabHost mTabHost;

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
        ActionBar actionBar = getSherlockActivity().getSupportActionBar();
        actionBar.setDisplayOptions(ActionBar.DISPLAY_HOME_AS_UP
                | ActionBar.DISPLAY_SHOW_TITLE
                | ActionBar.DISPLAY_SHOW_HOME);

        actionBar.setTitle(getString(R.string.social_followers));

        Fragment f = getCurrentFragment();
        if (f != null)
        {
            ((SherlockFragment)getCurrentFragment()).onCreateOptionsMenu(menu, inflater);
        }

        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override public boolean onOptionsItemSelected(MenuItem item)
    {
        Fragment f = getCurrentFragment();
        if (f != null)
        {
            boolean handled = ((SherlockFragment)getCurrentFragment()).onOptionsItemSelected(item);
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
            ((SherlockFragment) getCurrentFragment()).onPrepareOptionsMenu(menu);
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
        View view = inflater.inflate(R.layout.fragment_store_manage_followers_2, container, false);
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

    @Override public void onActivityCreated(Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);
        //setMessageLayoutShown(false);
    }

    @Override public void onPause()
    {
        super.onPause();
    }

    @Override public void onDestroyView()
    {
        broadcastView.setOnClickListener(null);
        whisperView.setOnClickListener(null);
        mTabHost = null;
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
            messageLayout.setVisibility(View.VISIBLE);
        }
        else
        {
            messageLayout.setVisibility(View.GONE);
        }
    }

    private void setMessageLayoutShown(boolean shown)
    {
        if (shown)
        {
            broadcastView.setOnClickListener(this);
            whisperView.setOnClickListener(this);
        }
        else
        {
            broadcastView.setOnClickListener(null);
            whisperView.setOnClickListener(null);
        }
        whisperView.setVisibility(View.GONE);
        messageLayout.setVisibility(shown ? View.VISIBLE : View.GONE);
    }

    private Fragment getCurrentFragment()
    {
        if(mTabHost == null)
        {
            return null;
        }
        String tag = mTabHost.getCurrentTabTag();
        android.support.v4.app.FragmentManager fm = ((Fragment) this).getChildFragmentManager();
        return fm.findFragmentByTag(tag);
    }

    private int[] getFollowerCount()
    {
        int[] result = new int[3];
        FollowerSummaryDTO followerSummaryDTO =
                followerSummaryCache.get(currentUserId.toUserBaseKey());
        if (followerSummaryDTO != null)
        {
            result[0] = followerSummaryDTO.getPaidFollowerCount();
            result[1] = followerSummaryDTO.getFreeFollowerCount();
            result[2] = result[0] + result[1];
        }
        return result;
    }

    private View addTabs()
    {
        //TODO NestedFragments needs ChildFragmentManager
        //http://developer.android.com/about/versions/android-4.2.html#NestedFragments
        mTabHost.setup(getActivity(), ((Fragment) this).getChildFragmentManager(), FRAGMENT_LAYOUT_ID);
        for (HeroTypeResourceDTO resourceDTO : getTabResourceDTOs())
        {
            addTab(resourceDTO);
        }
        setTitleColor();
        mTabHost.getTabWidget().setBackgroundColor(Color.WHITE);
        return mTabHost;
    }

    protected ArrayList<HeroTypeResourceDTO> getTabResourceDTOs()
    {
        return heroTypeResourceDTOFactory.getListOfHeroType();
    }

    private void addTab(HeroTypeResourceDTO resourceDTO)
    {
        Bundle args = new Bundle();
        FollowerManagerTabFragment.putHeroId(args, heroId);

        String title = MessageFormat.format(getString(resourceDTO.followerTabTitleRes), 0);

        TabHost.TabSpec tabSpec = mTabHost.newTabSpec(title).setIndicator(title);
        mTabHost.addTab(tabSpec, resourceDTO.followerContentFragmentClass, args);
    }

    private void setTitleColor()
    {
        int color = getResources().getColor(android.R.color.holo_blue_light);
        for (int i = 0; i < mTabHost.getTabWidget().getChildCount(); i++)
        {

            final TextView tv = (TextView) mTabHost.getTabWidget().getChildAt(i)
                    .findViewById(android.R.id.title);

            // Look for the title view to ensure this is an indicator and not a divider.(I didn't know, it would return divider too, so I was getting an NPE)
            if (tv == null)
            {
                continue;
            }
            else
            {
                tv.setTextColor(color);
            }
        }
    }

    private void changeTabTitle(HeroTypeResourceDTO resourceDTO, int count)
    {
        TextView titleView = (TextView) mTabHost.getTabWidget()
                .getChildTabViewAt(resourceDTO.followerTabIndex)
                .findViewById(android.R.id.title);
        String title = MessageFormat.format(getString(resourceDTO.followerTabTitleRes), count);
        titleView.setText(title);
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
        UserProfileDTO userProfileDTO = userProfileCache.get().get(currentUserId.toUserBaseKey());
        if (userProfileDTO != null)
        {
            userProfileDTO.paidFollowerCount = value.getPaidFollowerCount();
            userProfileDTO.freeFollowerCount = value.getFreeFollowerCount();
            userProfileDTO.allFollowerCount = userProfileDTO.paidFollowerCount + userProfileDTO.freeFollowerCount;
            userProfileCache.get().put(userBaseKey, userProfileDTO);
        }
    }

    @Override public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.send_message_whisper:
                throw new IllegalArgumentException("There is no whisper yet");

            case R.id.send_message_broadcast:
                goToMessagePage(DiscussionType.BROADCAST_MESSAGE);
                break;
            default:
                break;
        }
    }

    private void goToMessagePage(DiscussionType discussionType)
    {
        int page = mTabHost.getCurrentTab();
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
        ((DashboardActivity) getActivity()).getDashboardNavigator().pushFragment(
                SendMessageFragment.class, args);
    }
}
