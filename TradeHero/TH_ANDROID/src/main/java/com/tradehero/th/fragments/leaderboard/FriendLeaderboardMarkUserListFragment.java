package com.tradehero.th.fragments.leaderboard;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.localytics.android.LocalyticsSession;
import com.tradehero.common.utils.THToast;
import com.tradehero.common.widget.BetterViewAnimator;
import com.tradehero.th.R;
import com.tradehero.th.api.leaderboard.LeaderboardUserDTO;
import com.tradehero.th.api.leaderboard.position.LeaderboardFriendsDTO;
import com.tradehero.th.misc.exception.THException;
import com.tradehero.th.network.retrofit.MiddleCallback;
import com.tradehero.th.network.service.LeaderboardServiceWrapper;
import com.tradehero.th.utils.metrics.localytics.LocalyticsConstants;
import java.util.Date;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Provider;
import org.ocpsoft.prettytime.PrettyTime;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import timber.log.Timber;

//public class FriendLeaderboardMarkUserListFragment extends LeaderboardMarkUserListFragment
public class FriendLeaderboardMarkUserListFragment extends BaseLeaderboardFragment
{
    @InjectView(R.id.leaderboard_mark_user_listview) LeaderboardMarkUserListView leaderboardMarkUserListView;
    @InjectView(R.id.leaderboard_mark_user_screen) BetterViewAnimator leaderboardMarkUserScreen;

    private TextView leaderboardMarkUserMarkingTime;
    protected LeaderboardFriendsListAdapter leaderboardMarkUserListAdapter;
    private MiddleCallback<LeaderboardFriendsDTO> getFriendsMiddleCallback;
    @Inject Provider<PrettyTime> prettyTime;
    @Inject protected LocalyticsSession localyticsSession;
    @Inject LeaderboardServiceWrapper leaderboardServiceWrapper;

    @Override public void onActivityCreated(Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);
        Timber.d("lyl onActivityCreated");
        if (leaderboardMarkUserListAdapter == null)
        {
            leaderboardMarkUserListAdapter = new LeaderboardFriendsListAdapter(
                            getActivity(), getActivity().getLayoutInflater(), R.layout.leaderboard_friends_item_view);
            //leaderboardMarkUserListAdapter.setDTOLoaderCallback(new LeaderboardMarkUserListViewFragmentListLoaderCallback());
            //leaderboardMarkUserListAdapter.setCurrentUserProfileDTO(currentUserProfileDTO);
            //leaderboardMarkUserListAdapter.setFollowRequestedListener(new LeaderboardMarkUserListFollowRequestedListener());
            //leaderboardMarkUserListView.setOnRefreshListener(leaderboardMarkUserListAdapter);
            leaderboardMarkUserListView.setAdapter(leaderboardMarkUserListAdapter);
        }

