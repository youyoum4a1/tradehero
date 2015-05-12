package com.tradehero.chinabuild.fragment.userCenter;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import butterknife.ButterKnife;
import android.view.Menu;
import android.view.MenuInflater;
import com.handmark.pulltorefresh.library.pulltorefresh.PullToRefreshBase;
import com.handmark.pulltorefresh.library.pulltorefresh.PullToRefreshListView;
import com.tradehero.th.R;
import com.tradehero.th.adapters.UserFriendsListAdapter;
import com.tradehero.th.api.social.FollowerSummaryDTO;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.api.users.UserProfileCompactDTO;
import com.tradehero.th.fragments.base.DashboardFragment;
import com.tradehero.th.network.service.FollowerServiceWrapper;
import com.tradehero.th.widget.TradeHeroProgressBar;
import dagger.Lazy;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by palmer on 15/2/25.
 */
public class UserFansListFragment extends DashboardFragment {

    private TradeHeroProgressBar tradeheroprogressbar_users;
    private PullToRefreshListView fansLV;
    private ImageView emptyIV;

    private UserFriendsListAdapter adapter;

    private UserBaseKey showUserBaseKey;

    private final int perPage = 20;
    private int page = 1;

    @Inject Lazy<FollowerServiceWrapper> followerServiceWrapper;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initArgument();
        adapter = new UserFriendsListAdapter(getActivity(), false);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.user_fans_list_fragment, container, false);
        ButterKnife.inject(this, view);
        tradeheroprogressbar_users = (TradeHeroProgressBar) view.findViewById(R.id.tradeheroprogressbar_users);
        fansLV = (PullToRefreshListView) view.findViewById(R.id.listFriends);
        emptyIV = (ImageView) view.findViewById(R.id.imgEmpty);

        initFansLV();

        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        setHeadViewMiddleMain("粉丝");
    }

    private void initFansLV(){
        if(adapter!=null){
            fansLV.setAdapter(adapter);
        }else {
            adapter = new UserFriendsListAdapter(getActivity(), false);
        }
        fansLV.setMode(PullToRefreshBase.Mode.BOTH);
        fansLV.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                retrieveFans();
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                retrieveFansMore();
            }
        });
        adapter.setOnUserItemClickListener(new UserFriendsListAdapter.OnUserItemClickListener() {
            @Override
            public void onUserItemClick(int position) {
                UserProfileCompactDTO dto = (UserProfileCompactDTO) adapter.getItem((int) position);
                enterUserMainPage(dto.id);
            }
        });
        if(adapter.getCount()<=0){
            showProgressDlg();
            retrieveFans();
        }
    }

    private void retrieveFans(){
        page = 1;
        followerServiceWrapper.get().getFollowers(showUserBaseKey, perPage, page, new RetrieveFansCallback());
    }

    private void retrieveFansMore(){
        page ++;
        followerServiceWrapper.get().getFollowers(showUserBaseKey, perPage, page, new RetrieveFansCallback());
    }

    private class RetrieveFansCallback implements Callback<FollowerSummaryDTO>{

        @Override
        public void success(FollowerSummaryDTO followers, Response response) {
            if(followers!=null && followers.userFollowers !=null && followers.userFollowers.size()>0 && adapter!=null) {
                List<UserProfileCompactDTO> list = new ArrayList<UserProfileCompactDTO>();
                list.addAll(followers.userFollowers);
                if(page==1) {
                    adapter.setListData(list);
                }
                if(page>1){
                    adapter.addListData(list);
                }
                adapter.notifyDataSetChanged();
            }
            onFinish();
        }

        @Override
        public void failure(RetrofitError retrofitError) {
            if(page>1){
                page--;
            }
            onFinish();
        }

        private void onFinish(){
            fansLV.onRefreshComplete();
            hideProgressDlg();
            if(fansLV!=null && emptyIV!=null){
                fansLV.setEmptyView(emptyIV);
            }
        }
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

    private void initArgument(){
        Bundle bundle = getArguments();
        int userId = bundle.getInt(UserHeroesListFragment.BUNDLE_SHOW_USER_ID, 0);
        if (userId != 0) {
            showUserBaseKey = new UserBaseKey(userId);
        }
    }

    private void enterUserMainPage(int userId) {
        Bundle bundle = new Bundle();
        bundle.putInt(UserMainPage.BUNDLE_USER_BASE_KEY, userId);
        pushFragment(UserMainPage.class, bundle);
    }
}
