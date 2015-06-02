package com.tradehero.th.fragments.leaderboard;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import butterknife.ButterKnife;
import com.android.internal.util.Predicate;
import com.tradehero.common.persistence.DTOCacheRx;
import com.tradehero.metrics.Analytics;
import com.tradehero.th.R;
import com.tradehero.th.adapters.PagedDTOAdapter;
import com.tradehero.th.api.leaderboard.position.LeaderboardFriendsKey;
import com.tradehero.th.api.social.SocialNetworkEnum;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.fragments.social.friend.FriendsInvitationFragment;
import com.tradehero.th.models.share.SocialShareHelper;
import com.tradehero.th.persistence.leaderboard.position.LeaderboardFriendsCacheRx;
import com.tradehero.th.rx.TimberOnErrorAction;
import com.tradehero.th.rx.ToastAndLogOnErrorAction;
import com.tradehero.th.utils.AdapterViewUtils;
import com.tradehero.th.utils.metrics.AnalyticsConstants;
import com.tradehero.th.utils.metrics.events.SimpleEvent;
import com.tradehero.th.widget.list.SingleExpandingListViewListener;
import javax.inject.Inject;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;

public class FriendLeaderboardMarkUserListFragment extends BaseLeaderboardPagedListRxFragment<
        LeaderboardFriendsKey,
        FriendLeaderboardUserDTO,
        FriendLeaderboardUserDTOList,
        ProcessableLeaderboardFriendsDTO>
{
    @Inject Analytics analytics;
    @Inject SingleExpandingListViewListener singleExpandingListViewListener;
    @Inject LeaderboardFriendsCacheRx leaderboardFriendsCache;
    @Inject LeaderboardMarkUserListFragmentUtil fragmentUtil;
    @Inject SocialShareHelper socialShareHelper;

    @Override public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        fragmentUtil.linkWith(this, LeaderboardType.STOCKS);
    }

    @Override public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.leaderboard_friends_listview, container, false);
        ButterKnife.inject(this, view);

        if (listView != null)
        {
            listView.setEmptyView(inflateEmptyView(inflater, container));
        }
        return view;
    }

    @Override public void onStart()
    {
        super.onStart();
        fragmentUtil.onStart();
        onStopSubscriptions.add(((LeaderboardFriendsSetAdapter) itemViewAdapter).getFollowRequestObservable()
                .subscribe(
                        fragmentUtil,
                        new TimberOnErrorAction("Error on follow requested")));
        onStopSubscriptions.add(((LeaderboardFriendsSetAdapter) itemViewAdapter).getSocialNetworkEnumObservable()
                .flatMap(new Func1<SocialNetworkEnum, Observable<UserProfileDTO>>()
                {
                    @Override public Observable<UserProfileDTO> call(SocialNetworkEnum socialNetworkEnum)
                    {
                        return socialShareHelper.handleNeedToLink(socialNetworkEnum);
                    }
                })
                .doOnError(new ToastAndLogOnErrorAction("Failed to listen to social network"))
                .retry()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        new Action1<UserProfileDTO>()
                        {
                            @Override public void call(UserProfileDTO userProfileDTO)
                            {
                                setCurrentUserProfileDTO(userProfileDTO);
                                requestDtos();
                            }
                        },
                        new ToastAndLogOnErrorAction("Failed to listen to social network")));
        if ((itemViewAdapter != null) && (itemViewAdapter.getCount() == 0))
        {
            requestDtos();
        }
    }

    @Override public void onResume()
    {
        super.onResume();
        analytics.addEvent(new SimpleEvent(AnalyticsConstants.FriendsLeaderboard_Filter_FoF));
    }

    @Override protected int getMenuResource()
    {
        return R.menu.friend_leaderboard_menu;
    }

    @Override public boolean onOptionsItemSelected(@NonNull MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.friend_leaderboard_invite:
                pushInvitationFragment();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override public void onStop()
    {
        onStopSubscriptions.unsubscribe();
        fragmentUtil.onStop();
        super.onStop();
    }

    @Override public void onDestroy()
    {
        itemViewAdapter.clear();
        super.onDestroy();
    }

    @NonNull @Override protected PagedDTOAdapter<FriendLeaderboardUserDTO> createItemViewAdapter()
    {
        return new LeaderboardFriendsSetAdapter(
                getActivity(),
                currentUserId,
                R.layout.lbmu_item_roi_mode,
                R.layout.leaderboard_friends_social_item_view,
                R.layout.leaderboard_friends_call_action);
    }

    @NonNull @Override protected DTOCacheRx<LeaderboardFriendsKey, ProcessableLeaderboardFriendsDTO> getCache()
    {
        return new ProcessableLeaderboardFriendsCache(
                leaderboardFriendsCache,
                userProfileCache,
                currentUserId,
                ((LeaderboardFriendsSetAdapter) itemViewAdapter).createItemFactory());
    }

    protected View inflateEmptyView(@NonNull LayoutInflater inflater, ViewGroup container)
    {
        return inflater.inflate(R.layout.friend_leaderboard_empty_view, container, false);
    }

    private void pushInvitationFragment()
    {
        navigator.get().pushFragment(FriendsInvitationFragment.class);
    }

    @Override protected void setCurrentUserProfileDTO(@NonNull UserProfileDTO currentUserProfileDTO)
    {
        super.setCurrentUserProfileDTO(currentUserProfileDTO);
        if (itemViewAdapter != null)
        {
            ((LeaderboardFriendsSetAdapter) itemViewAdapter).setCurrentUserProfileDTO(currentUserProfileDTO);
            ((LeaderboardFriendsSetAdapter) itemViewAdapter).notifyDataSetChanged();
        }
    }

    @Override public boolean canMakePagedDtoKey()
    {
        return true;
    }

    @NonNull @Override public LeaderboardFriendsKey makePagedDtoKey(int page)
    {
        return new LeaderboardFriendsKey(page);
    }

    @Override protected void updateListViewRow(@NonNull UserProfileDTO currentUserProfile, @NonNull final UserBaseKey heroId)
    {
        AdapterViewUtils.updateSingleRowWhere(
                listView,
                FriendLeaderboardMarkedUserDTO.class,
                new Predicate<FriendLeaderboardMarkedUserDTO>()
                {
                    @Override public boolean apply(FriendLeaderboardMarkedUserDTO friendLeaderboardMarkedUserDTO)
                    {
                        return friendLeaderboardMarkedUserDTO.leaderboardUserDTO.getBaseKey().equals(heroId);
                    }
                });
    }

    @Override public void onItemClick(AdapterView<?> parent, View view, int position, long id)
    {
        super.onItemClick(parent, view, position, id);
        singleExpandingListViewListener.onItemClick(parent, view, position, id);
    }
}