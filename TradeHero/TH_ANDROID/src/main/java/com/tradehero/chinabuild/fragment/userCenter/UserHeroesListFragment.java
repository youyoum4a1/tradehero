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
import android.view.Menu;
import android.view.MenuInflater;
import com.handmark.pulltorefresh.library.pulltorefresh.PullToRefreshBase;
import com.handmark.pulltorefresh.library.pulltorefresh.PullToRefreshListView;
import com.tradehero.chinabuild.fragment.message.DiscussSendFragment;
import com.tradehero.common.fragment.HasSelectedItem;
import com.tradehero.common.persistence.DTOCacheNew;
import com.tradehero.common.utils.THToast;
import com.tradehero.th.R;
import com.tradehero.th.adapters.UserFriendsListAdapter;
import com.tradehero.th.api.social.HeroDTOExtWrapper;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.api.users.UserProfileCompactDTO;
import com.tradehero.th.fragments.base.DashboardFragment;
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
public class UserHeroesListFragment extends DashboardFragment implements HasSelectedItem
{
    public static final String BUNDLE_SHOW_USER_ID = "bundle_show_user_id";

    public UserBaseKey showUserBaseKey;

    @Inject protected HeroManagerInfoFetcher heroInfoFetcher;
    @InjectView(R.id.listFriends) PullToRefreshListView listView;
    @InjectView(R.id.tradeheroprogressbar_users) TradeHeroProgressBar tradeheroprogressbar_users;
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

        adapter = new UserFriendsListAdapter(getActivity(), true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        super.onCreateOptionsMenu(menu, inflater);
        setHeadViewMiddleMain("股神");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.user_friends_list_fragment, container, false);
        ButterKnife.inject(this, view);
        heroInfoFetcher.setHeroListListener(new HeroManagerHeroListCacheListener());
        initView();

        if (adapter.getCount() == 0)  {
            showProgressDlg();
            fetchUserFriendList();
        } else {
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
        int userId = bundle.getInt(BUNDLE_SHOW_USER_ID, 0);
        if (userId != 0)
        {
            showUserBaseKey = new UserBaseKey(userId);
        }
    }

    public void initView() {
        listView.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
        listView.setAdapter(adapter);
        listView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            @Override public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                fetchUserFriendList(true);
            }

            @Override public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) { }
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

    public void fetchUserFriendList(){
            fetchHeros(false);
    }
    public void fetchUserFriendList(boolean force)
    {
            fetchHeros(force);
    }

    protected void fetchHeros(boolean force) {
        if(force) {
            this.heroInfoFetcher.reloadHeroes(showUserBaseKey);
        } else {
            this.heroInfoFetcher.fetch(showUserBaseKey);
        }
    }

    public void initHeroData(HeroDTOExtWrapper value) {
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

    private class HeroManagerHeroListCacheListener
            implements DTOCacheNew.Listener<UserBaseKey, HeroDTOExtWrapper> {
        @Override public void onDTOReceived(@NotNull UserBaseKey key, @NotNull HeroDTOExtWrapper value)  {
            initHeroData(value);
            onFinish();
        }

        @Override public void onErrorThrown(@NotNull UserBaseKey key, @NotNull Throwable error) {

            THToast.show(R.string.error_fetch_hero);
            onFinish();
        }

        private void onFinish() {
            if(listView==null){

                return;
            }
            if(imgEmpty!=null) {
                listView.setEmptyView(imgEmpty);
            }
            listView.setAdapter(adapter);
            hideProgressDlg();
            listView.onRefreshComplete();
        }
    }

    @Override public void onDestroyView()
    {
        ButterKnife.reset(this);
        super.onDestroyView();
    }

    @Override public Object getSelectedItem()
    {
        return selectedItem;
    }

    private void showProgressDlg() {
        if (tradeheroprogressbar_users != null) {
            tradeheroprogressbar_users.setVisibility(View.VISIBLE);
            tradeheroprogressbar_users.startLoading();
        }
    }

    private void hideProgressDlg() {
        if (tradeheroprogressbar_users != null) {
            tradeheroprogressbar_users.stopLoading();
            tradeheroprogressbar_users.setVisibility(View.GONE);
        }
    }

}
