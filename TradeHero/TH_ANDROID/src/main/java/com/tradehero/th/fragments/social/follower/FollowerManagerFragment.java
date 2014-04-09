package com.tradehero.th.fragments.social.follower;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.tradehero.common.persistence.DTOCache;
import com.tradehero.common.utils.THLog;
import com.tradehero.common.utils.THToast;
import com.tradehero.th.R;
import com.tradehero.th.activities.DashboardActivity;
import com.tradehero.th.api.discussion.DiscussionType;
import com.tradehero.th.api.social.FollowerId;
import com.tradehero.th.api.social.FollowerSummaryDTO;
import com.tradehero.th.api.social.UserFollowerDTO;
import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.fragments.base.BaseFragment;
import com.tradehero.th.fragments.billing.BasePurchaseManagerFragment;
import com.tradehero.th.fragments.updatecenter.TabListener;
import com.tradehero.th.persistence.social.HeroKey;
import com.tradehero.th.persistence.social.HeroType;
import java.text.MessageFormat;
import javax.inject.Inject;
import timber.log.Timber;

/**
 * Created with IntelliJ IDEA. User: xavier Date: 11/11/13 Time: 11:04 AM To change this template use File | Settings | File Templates.
 */
public class FollowerManagerFragment extends BaseFragment /*BasePurchaseManagerFragment*/ implements
        View.OnClickListener
{
    public static class FollowerTypeExt
    {
        public final int titleRes;
        public final HeroType followerType;
        public final int pageIndex;
        public final Class<? extends Fragment> fragmentClass;

        public FollowerTypeExt(int titleRes, HeroType followerType, int pageIndex, Class<? extends Fragment> fragmentClass)
        {
            this.titleRes = titleRes;
            this.followerType = followerType;
            this.pageIndex = pageIndex;
            this.fragmentClass = fragmentClass;
        }

        public static FollowerTypeExt[] getSortedList()
        {
            HeroType[] arr = HeroType.values();
            int len = arr.length;
            FollowerTypeExt[] result = new FollowerTypeExt[arr.length];

            for (int i = 0; i < len; i++)
            {
                int typeId = arr[i].typeId;
                if (typeId == HeroType.PREMIUM.typeId)
                {
                    result[i] = new FollowerTypeExt(R.string.leaderboard_community_hero_premium,
                            HeroType.PREMIUM, 0, PrimiumFollowerFragment.class);
                }
                else if (typeId == HeroType.FREE.typeId)
                {
                    result[i] = new FollowerTypeExt(R.string.leaderboard_community_hero_free,
                            HeroType.FREE, 1, FreeFollowerFragment.class);
                }
                else if (typeId == HeroType.ALL.typeId)
                {
                    result[i] = new FollowerTypeExt(R.string.leaderboard_community_hero_all,
                            HeroType.ALL, 2, AllFollowerFragment.class);
                }
            }
            return result;
        }

        public static FollowerTypeExt fromIndex(FollowerTypeExt[] arr, int pageIndex)
        {
            for (FollowerTypeExt type : arr)
            {
                if (type.pageIndex == pageIndex)
                {
                    return type;
                }
            }
            return null;
        }
    }

    public static final String KEY_PAGE = FollowerManagerFragment.class.getName() + ".keyPage";
    public static final String KEY_ID = FollowerManagerFragment.class.getName() + ".keyId";

    public static final String BUNDLE_KEY_FOLLOWED_ID =
            FollowerManagerFragment.class.getName() + ".followedId";

    /** parent layout of broadcastView and whisperView */
    @InjectView(R.id.send_message_layout) View messageLayout;
    /** view to 'send broadcast' */
    @InjectView(R.id.send_message_broadcast) View broadcastView;
    /** view to 'send whisper' */
    @InjectView(R.id.send_message_whisper) View whisperView;

    private UserBaseKey followedId;
    /** categories of follower:premium,free,all */
    private FollowerTypeExt[] followerTypes;

    private int selectedId = -1;

    @Override public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        this.followedId = new UserBaseKey(getArguments().getInt(BUNDLE_KEY_FOLLOWED_ID));
        this.followerTypes = FollowerTypeExt.getSortedList();
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

    private void addTabs()
    {
        //TODO NestedFragments needs ChildFragmentManager
        //http://developer.android.com/about/versions/android-4.2.html#NestedFragments
        int savedSelectedId = selectedId;
        ActionBar.Tab selectedTab = null;
        ActionBar actionBar = getSherlockActivity().getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        //actionBar.setDisplayOptions(0, ActionBar.DISPLAY_SHOW_TITLE);

        FollowerTypeExt[] types = followerTypes;
        Bundle args = getArguments();
        if (args == null)
        {
            args = new Bundle();
        }
        for (FollowerTypeExt type : types)
        {
            args = new Bundle(args);
            args.putInt(KEY_PAGE, type.pageIndex);
            args.putInt(KEY_ID, type.followerType.typeId);
            ActionBar.Tab tab = actionBar.newTab().setTabListener(
                    new MyTabListener(getSherlockActivity(), type.fragmentClass, type.toString(), args));
            tab.setTag(type.followerType.typeId);
            setTabTitle(tab, type.titleRes, 0);
            actionBar.addTab(tab);
            if (savedSelectedId == type.followerType.typeId)
            {
                selectedTab = tab;
            }
        }
        if (selectedTab != null)
        {
            actionBar.selectTab(selectedTab);
        }

        Timber.d("addTabs");
    }

    private void changetTabTitle(int page, int number)
    {
        int titleRes = 0;
        int len = followerTypes.length;
        for (int i = 0; i < len; i++)
        {
            if (followerTypes[i].pageIndex == page)
            {
                titleRes = followerTypes[i].titleRes;
            }
        }
        ActionBar actionBar = getSherlockActivity().getSupportActionBar();
        ActionBar.Tab tab = actionBar.getTabAt(page);
        String title = MessageFormat.format(getSherlockActivity().getString(titleRes), number);
        tab.setText(title);
    }

    private void changetTabTitle(int number1, int number2, int number3)
    {
        changetTabTitle(0, number1);
        changetTabTitle(1, number2);
        changetTabTitle(2, number3);

        Timber.d("changetTabTitle result:%d,%d,%d", number1, number2, number3);
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

    OnFollowersLoadedListener onFollowersLoadedListener = new OnFollowersLoadedListener()
    {

        @Override public void onFollowerLoaded(int page, FollowerSummaryDTO value)
        {
            if (!isDetached())
            {
                //remove the function to send message
                //setMessageLayoutShown(false);
                changetTabTitle(value.paidFollowerCount, value.freeFollowerCount, (value.paidFollowerCount + value.freeFollowerCount));
            }
        }
    };

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
        Integer tagId = (Integer) getSherlockActivity().getSupportActionBar().getSelectedTab().getTag();
        int tabIndex = getSherlockActivity().getSupportActionBar().getSelectedTab().getPosition();
        Timber.d("goToMessagePage index:%d,tabIndex:%d", index, tabIndex);

        HeroType followerType = HeroType.fromId(tagId);

        Bundle args = new Bundle();

        args.putInt(SendMessageFragment.KEY_DISCUSSION_TYPE, discussionType.value);
        args.putInt(SendMessageFragment.KEY_MESSAGE_TYPE, followerType.typeId);

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

    public static class PrimiumFollowerFragment extends FollowerManagerTabFragment
    {

        public PrimiumFollowerFragment()
        {
        }
    }

    public static class AllFollowerFragment extends FollowerManagerTabFragment
    {

        public AllFollowerFragment()
        {
        }
    }

    public static class FreeFollowerFragment extends FollowerManagerTabFragment
    {

        public FreeFollowerFragment()
        {
        }
    }

    public static class FollowerManagerTabFragment extends BasePurchaseManagerFragment
    {
        private FollowerManagerViewContainer viewContainer;

        private FollowerAndPayoutListItemAdapter followerListAdapter;
        private UserBaseKey followedId;
        private FollowerSummaryDTO followerSummaryDTO;

        @Inject protected CurrentUserId currentUserId;
        private FollowerManagerInfoFetcher infoFetcher;

        int page;
        HeroType followerType;

        public FollowerManagerTabFragment()
        {
        }

        public FollowerManagerTabFragment(int page)
        {
            this.page = page;
        }

        //<editor-fold desc="BaseFragment.TabBarVisibilityInformer">
        @Override public boolean isTabBarVisible()
        {
            return false;
        }

        //</editor-fold>

        @Override public void onCreate(Bundle savedInstanceState)
        {
            super.onCreate(savedInstanceState);
            Bundle args = getArguments();
            this.page = args.getInt(KEY_PAGE);
            this.followerType = HeroType.fromId(args.getInt(KEY_ID));
        }

        @Override public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState)
        {
            View view =
                    inflater.inflate(R.layout.fragment_store_manage_followers, container, false);
            initViews(view);
            Timber.d("FollowerManagerTabFragment onCreateView");
            return view;
        }

        @Override protected void initViews(View view)
        {
            this.viewContainer = new FollowerManagerViewContainer(view);
            this.infoFetcher =
                    new FollowerManagerInfoFetcher(new FollowerManagerFollowerSummaryListener());

            if (followerListAdapter == null)
            {
                followerListAdapter = new FollowerAndPayoutListItemAdapter(getActivity(),
                        getActivity().getLayoutInflater(),
                        R.layout.follower_list_header,
                        R.layout.hero_payout_list_item,
                        R.layout.hero_payout_none_list_item,
                        R.layout.follower_list_item,
                        R.layout.follower_none_list_item
                );
            }

            if (this.viewContainer.followerList != null)
            {
                this.viewContainer.followerList.setOnItemClickListener(
                        new AdapterView.OnItemClickListener()
                        {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position,
                                    long id)
                            {
                                handleFollowerItemClicked(view, position, id);
                            }
                        }
                );
                this.viewContainer.followerList.setAdapter(followerListAdapter);
            }
        }

        @Override public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
        {
            ActionBar actionBar = getSherlockActivity().getSupportActionBar();
            actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_HOME
                    | ActionBar.DISPLAY_SHOW_TITLE
                    | ActionBar.DISPLAY_HOME_AS_UP);
            actionBar.setTitle(R.string.manage_followers_title);
            super.onCreateOptionsMenu(menu, inflater);
        }

        @Override public void onResume()
        {
            super.onResume();
            Timber.d("FollowerManagerTabFragment onResume");
            this.followedId = new UserBaseKey(getArguments().getInt(BUNDLE_KEY_FOLLOWED_ID));

            //May be null(getSelectedTab)
            //Integer tagId = (Integer)getSherlockActivity().getSupportActionBar().getSelectedTab().getTag();
            //int tabIndex = getSherlockActivity().getSupportActionBar().getSelectedTab().getPosition();
            //HeroType followerType = HeroType.fromId(tagId);
            this.infoFetcher.fetch(this.followedId, followerType);
        }

        @Override public void onPause()
        {
            this.infoFetcher.onPause();
            super.onPause();
        }

        @Override public void onDestroyView()
        {
            if (this.viewContainer.followerList != null)
            {
                this.viewContainer.followerList.setOnItemClickListener(null);
            }
            this.viewContainer = null;
            this.followerListAdapter = null;
            this.infoFetcher = null;
            super.onDestroyView();
        }

        public void display(FollowerSummaryDTO summaryDTO)
        {
            linkWith(summaryDTO, true);
        }

        public void linkWith(FollowerSummaryDTO summaryDTO, boolean andDisplay)
        {
            this.followerSummaryDTO = summaryDTO;
            if (andDisplay)
            {
                this.viewContainer.displayTotalRevenue(summaryDTO);
                this.viewContainer.displayTotalAmountPaid(summaryDTO);
                this.viewContainer.displayFollowersCount(summaryDTO);
                displayFollowerList();
            }
        }

        public void display()
        {
            this.viewContainer.displayTotalRevenue(this.followerSummaryDTO);
            this.viewContainer.displayTotalAmountPaid(this.followerSummaryDTO);
            this.viewContainer.displayFollowersCount(this.followerSummaryDTO);
            displayFollowerList();
        }

        public void displayFollowerList()
        {
            if (this.followerListAdapter != null)
            {
                this.followerListAdapter.setFollowerSummaryDTO(this.followerSummaryDTO);
            }
        }

        public void displayProgress(boolean running)
        {
            if (this.viewContainer.progressBar != null)
            {
                this.viewContainer.progressBar.setVisibility(running ? View.VISIBLE : View.GONE);
            }
        }

        private void handleFollowerItemClicked(View view, int position, long id)
        {
            if (followerListAdapter != null
                    && followerListAdapter.getItemViewType(position)
                    == FollowerAndPayoutListItemAdapter.VIEW_TYPE_ITEM_FOLLOWER)
            {
                UserFollowerDTO followerDTO =
                        (UserFollowerDTO) followerListAdapter.getItem(position);
                if (followerDTO != null)
                {
                    FollowerId followerId =
                            new FollowerId(getApplicablePortfolioId().userId, followerDTO.id);
                    Bundle args = new Bundle();
                    args.putBundle(FollowerPayoutManagerFragment.BUNDLE_KEY_FOLLOWER_ID_BUNDLE,
                            followerId.getArgs());
                    ((DashboardActivity) getActivity()).getDashboardNavigator()
                            .pushFragment(FollowerPayoutManagerFragment.class, args);
                }
                else
                {
                    Timber.d("handleFollowerItemClicked: FollowerDTO was null");
                }
            }
            else
            {
                THToast.show("Position clicked " + position);
            }
        }

        private class FollowerManagerFollowerSummaryListener
                implements DTOCache.Listener<HeroKey, FollowerSummaryDTO>
        {
            @Override
            public void onDTOReceived(HeroKey key, FollowerSummaryDTO value, boolean fromCache)
            {
                displayProgress(false);
                display(value);
                notifyFollowerLoaded(value);
            }

            @Override public void onErrorThrown(HeroKey key, Throwable error)
            {
                displayProgress(false);
                THToast.show(R.string.error_fetch_follower);
                Timber.e("Failed to fetch FollowerSummary", error);
            }
        }

        OnFollowersLoadedListener onFollowersLoadedListener;

        public void setOnFollowersLoadedListener(OnFollowersLoadedListener listener)
        {
            this.onFollowersLoadedListener = listener;
        }

        private void notifyFollowerLoaded(FollowerSummaryDTO value)
        {
            Timber.d("notifyFollowerLoaded for page:%d", page);
            if (onFollowersLoadedListener != null && !isDetached())
            {
                onFollowersLoadedListener.onFollowerLoaded(page, value);
            }
        }
    }
}
