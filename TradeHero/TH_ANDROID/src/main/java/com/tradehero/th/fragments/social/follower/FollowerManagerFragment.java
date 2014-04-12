package com.tradehero.th.fragments.social.follower;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.tradehero.th.R;
import com.tradehero.th.activities.DashboardActivity;
import com.tradehero.th.api.discussion.DiscussionType;
import com.tradehero.th.api.discussion.MessageType;
import com.tradehero.th.api.social.FollowerSummaryDTO;
import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.fragments.base.BaseFragment;
import com.tradehero.th.fragments.updatecenter.TabListener;
import com.tradehero.th.models.social.follower.HeroTypeResourceDTO;
import com.tradehero.th.models.social.follower.HeroTypeResourceDTOFactory;
import com.tradehero.th.persistence.social.FollowerSummaryCache;
import com.tradehero.th.persistence.social.HeroKey;
import com.tradehero.th.persistence.social.HeroType;
import java.text.MessageFormat;
import java.util.Map;
import javax.inject.Inject;
import timber.log.Timber;

/**
 * Created with IntelliJ IDEA. User: xavier Date: 11/11/13 Time: 11:04 AM To change this template use File | Settings | File Templates.
 */
public class FollowerManagerFragment extends BaseFragment /*BasePurchaseManagerFragment*/
        implements View.OnClickListener
{
    public static final String KEY_PAGE = FollowerManagerFragment.class.getName() + ".keyPage";
    public static final String KEY_ID = FollowerManagerFragment.class.getName() + ".keyId";

    public static final String BUNDLE_KEY_HERO_ID = FollowerManagerFragment.class.getName() + ".heroId";

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

    private int selectedId = -1;

    OnFollowersLoadedListener onFollowersLoadedListener;

    @Override public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        this.heroId = new UserBaseKey(getArguments().getInt(BUNDLE_KEY_HERO_ID));
        this.followerTypes = heroTypeResourceDTOFactory.getMapByHeroTypeId();
        onFollowersLoadedListener = new OnFollowersLoadedListener()
        {
            @Override public void onFollowerLoaded(int page, FollowerSummaryDTO value)
            {
                if (!isDetached())
                {
                    //remove the function to send message
                    //setMessageLayoutShown(false);
                    if (getSherlockActivity().getActionBar().getTabCount() == followerTypes.size())
                    {
                        int paid = value.getPaidFollowerCount();
                        int free = value.getFreeFollowerCount();
                        changeTabTitle(paid, free, (paid + free));
                    }
                }
            }
        };
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
        saveSelectedTab();
    }

    @Override public void onDestroyView()
    {
        super.onDestroyView();
        clearTabs();
    }

    @Override public void onDestroy()
    {
        super.onDestroy();
        followerTypes = null;
        onFollowersLoadedListener = null;
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

    private void addTabs2()
    {
        //FragmentTabHost mTabHost = new FragmentTabHost(getActivity());
        //mTabHost.setup(getActivity(), getChildFragmentManager(), R.id.fragment1);
        //
        //mTabHost.addTab(mTabHost.newTabSpec("simple").setIndicator("Simple"),
        //        FragmentStackSupport.CountingFragment.class, null);
        //mTabHost.addTab(mTabHost.newTabSpec("contacts").setIndicator("Contacts"),
        //        LoaderCursorSupport.CursorLoaderListFragment.class, null);
        //mTabHost.addTab(mTabHost.newTabSpec("custom").setIndicator("Custom"),
        //        LoaderCustomSupport.AppListFragment.class, null);
        //mTabHost.addTab(mTabHost.newTabSpec("throttle").setIndicator("Throttle"),
        //        LoaderThrottleSupport.ThrottledLoaderListFragment.class, null);
    }

    private int[] getFollowerCount()
    {
        int[] result = new int[3];
        FollowerSummaryDTO followerSummaryDTO = followerSummaryCache.get(currentUserId.toUserBaseKey());
        if (followerSummaryDTO != null)
        {
            result[0] = followerSummaryDTO.getPaidFollowerCount();
            result[1] = followerSummaryDTO.getFreeFollowerCount();
            result[2] = result[0] + result[1];
        }
        return result;
    }

    private void addTabs()
    {
        //TODO NestedFragments needs ChildFragmentManager
        //http://developer.android.com/about/versions/android-4.2.html#NestedFragments
        int savedSelectedId = selectedId;
        ActionBar.Tab selectedTab = null;
        ActionBar actionBar = getSherlockActivity().getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        //actionBar.setDisplayOptions(0, ActionBar.DISPLAY_SHOW_TITLE);

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
            ActionBar.Tab tab = actionBar.newTab().setTabListener(
                    new MyTabListener(getSherlockActivity(), entry.getValue().fragmentClass, entry.toString(),
                            args)
            );
            tab.setTag(entry.getValue().getFollowerType().typeId);
            setTabTitle(tab, entry.getValue().titleRes, 0);
            actionBar.addTab(tab);
            if (savedSelectedId == entry.getValue().getFollowerType().typeId)
            {
                selectedTab = tab;
            }
        }
        if (selectedTab != null)
        {
            actionBar.selectTab(selectedTab);
        }

        int[] result = getFollowerCount();
        changeTabTitle(result[0], result[1], result[2]);

        Timber.d("addTabs");
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
        ActionBar actionBar = getSherlockActivity().getSupportActionBar();
        ActionBar.Tab tab = actionBar.getTabAt(page);
        String title = MessageFormat.format(getSherlockActivity().getString(titleRes), number);
        tab.setText(title);
    }

    private void changeTabTitle(int number1, int number2, int number3)
    {
        changeTabTitle(0, number1);
        changeTabTitle(1, number2);
        changeTabTitle(2, number3);

        Timber.d("changeTabTitle result:%d,%d,%d", number1, number2, number3);
    }

    private void setTabTitle(ActionBar.Tab tab, int titleRes, int number)
    {
        String title = "";
        title = MessageFormat.format(getSherlockActivity().getString(titleRes), number);
        tab.setText(title);
    }

    private void saveSelectedTab()
    {
        ActionBar actionBar = getSherlockActivity().getSupportActionBar();
        this.selectedId = (Integer) actionBar.getSelectedTab().getTag();
    }

    private void clearTabs()
    {
        ActionBar actionBar = getSherlockActivity().getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.removeAllTabs();
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
        int index = getSherlockActivity().getSupportActionBar().getSelectedNavigationIndex();
        Integer tagId =
                (Integer) getSherlockActivity().getSupportActionBar().getSelectedTab().getTag();
        int tabIndex = getSherlockActivity().getSupportActionBar().getSelectedTab().getPosition();

        HeroType followerType = HeroType.fromId(tagId);

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
        Timber.d("goToMessagePage index:%d,tabIndex:%d,followerType:%s,discussionType:%s", index,
                tabIndex, followerType, discussionType);
        ((DashboardActivity) getActivity()).getDashboardNavigator().pushFragment(
                SendMessageFragment.class, args);
    }

    public static interface OnFollowersLoadedListener
    {
        void onFollowerLoaded(int page, FollowerSummaryDTO followerSummaryDTO);
    }

    /**
     * Callback
     */
    private class MyTabListener extends TabListener
    {

        public MyTabListener(SherlockFragmentActivity activity,
                Class<? extends Fragment> fragmentClass, String tag, Bundle args)
        {
            super(activity, fragmentClass, tag, args);
        }

        @Override public void onTabSelected(ActionBar.Tab tab, FragmentTransaction ft)
        {
            if (mFragment == null)
            {
                mFragment = Fragment.instantiate(mActivity, mFragmentClass.getName(), mArgs);
                FollowerManagerTabFragment fragment = (FollowerManagerTabFragment) mFragment;
                fragment.setOnFollowersLoadedListener(onFollowersLoadedListener);
                ft.add(R.id.fragment_content, mFragment, mTag);
            }
            else
            {
                super.onTabSelected(tab, ft);
            }
        }
    }

    ///////////////////////////////////////////////////////////////////////////
}
