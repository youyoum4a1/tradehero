package com.tradehero.chinabuild.fragment.userCenter;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.handmark.pulltorefresh.library.pulltorefresh.PullToRefreshBase;
import com.tradehero.chinabuild.fragment.message.DiscussSendFragment;
import com.tradehero.chinabuild.listview.SecurityListView;
import com.tradehero.common.fragment.HasSelectedItem;
import com.tradehero.common.persistence.DTOCacheNew;
import com.tradehero.common.utils.THToast;
import com.tradehero.common.widget.BetterViewAnimator;
import com.tradehero.th.R;
import com.tradehero.th.adapters.UserFriendsListAdapter;
import com.tradehero.th.api.social.FollowerSummaryDTO;
import com.tradehero.th.api.social.HeroDTOExtWrapper;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.api.users.UserProfileCompactDTO;
import com.tradehero.th.fragments.base.DashboardFragment;
import com.tradehero.th.fragments.social.follower.FollowerManagerInfoFetcher;
import com.tradehero.th.fragments.social.hero.HeroManagerInfoFetcher;
import com.tradehero.th.utils.InputTools;
import com.tradehero.th.widget.ABCDView;
import com.tradehero.th.widget.TradeHeroProgressBar;
import org.jetbrains.annotations.NotNull;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

/*
   股神或者粉丝列表显示
 */
public class UserFriendsListFragment extends DashboardFragment implements HasSelectedItem
{
    public static final String BUNDLE_SHOW_USER_ID = "bundle_show_user_id";
    public static final String BUNDLE_SHOW_FRIENDS_TYPE = "bundle_show_friends_type";

    public static final int TYPE_FRIENDS_HERO = 0;
    public static final int TYPE_FRIENDS_FOLLOWS = 1;
    public static final int TYPE_FRIENDS_ALL = 2;
    public int typeFriends = TYPE_FRIENDS_HERO;
    public UserBaseKey showUserBaseKey;

    public FollowerSummaryDTO followerSummaryDTO;
    public FollowerManagerInfoFetcher followerInfoFetcher;

    @Inject protected HeroManagerInfoFetcher heroInfoFetcher;
    @InjectView(R.id.listFriends) SecurityListView listView;
    @InjectView(R.id.bvaViewAll) BetterViewAnimator betterViewAnimator;
    @InjectView(R.id.tradeheroprogressbar_users) TradeHeroProgressBar progressBar;
    @InjectView(R.id.imgEmpty) ImageView imgEmpty;

    //Divider View
    private ABCDView dividerABCDView;
    private TextView showDividerView;

    protected UserProfileCompactDTO selectedItem;

    private UserFriendsListAdapter adapter;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        initArgument();

