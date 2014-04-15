package com.tradehero.th.fragments.social.follower;

import android.os.Bundle;
import android.support.v4.app.FragmentTabHost;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TabHost;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.tradehero.th.R;
import com.tradehero.th.activities.DashboardActivity;
import com.tradehero.th.api.discussion.DiscussionType;
import com.tradehero.th.api.discussion.MessageType;
import com.tradehero.th.api.social.FollowerSummaryDTO;
import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.fragments.base.DashboardFragment;
import com.tradehero.th.models.social.follower.HeroTypeResourceDTO;
import com.tradehero.th.models.social.follower.HeroTypeResourceDTOFactory;
import com.tradehero.th.persistence.social.FollowerSummaryCache;
import com.tradehero.th.persistence.social.HeroType;
import java.text.MessageFormat;
import java.util.Map;
import javax.inject.Inject;
import timber.log.Timber;

/**
 * Created with IntelliJ IDEA. User: xavier Date: 11/11/13 Time: 11:04 AM To change this template
 * use File | Settings | File Templates.
 */
public class FollowerManagerFragment extends DashboardFragment /*BasePurchaseManagerFragment*/
        implements View.OnClickListener, OnFollowersLoadedListener
{
    public static final String KEY_PAGE = FollowerManagerFragment.class.getName() + ".keyPage";
    public static final String KEY_ID = FollowerManagerFragment.class.getName() + ".keyId";

    static final int FRAGMENT_LAYOUT_ID = 10000;
    public static final String BUNDLE_KEY_HERO_ID =
            FollowerManagerFragment.class.getName() + ".heroId";

    /** parent layout of broadcastView and whisperView */
    @InjectView(R.id.send_message_layout) View messageLayout;
    /** view to 'send broadcast' */
    @InjectView(R.id.send_message_broadcast) View broadcastView;
    /** view to 'send whisper' */
    @InjectView(R.id.send_message_whisper) View whisperView;

    @Inject FollowerSummaryCache followerSummaryCache;
    @Inject CurrentUserId currentUserId;
    @Inject HeroTypeResourceDTOFactory heroTypeResourceDTOFactory;

    private UserBaseKey heroId;
    /** categories of follower:premium,free,all */
    private Map<Integer /* tab index */, HeroTypeResourceDTO> followerTypes;

    @InjectView(android.R.id.tabhost) FragmentTabHost mTabHost;

    @Override public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        this.heroId = new UserBaseKey(getArguments().getInt(BUNDLE_KEY_HERO_ID));
        this.followerTypes = heroTypeResourceDTOFactory.getMapByHeroTypeId();
    }

    @Override public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        super.onCreateOptionsMenu(menu, inflater);

        ActionBar actionBar = getSherlockActivity().getSupportActionBar();
        actionBar.setDisplayOptions(ActionBar.DISPLAY_HOME_AS_UP
                | ActionBar.DISPLAY_SHOW_TITLE
                | ActionBar.DISPLAY_SHOW_HOME);

        actionBar.setTitle("Followers");
    }

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState)
    {
        Timber.d("onCreateView");
        View view = inflater.inflate(R.layout.fragment_store_manage_followers_2, container, false);
        ButterKnife.inject(this, view);
        addTabs();
        return view;
    }

    @Override public void onViewCreated(View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        setMessageLayoutShown(true);
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
        super.onDestroyView();
    }

    @Override public void onDestroy()
    {
        super.onDestroy();
        followerTypes = null;
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
        mTabHost.setup(getActivity(), getChildFragmentManager(), FRAGMENT_LAYOUT_ID);
        Bundle args = getArguments();
        if (args == null)
        {
            args = new Bundle();
        }
        for (Map.Entry<Integer, HeroTypeResourceDTO> entry : followerTypes.entrySet())
        {
            args = new Bundle(args);
            args.putInt(KEY_PAGE, entry.getValue().pageIndex);
            args.putInt(KEY_ID, entry.getValue().getFollowerType().typeId);

            String title =
                    MessageFormat.format(getSherlockActivity().getString(entry.getValue().titleRes),
                            0);

            TabHost.TabSpec tabSpec = mTabHost.newTabSpec(title).setIndicator(title);
            mTabHost.addTab(tabSpec, entry.getValue().fragmentClass, args);
        }

        return mTabHost;
    }

    private void changeTabTitle(int page, int number)
    {
        int titleRes = 0;
        for (Map.Entry<Integer, HeroTypeResourceDTO> entry : followerTypes.entrySet())
        {
            if (entry.getValue().pageIndex == page)
            {
                titleRes = entry.getValue().titleRes;
            }
        }
        TextView tv = (TextView) mTabHost.getTabWidget()
                .getChildTabViewAt(page)
                .findViewById(android.R.id.title);
        String title = MessageFormat.format(getSherlockActivity().getString(titleRes), number);
        tv.setText(title);
    }

    @Override public void onFollowerLoaded(int page, FollowerSummaryDTO value)
    {
        if (!isDetached())
        {
            //remove the function to send message
            //setMessageLayoutShown(false);
            int paid = value.getPaidFollowerCount();
            int free = value.getFreeFollowerCount();
            changeTabTitle(0, paid);
            changeTabTitle(1, free);
            changeTabTitle(2, (paid + free));
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
        MessageType messageType = null;
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
                throw new IllegalStateException("unknown followerType! ");
        }

        args.putInt(SendMessageFragment.KEY_MESSAGE_TYPE, messageType.typeId);
        Timber.d("goToMessagePage index:%d,tabIndex:%d,followerType:%s,discussionType:%s", page,
                page, followerType, discussionType);
        ((DashboardActivity) getActivity()).getDashboardNavigator().pushFragment(
                SendMessageFragment.class, args);
    }

    @Override public boolean isTabBarVisible()
    {
        return false;
    }

    ///////////////////////////////////////////////////////////////////////////
}
