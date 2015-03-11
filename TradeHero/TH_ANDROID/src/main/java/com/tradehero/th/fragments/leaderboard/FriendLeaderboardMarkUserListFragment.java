package com.tradehero.th.fragments.leaderboard;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import butterknife.ButterKnife;
import com.android.internal.util.Predicate;
import com.tradehero.common.persistence.DTOCacheRx;
import com.tradehero.metrics.Analytics;
import com.tradehero.th.R;
import com.tradehero.th.adapters.PagedDTOAdapter;
import com.tradehero.th.api.leaderboard.position.LeaderboardFriendsKey;
import com.tradehero.th.api.users.UserBaseDTO;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.api.users.UserProfileDTOUtil;
import com.tradehero.th.fragments.social.friend.FriendsInvitationFragment;
import com.tradehero.th.models.social.FollowRequest;
import com.tradehero.th.models.user.follow.ChoiceFollowUserAssistantWithDialog;
import com.tradehero.th.persistence.leaderboard.position.LeaderboardFriendsCacheRx;
import com.tradehero.th.rx.TimberOnErrorAction;
import com.tradehero.th.rx.ToastOnErrorAction;
import com.tradehero.th.utils.AdapterViewUtils;
import com.tradehero.th.utils.metrics.AnalyticsConstants;
import com.tradehero.th.utils.metrics.events.ScreenFlowEvent;
import com.tradehero.th.utils.metrics.events.SimpleEvent;
import com.tradehero.th.widget.list.SingleExpandingListViewListener;
import java.util.Date;
import javax.inject.Inject;
import javax.inject.Provider;
import org.ocpsoft.prettytime.PrettyTime;
import rx.android.app.AppObservable;
import rx.functions.Action1;

public class FriendLeaderboardMarkUserListFragment extends BaseLeaderboardPagedListRxFragment<
        LeaderboardFriendsKey,
        FriendLeaderboardUserDTO,
        FriendLeaderboardUserDTOList,
        ProcessableLeaderboardFriendsDTO>
{
    @Inject Analytics analytics;
    @Inject SingleExpandingListViewListener singleExpandingListViewListener;
    @Inject LeaderboardFriendsCacheRx leaderboardFriendsCache;

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
        onStopSubscriptions.add(((LeaderboardFriendsSetAdapter) itemViewAdapter).getFollowRequestObservable()
                .subscribe(
                        new Action1<UserBaseDTO>()
                        {
                            @Override public void call(UserBaseDTO userBaseDTO)
                            {
                                FriendLeaderboardMarkUserListFragment.this.handleFollowRequested(userBaseDTO);
                            }
                        },
                        new TimberOnErrorAction("Error on follow requested")));
        requestDtos();
    }

    @Override public void onResume()
    {
        super.onResume();
        analytics.addEvent(new SimpleEvent(AnalyticsConstants.FriendsLeaderboard_Filter_FoF));
        mProgress.setVisibility(View.VISIBLE);
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
                R.layout.leaderboard_friends_social_item_view);
    }

    @NonNull @Override protected DTOCacheRx<LeaderboardFriendsKey, ProcessableLeaderboardFriendsDTO> getCache()
    {
        return new ProcessableLeaderboardFriendsCache(
                leaderboardFriendsCache,
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

    protected void handleFollowRequested(@NonNull final UserBaseDTO userBaseDTO)
    {
        onStopSubscriptions.add(AppObservable.bindFragment(
                this,
                new ChoiceFollowUserAssistantWithDialog(
                        getActivity(),
                        userBaseDTO
//                        getApplicablePortfolioId()
                ).launchChoiceRx())
                .subscribe(
                        new Action1<Pair<FollowRequest, UserProfileDTO>>()
                        {
                            @Override public void call(Pair<FollowRequest, UserProfileDTO> pair)
                            {
                                FriendLeaderboardMarkUserListFragment.this.setCurrentUserProfileDTO(pair.second);
                                int followType = pair.second.getFollowType(userBaseDTO);
                                if (followType == UserProfileDTOUtil.IS_FREE_FOLLOWER)
                                {
                                    analytics.addEvent(new ScreenFlowEvent(AnalyticsConstants.FreeFollow_Success, AnalyticsConstants.Leaderboard));
                                }
                                else if (followType == UserProfileDTOUtil.IS_PREMIUM_FOLLOWER)
                                {
                                    analytics.addEvent(new ScreenFlowEvent(AnalyticsConstants.PremiumFollow_Success, AnalyticsConstants.Leaderboard));
                                }
                                FriendLeaderboardMarkUserListFragment.this.updateListViewRow(userBaseDTO.getBaseKey());
                            }
                        },
                        new ToastOnErrorAction()
                ));
    }

    private void updateListViewRow(final UserBaseKey heroId)
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