        adapter = new UserFriendsListAdapter(getActivity());
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        super.onCreateOptionsMenu(menu, inflater);
        setHeadViewMiddleMain(typeFriends == TYPE_FRIENDS_FOLLOWS ? "粉丝" : "股神");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.user_friends_list_fragment, container, false);
        ButterKnife.inject(this, view);
        heroInfoFetcher.setHeroListListener(new HeroManagerHeroListCacheListener());
        initView();
        fetchUserFriendList();

        if (adapter.getCount() == 0)
        {
            betterViewAnimator.setDisplayedChildByLayoutId(R.id.tradeheroprogressbar_users);
            progressBar.startLoading();
        }
        else
        {
            betterViewAnimator.setDisplayedChildByLayoutId(R.id.relativelayout_listfriends);
            dividerABCDView.setVisibility(View.VISIBLE);
        }
        showDividerView = (TextView)view.findViewById(R.id.textview_show_divider);
        dividerABCDView = (ABCDView)view.findViewById(R.id.abcdview_divider);
        dividerABCDView.setListener(new ABCDView.OnCharTouchListener() {
            @Override
            public void onTouchDown(String divider) {
                int position = adapter.getPosition(divider);
                showDividerView.setVisibility(View.VISIBLE);
                showDividerView.setText(divider);
                if(position==-1){
                    return;
                }
                listView.getRefreshableView().setSelection(position + 1);
            }

            @Override
            public void onTouchUp(String divider) {
                int position = adapter.getPosition(divider);
                showDividerView.setVisibility(View.GONE);
                showDividerView.setText(divider);
                if(position==-1){
                    return;
                }
                listView.getRefreshableView().setSelection(position + 1);
            }

            @Override
            public void onTouchCancel() {
                showDividerView.setVisibility(View.GONE);
                showDividerView.setText("");
            }

            @Override
            public void onTouchMove(String divider) {
                int position = adapter.getPosition(divider);
                showDividerView.setVisibility(View.VISIBLE);
                showDividerView.setText(divider);
                if(position==-1){
                    return;
                }
                listView.getRefreshableView().setSelection(position + 1);
            }
        });
        return view;
    }

    @Override
    public void onResume(){
        super.onResume();
        if(getActivity()!=null) {
            InputTools.dismissKeyBoard(getActivity());
        }
    }

    public void initArgument()
    {
        Bundle bundle = getArguments();
        typeFriends = bundle.getInt(BUNDLE_SHOW_FRIENDS_TYPE, 0);
        int userId = bundle.getInt(BUNDLE_SHOW_USER_ID, 0);
        if (userId != 0)
        {
            showUserBaseKey = new UserBaseKey(userId);
        }
    }

    public void initView()
    {
        listView.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
        listView.setAdapter(adapter);
        listView.setEmptyView(imgEmpty);
        listView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>()
        {
            @Override public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView)
            {
                fetchUserFriendList(true);
            }

            @Override public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView)
            {

            }
        });
        adapter.setOnUserItemClickListener(new UserFriendsListAdapter.OnUserItemClickListener() {
            @Override
            public void onUserItemClick(int position) {
                UserProfileCompactDTO dto = (UserProfileCompactDTO) adapter.getItem((int) position);
                enterUserMainPage(dto.id, dto);
            }
        });
    }

    public void enterUserMainPage(int userId, UserProfileCompactDTO dto)
    {
        if (getArguments() != null && getArguments().containsKey(
                DiscussSendFragment.BUNDLE_KEY_RETURN_FRAGMENT))
        {
            selectedItem = dto;
            popCurrentFragment();
            return;
        }
        else
        {
            Bundle bundle = new Bundle();
            bundle.putInt(UserMainPage.BUNDLE_USER_BASE_KEY, userId);
            pushFragment(UserMainPage.class, bundle);
        }
    }

    boolean isEmptyHeroAndFollower = false;

    public void fetchUserFriendList()
    {
        if (typeFriends == TYPE_FRIENDS_HERO)
        {
            fetchHeros(false);
        }
        else if (typeFriends == TYPE_FRIENDS_FOLLOWS)
        {
            fetchFollowers(false);
        }
        else if (typeFriends == TYPE_FRIENDS_ALL)
        {
            clearHeroAndFollower();
            fetchHeros(false);
            fetchFollowers(false);
        }
    }

    public void clearHeroAndFollower()
    {
        isEmptyHeroAndFollower = true;
    }


    public void fetchUserFriendList(boolean force)
    {
        if (typeFriends == TYPE_FRIENDS_HERO)
        {
            fetchHeros(force);
        }
        else if (typeFriends == TYPE_FRIENDS_FOLLOWS)
        {
            fetchFollowers(force);
        }
        else if (typeFriends == TYPE_FRIENDS_ALL)
        {
            clearHeroAndFollower();
            fetchHeros(force);
            fetchFollowers(force);
        }
    }

    protected void fetchHeros(boolean force)
    {
        if(force)
        {
            this.heroInfoFetcher.reloadHeroes(showUserBaseKey);
        }
        else
        {
            this.heroInfoFetcher.fetch(showUserBaseKey);
        }
    }

    protected void fetchFollowers(boolean force)
    {
        detachFollowerFetcher();
        followerInfoFetcher = new FollowerManagerInfoFetcher(createFollowerSummaryCacheListener());
        followerInfoFetcher.fetch(this.showUserBaseKey,force);
    }

    protected DTOCacheNew.Listener<UserBaseKey, FollowerSummaryDTO> createFollowerSummaryCacheListener()
    {
        return new FollowerManagerFollowerSummaryListener();
    }

    public void initFollowerData(FollowerSummaryDTO value)
    {
        if (value == null) return;
        if (value.userFollowers == null) return;
        int size = value.userFollowers.size();
        if (size > 0)
        {
            List<UserProfileCompactDTO> list = new ArrayList<UserProfileCompactDTO>();
            for (int i = 0; i < value.userFollowers.size(); i++)
            {
                list.add(value.userFollowers.get(i));
            }
            if (typeFriends == TYPE_FRIENDS_ALL)
            {
                if(isEmptyHeroAndFollower){
                    adapter.setListData(list);
                    isEmptyHeroAndFollower = false;
                }else
                {
                    adapter.addListData(list);
                }
            }
            else
            {
                adapter.setListData(list);
            }
            if(adapter.getCount()<=0){
                if(dividerABCDView!=null) {
                    dividerABCDView.setVisibility(View.GONE);
                }
            }else{
                if(dividerABCDView!=null) {
                    dividerABCDView.setVisibility(View.VISIBLE);
                }
            }
            adapter.notifyDataSetChanged();
        }
    }

    public void initHeroData(HeroDTOExtWrapper value)
    {
        if (value == null) return;
        if (value.allActiveHeroes == null) return;
        int size = value.allActiveHeroes.size();
        if (size > 0)
        {
            List<UserProfileCompactDTO> list = new ArrayList<UserProfileCompactDTO>();
            for (int i = 0; i < value.allActiveHeroes.size(); i++)
            {
                list.add(value.allActiveHeroes.get(i));
            }

            if (typeFriends == TYPE_FRIENDS_ALL)
            {
                if(isEmptyHeroAndFollower){
                    adapter.setListData(list);
                    isEmptyHeroAndFollower = false;
                }else
                {
                    adapter.addListData(list);
                }
            }
            else
            {
                adapter.setListData(list);
            }
            if(adapter.getCount()<=0){
                if(dividerABCDView!=null) {
                    dividerABCDView.setVisibility(View.GONE);
                }
            }else{
                if(dividerABCDView!=null) {
                    dividerABCDView.setVisibility(View.VISIBLE);
                }
            }
            adapter.notifyDataSetChanged();
        }
    }

    protected class FollowerManagerFollowerSummaryListener
            implements DTOCacheNew.Listener<UserBaseKey, FollowerSummaryDTO>
    {
        @Override
        public void onDTOReceived(@NotNull UserBaseKey key, @NotNull FollowerSummaryDTO value)
        {
            followerSummaryDTO = value;
            initFollowerData(value);
            onFinish();
        }

        @Override public void onErrorThrown(@NotNull UserBaseKey key, @NotNull Throwable error)
        {
            THToast.show(R.string.error_fetch_follower);
            onFinish();
        }

        private void onFinish()
        {
            listView.onRefreshComplete();
            betterViewAnimator.setDisplayedChildByLayoutId(R.id.relativelayout_listfriends);
            if(progressBar != null){
                progressBar.stopLoading();
            }
        }
    }

    private class HeroManagerHeroListCacheListener
            implements DTOCacheNew.Listener<UserBaseKey, HeroDTOExtWrapper>
    {
        @Override public void onDTOReceived(@NotNull UserBaseKey key, @NotNull HeroDTOExtWrapper value)
        {
            initHeroData(value);
            onFinish();
        }

        @Override public void onErrorThrown(@NotNull UserBaseKey key, @NotNull Throwable error)
        {

            THToast.show(R.string.error_fetch_hero);
            onFinish();
        }

        private void onFinish()
        {
            try {
                listView.onRefreshComplete();
                betterViewAnimator.setDisplayedChildByLayoutId(R.id.relativelayout_listfriends);
            } catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }

    protected void detachFollowerFetcher()
    {
        if (this.followerInfoFetcher != null)
        {
            this.followerInfoFetcher.onDestroyView();
        }
        this.followerInfoFetcher = null;
    }

    @Override public void onDestroyView()
    {
        detachFollowerFetcher();
        ButterKnife.reset(this);
        super.onDestroyView();
    }

    @Override public Object getSelectedItem()
    {
        return selectedItem;
    }


}
