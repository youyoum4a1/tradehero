package com.tradehero.th.fragments.chinabuild.fragment.userCenter;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.tradehero.common.persistence.DTOCacheNew;
import com.tradehero.common.utils.THToast;
import com.tradehero.th.adapters.UserFriendsListAdapter;
import com.tradehero.th.api.social.FollowerSummaryDTO;
import com.tradehero.th.api.social.HeroDTOExtWrapper;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.api.users.UserProfileCompactDTO;
import com.tradehero.th.fragments.base.DashboardFragment;
import com.tradehero.th.fragments.chinabuild.listview.SecurityListView;
import com.tradehero.th.fragments.social.follower.FollowerManagerInfoFetcher;
import com.tradehero.th.fragments.social.hero.HeroManagerInfoFetcher;
import com.tradehero.th.utils.AlertDialogUtil;
import com.tradehero.th2.R;
import dagger.Lazy;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;
import org.jetbrains.annotations.NotNull;
import timber.log.Timber;

/*
   股神或者粉丝列表显示
 */
public class UserFriendsListFragment extends DashboardFragment
{
    public static final String BUNDLE_SHOW_USER_ID = "bundle_show_user_id";
    public static final String BUNDLE_SHOW_FRIENDS_TYPE = "bundle_show_friends_type";

    public static final int TYPE_FRIENDS_HERO = 0;
    public static final int TYPE_FRIENDS_FOLLOWS = 1;
    public int typeFriends = TYPE_FRIENDS_HERO;
    public UserBaseKey showUserBaseKey;

    public FollowerSummaryDTO followerSummaryDTO;
    public FollowerManagerInfoFetcher followerInfoFetcher;

    @Inject protected HeroManagerInfoFetcher heroInfoFetcher;
    @InjectView(R.id.listFriends) SecurityListView listView;

    private UserFriendsListAdapter adapter;
    @Inject Lazy<AlertDialogUtil> alertDialogUtilLazy;

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
        setHeadViewMiddleMain(typeFriends == TYPE_FRIENDS_FOLLOWS ? "粉丝" : "英雄");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.user_friends_list_fragment, container, false);
        ButterKnife.inject(this, view);
        this.heroInfoFetcher.setHeroListListener(new HeroManagerHeroListCacheListener());

        initView();
        fetchUserFriendList();
        startLoadding();
        return view;
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
        listView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>()
        {
            @Override public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView)
            {
                Timber.d("下拉刷新");
                fetchUserFriendList();
            }

            @Override public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView)
            {

            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override public void onItemClick(AdapterView<?> adapterView, View view, int i, long position)
            {
                UserProfileCompactDTO dto = (UserProfileCompactDTO) adapter.getItem((int) position);
                enterUserMainPage(dto.id);
            }
        });
    }

    public void enterUserMainPage(int userId)
    {
        Bundle bundle = new Bundle();
        bundle.putInt(UserMainPage.BUNDLE_USER_BASE_KEY, userId);
        pushFragment(UserMainPage.class, bundle);
    }

    public void fetchUserFriendList()
    {
        if (typeFriends == TYPE_FRIENDS_HERO)
        {
            fetchHeros();
        }
        else if (typeFriends == TYPE_FRIENDS_FOLLOWS)
        {
            fetchFollowers();
        }
    }

    public void startLoadding()
    {
        if (getActivity() != null)
        {
            alertDialogUtilLazy.get().showProgressDialog(getActivity(), "加载中");
        }
    }

    protected void fetchHeros()
    {
        this.heroInfoFetcher.fetch(showUserBaseKey);
    }

    protected void fetchFollowers()
    {
        detachInfoFetcher();
        followerInfoFetcher = new FollowerManagerInfoFetcher(createFollowerSummaryCacheListener());
        followerInfoFetcher.fetch(this.showUserBaseKey);
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
            adapter.setListData(list);
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
            adapter.setListData(list);
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
            Timber.d("onDTOReceived");
            initFollowerData(value);
            finish();
        }

        @Override public void onErrorThrown(@NotNull UserBaseKey key, @NotNull Throwable error)
        {
            THToast.show(R.string.error_fetch_follower);
            finish();
        }

        private void finish()
        {
            listView.onRefreshComplete();
            alertDialogUtilLazy.get().dismissProgressDialog();
        }
    }

    private class HeroManagerHeroListCacheListener
            implements DTOCacheNew.Listener<UserBaseKey, HeroDTOExtWrapper>
    {
        @Override public void onDTOReceived(@NotNull UserBaseKey key, @NotNull HeroDTOExtWrapper value)
        {
            //displayProgress(false);
            Timber.d("");
            initHeroData(value);
            finish();
        }

        @Override public void onErrorThrown(@NotNull UserBaseKey key, @NotNull Throwable error)
        {

            THToast.show(R.string.error_fetch_hero);
            finish();
        }

        private void finish()
        {
            listView.onRefreshComplete();
            alertDialogUtilLazy.get().dismissProgressDialog();
        }
    }

    @Override public void onStop()
    {

        super.onStop();
    }

    protected void detachInfoFetcher()
    {
        if (this.followerInfoFetcher != null)
        {
            this.followerInfoFetcher.onDestroyView();
        }
        this.followerInfoFetcher = null;

        if (this.heroInfoFetcher != null)
        {
            this.heroInfoFetcher.onDestroyView();
        }
        //this.heroInfoFetcher = null;
    }

    @Override public void onDestroyView()
    {
        detachInfoFetcher();
        ButterKnife.reset(this);
        super.onDestroyView();
    }

    @Override public void onDestroy()
    {
        super.onDestroy();
    }

    @Override public void onResume()
    {
        super.onResume();
    }
}