        //Bundle loaderBundle = new Bundle(getArguments());
        //leaderboardMarkUserLoader = (LeaderboardMarkUserLoader) getActivity().getSupportLoaderManager().initLoader(
        //        leaderboardId, loaderBundle, leaderboardMarkUserListAdapter.getLoaderCallback());
        //leaderboardMarkUserListView.setRefreshing();
        //detachGetFriendsMiddleCallBack();
        //getFriendsMiddleCallback = leaderboardServiceWrapper.getNewFriendsLeaderboard(new FriendsCallback());
    }

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        //View view = inflater.inflate(R.layout.leaderboard_friends_listview, container, false);
        View view = inflater.inflate(R.layout.leaderboard_mark_user_listview, container, false);
        ButterKnife.inject(this, view);
        initViews(view);
        inflateHeaderView(inflater, container);

        if (leaderboardMarkUserListView != null)
        {
            leaderboardMarkUserListView.setEmptyView(inflateEmptyView(inflater, container));
        }
        return view;
    }

    @Override protected void initViews(View view)
    {
        //leaderboardMarkUserListAdapter = new LeaderboardFriendsListAdapter(
        //        getActivity(), getActivity().getLayoutInflater(), R.layout.leaderboard_friends_item_view);
        //leaderboardMarkUserListView.setAdapter(leaderboardMarkUserListAdapter);
    }

    protected void inflateHeaderView(LayoutInflater inflater, ViewGroup container)
    {
        if (leaderboardMarkUserListView != null)
        {
            View headerView = inflater.inflate(R.layout.leaderboard_listview_header, null);
            if (headerView != null)
            {
                leaderboardMarkUserListView.getRefreshableView().addHeaderView(headerView, null, false);
                initHeaderView(headerView);
            }
        }
    }

    protected void initHeaderView(View headerView)
    {
        String leaderboardDefDesc = getArguments().getString(BUNDLE_KEY_LEADERBOARD_DEF_DESC);

        TextView leaderboardMarkUserTimePeriod = (TextView) headerView.findViewById(R.id.leaderboard_time_period);
        if (leaderboardMarkUserTimePeriod != null)
        {
            if (leaderboardDefDesc != null)
            {
                leaderboardMarkUserTimePeriod.setText(leaderboardDefDesc);
                leaderboardMarkUserTimePeriod.setVisibility(View.VISIBLE);
            }
            else
            {
                leaderboardMarkUserTimePeriod.setVisibility(View.GONE);
            }
        }
        leaderboardMarkUserMarkingTime = (TextView) headerView.findViewById(R.id.leaderboard_marking_time);
    }

    @Override public void onResume()
    {
        Timber.d("lyl onResume");
        super.onResume();

        localyticsSession.tagEvent(LocalyticsConstants.FriendsLeaderboard_Filter_FoF);
        detachGetFriendsMiddleCallBack();
        getFriendsMiddleCallback = leaderboardServiceWrapper.getNewFriendsLeaderboard(new FriendsCallback());
    }

    private void detachGetFriendsMiddleCallBack()
    {
        if (getFriendsMiddleCallback != null)
        {
            getFriendsMiddleCallback.setPrimaryCallback(null);
        }
        getFriendsMiddleCallback = null;
    }

    public class FriendsCallback implements Callback<LeaderboardFriendsDTO>
    {
        @Override public void success(LeaderboardFriendsDTO dto, Response response)
        {
            Timber.d("lyl success list.size=%d", dto.leaderboard.users.size());
            //mRelationsList = list;
            //alertDialogUtilLazy.get().dismissProgressDialog();
            Date markingTime = dto.leaderboard.markUtc;
            if (markingTime != null && leaderboardMarkUserMarkingTime != null)
            {
                leaderboardMarkUserMarkingTime.setText(String.format("(%s)", prettyTime.get().format(markingTime)));
            }
            //Timber.d("lyl %d", leaderboardMarkUserScreen.getDisplayedChildLayoutId());
            leaderboardMarkUserScreen.setDisplayedChildByLayoutId(R.id.leaderboard_mark_user_listview);
            leaderboardMarkUserListView.onRefreshComplete();
            List<LeaderboardUserDTO> list = dto.leaderboard.users;
            list.addAll(dto.socialFriends);
            leaderboardMarkUserListAdapter.setItems(list);
            //leaderboardMarkUserListAdapter.setItems(dto.leaderboard.users);
            leaderboardMarkUserListAdapter.notifyDataSetChanged();
        }

        @Override public void failure(RetrofitError retrofitError)
        {
            THToast.show(new THException(retrofitError));
            Timber.d("lyl %s", new THException(retrofitError));
            //alertDialogUtilLazy.get().dismissProgressDialog();
        }
    }

    @Override protected int getMenuResource()
    {
        return R.menu.friend_leaderboard_menu;
    }

    //@Override public boolean onOptionsItemSelected(MenuItem item)
    //{
    //    if (leaderboardMarkUserLoader != null)
    //    {
    //        boolean oldIncludeFoF = leaderboardMarkUserLoader.isIncludeFoF();
    //        switch (item.getItemId())
    //        {
    //            case R.id.friend_leaderboard_menu_show_friends_of_friends:
    //                if (!oldIncludeFoF)
    //                {
    //                    setFriendOfFriendFilter(true);
    //                }
    //                return true;
    //            case R.id.friend_leaderboard_menu_show_friends_only:
    //                if (oldIncludeFoF)
    //                {
    //                    setFriendOfFriendFilter(false);
    //                }
    //                return true;
    //        }
    //    }
    //    return super.onOptionsItemSelected(item);
    //}

    @Override public void onDestroyView()
    {
        detachGetFriendsMiddleCallBack();
        super.onDestroyView();
    }

    //@Override protected PerPagedLeaderboardKey getInitialLeaderboardKey()
    //{
    //    return new FriendsPerPagedLeaderboardKey(leaderboardId, null, null, false);
    //}
    //
    //@Override protected void saveCurrentFilterKey()
    //{
    //    // Do nothing really
    //}

     protected View inflateEmptyView(LayoutInflater inflater, ViewGroup container)
    {
        return inflater.inflate(R.layout.friend_leaderboard_empty_view, container, false);
    }

    //private void setFriendOfFriendFilter(boolean isFoF)
    //{
    //    currentLeaderboardKey = new FriendsPerPagedLeaderboardKey(
    //            currentLeaderboardKey.key,
    //            currentLeaderboardKey.page,
    //            currentLeaderboardKey.perPage,
    //            isFoF);
    //    leaderboardMarkUserLoader.setPagedLeaderboardKey(currentLeaderboardKey);
    //    leaderboardMarkUserLoader.reload();
    //    //invalidateCachedItemView();
    //}
}
