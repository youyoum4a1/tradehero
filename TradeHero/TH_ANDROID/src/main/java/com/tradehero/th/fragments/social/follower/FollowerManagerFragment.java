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
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.tradehero.common.persistence.DTOCache;
import com.tradehero.common.utils.THLog;
import com.tradehero.common.utils.THToast;
import com.tradehero.th.R;
import com.tradehero.th.activities.DashboardActivity;
import com.tradehero.th.api.social.FollowerId;
import com.tradehero.th.api.social.FollowerSummaryDTO;
import com.tradehero.th.api.social.UserFollowerDTO;
import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.fragments.base.BaseFragment;
import com.tradehero.th.fragments.billing.BasePurchaseManagerFragment;
import com.tradehero.th.persistence.social.HeroKey;
import com.tradehero.th.persistence.social.HeroType;
import java.text.MessageFormat;
import javax.inject.Inject;
import timber.log.Timber;

/**
 * Created with IntelliJ IDEA. User: xavier Date: 11/11/13 Time: 11:04 AM To change this template
 * use File | Settings | File Templates.
 */
public class FollowerManagerFragment extends BaseFragment /*BasePurchaseManagerFragment*/ implements
        View.OnClickListener
{

    public static class FollowerTypeExt
    {
        public final int titleRes;
        public final HeroType followerType;
        public final int pageIndex;

        public FollowerTypeExt(int titleRes, HeroType followerType,int pageIndex)
        {
            this.titleRes = titleRes;
            this.followerType = followerType;
            this.pageIndex = pageIndex;
        }

        public static FollowerTypeExt[] getSortedList()
        {
            HeroType[] arr = HeroType.values();
            int len = arr.length;
            FollowerTypeExt[] result = new FollowerTypeExt[arr.length];

            for(int i=0;i< len;i++){
                int typeId = arr[i].typeId;
                if (typeId== HeroType.PREMIUM.typeId){
                    result[i] = new FollowerTypeExt(R.string.leaderboard_community_hero_premium,
                            HeroType.PREMIUM,0);
                }else if (typeId== HeroType.FREE.typeId){
                    result[i] = new FollowerTypeExt(R.string.leaderboard_community_hero_free,
                            HeroType.FREE,1);
                }else if (typeId== HeroType.ALL.typeId){
                    result[i] = new FollowerTypeExt(R.string.leaderboard_community_hero_all,
                            HeroType.ALL,2);
                }
            }
            return result;
        }

        public static FollowerTypeExt fromIndex(FollowerTypeExt[] arr,int pageIndex)
        {
            for (FollowerTypeExt type:arr)
            {
                if (type.pageIndex == pageIndex)
                {
                    return type;
                }
            }
            return null;

        }

    }

    public static enum MessageType
    {
        MESSAGE_TYPE_BROADCAST(0),
        MESSAGE_TYPE_WHISPER(1);

        public final int typeId;
        private MessageType(int typeId)
        {
            this.typeId = typeId;
        }
        //
    }
    public static final String KEY_MESSAGE_TYPE = "msg_type";

    public static final String KEY_FOLLOER_TYPE = "follower_type";


    public static final String TAG = FollowerManagerFragment.class.getSimpleName();

    public static final String BUNDLE_KEY_FOLLOWED_ID =
            FollowerManagerFragment.class.getName() + ".followedId";


    /**parent layout of broadcastView and whisperView*/
    @InjectView(R.id.send_message_layout) View messageLayout;
    /**view to 'send broadcast'*/
    @InjectView(R.id.send_message_broadcast) View broadcastView;
    /**view to 'send whisper'*/
    @InjectView(R.id.send_message_whisper) View whisperView;

    private UserBaseKey followedId;
    /**categories of follower:premium,free,all*/
    private FollowerTypeExt[] followerTypes;
    @Override public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        this.followedId = new UserBaseKey(getArguments().getInt(BUNDLE_KEY_FOLLOWED_ID));
        this.followerTypes = FollowerTypeExt.getSortedList();
    }

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState)
    {
        Timber.d("%s,onCreateView",TAG);
        View view = inflater.inflate(R.layout.fragment_store_manage_followers_2, container, false);
        ButterKnife.inject(this, view);
        addTabs();
        return view;
    }

    @Override public void onViewCreated(View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        setMessageLayoutShown(false);
    }

    @Override public void onActivityCreated(Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);
        //setMessageLayoutShown(false);
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
        messageLayout.setVisibility(shown ? View.VISIBLE : View.GONE);
    }

    private void addTabs()
    {
        ActionBar actionBar = getSherlockActivity().getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        //actionBar.setDisplayOptions(0, ActionBar.DISPLAY_SHOW_TITLE);

        FollowerTypeExt[] types = followerTypes;
        for (FollowerTypeExt type:types)
        {
            FollowerManagerTabFragment fragment = null;
            switch (type.followerType)
            {
                case PREMIUM:
                    fragment = new PrimiumFollowerFragment(type.pageIndex);
                    break;
                case FREE:
                    fragment = new FreeFollowerFragment(type.pageIndex);
                    break;
                case ALL:
                    fragment = new AllFollowerFragment(type.pageIndex);
                    break;
                default:
                    break;
            }
            fragment.setArguments(getArguments());
            fragment.setOnFollowersLoadedListener(onFollowersLoadedListener);
            //Action Bar Tab must have a Callback
            ActionBar.Tab tab = actionBar.newTab().setTabListener(
                    new TabListener(fragment));
            tab.setTag(type.followerType.typeId);
            setTabTitle(tab, type.titleRes, 0);
            actionBar.addTab(tab);
        }

        Timber.d("%s,addTabs",TAG);
    }

    private void changetTabTitle(int page, int number)
    {
        int titleRes = 0;
        int len = followerTypes.length;
        for (int i=0;i<len;i++)
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

    private void changetTabTitle(int number1,int number2,int number3)
    {
        changetTabTitle(0,number1);
        changetTabTitle(1,number2);
        changetTabTitle(2,number3);

        Timber.d("%s,changetTabTitle result:%d,%d,%d",TAG,number1,number2,number3);
    }

    private void setTabTitle(ActionBar.Tab tab, int titleRes, int number)
    {
        String title = "";
        title = MessageFormat.format(getSherlockActivity().getString(titleRes), number);
        tab.setText(title);
    }

    private void clearTabs()
    {
        ActionBar actionBar = getSherlockActivity().getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.removeAllTabs();
    }

    OnFollowersLoadedListener onFollowersLoadedListener = new OnFollowersLoadedListener()
    {

        @Override public void onFollowerLoaded(int page,FollowerSummaryDTO value)
        {
            if (!isDetached())
            {
                //remove the function to send message
                setMessageLayoutShown(false);
                changetTabTitle(value.paidFollowerCount,value.freeFollowerCount,(value.paidFollowerCount+value.freeFollowerCount));
            }
        }
    };

    @Override public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.send_message_whisper:
                goToMessagePage(MessageType.MESSAGE_TYPE_WHISPER.typeId);
                break;
            case R.id.send_message_broadcast:
                goToMessagePage(MessageType.MESSAGE_TYPE_BROADCAST.typeId);
                break;
            default:
                break;
        }
    }

    private void goToMessagePage(int messageType)
    {
        int index = getSherlockActivity().getSupportActionBar().getSelectedNavigationIndex();
        Integer tagId = (Integer)getSherlockActivity().getSupportActionBar().getSelectedTab().getTag();
        int tabIndex = getSherlockActivity().getSupportActionBar().getSelectedTab().getPosition();
        Timber.d("goToMessagePage index:%d,tabIndex:%d",index,tabIndex);

        HeroType followerType = HeroType.fromId(tagId);

        Bundle args = new Bundle();

        args.putInt(KEY_MESSAGE_TYPE, messageType);
        args.putInt(KEY_FOLLOER_TYPE, followerType.typeId);
        args.putInt(KEY_MESSAGE_TYPE, followedId.key);

        ((DashboardActivity) getActivity()).getDashboardNavigator().pushFragment(
                SendMessageFragment.class, args);
    }

    public static interface OnFollowersLoadedListener
    {
        void onFollowerLoaded(int page,FollowerSummaryDTO followerSummaryDTO);
    }

    /**
     * Callback
     */
    private class TabListener implements ActionBar.TabListener
    {

        private Fragment mFragment;

        public TabListener(Fragment fragment)
        {
            mFragment = fragment;
        }

        @Override public void onTabSelected(ActionBar.Tab tab, FragmentTransaction ft)
        {

            ft.add(R.id.fragment_content, mFragment, mFragment.getTag());
        }

        @Override public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction ft)
        {
            ft.remove(mFragment);
        }

        @Override public void onTabReselected(ActionBar.Tab tab, FragmentTransaction ft)
        {
            //Toast.makeText(ActionBarTabs.this, "Reselected!", Toast.LENGTH_SHORT).show();
        }
    }

    public static class PrimiumFollowerFragment extends FollowerManagerTabFragment
    {

        public PrimiumFollowerFragment(int page)
        {
            super(page);
        }
    }

    public static class AllFollowerFragment extends FollowerManagerTabFragment
    {

        public AllFollowerFragment(int page)
        {
            super(page);
        }
    }


    public static class FreeFollowerFragment extends FollowerManagerTabFragment
    {

        public FreeFollowerFragment(int page)
        {
            super(page);
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

        @Override public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState)
        {
            View view =
                    inflater.inflate(R.layout.fragment_store_manage_followers, container, false);
            initViews(view);
            Timber.d("%s,FollowerManagerTabFragment onCreateView",TAG);
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
                        });
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
            Timber.d("%s,FollowerManagerTabFragment onResume",TAG);
            this.followedId = new UserBaseKey(getArguments().getInt(BUNDLE_KEY_FOLLOWED_ID));

            Integer tagId = (Integer)getSherlockActivity().getSupportActionBar().getSelectedTab().getTag();
            int tabIndex = getSherlockActivity().getSupportActionBar().getSelectedTab().getPosition();

            HeroType followerType = HeroType.fromId(tagId);
            this.infoFetcher.fetch(this.followedId,followerType);
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
                    THLog.d(TAG, "handleFollowerItemClicked: FollowerDTO was null");
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
                THLog.e(TAG, "Failed to fetch FollowerSummary", error);
            }
        }

        OnFollowersLoadedListener onFollowersLoadedListener;

        public void setOnFollowersLoadedListener(OnFollowersLoadedListener listener)
        {
            this.onFollowersLoadedListener = listener;
        }

        private void notifyFollowerLoaded(FollowerSummaryDTO value)
        {
            Timber.d("%s,notifyFollowerLoaded for page:%d",TAG,page);
            if (onFollowersLoadedListener != null && !isDetached())
            {
                onFollowersLoadedListener.onFollowerLoaded(page,value);
            }
        }
    }
}
