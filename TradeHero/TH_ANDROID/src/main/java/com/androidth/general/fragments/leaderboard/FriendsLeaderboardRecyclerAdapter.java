package com.androidth.general.fragments.leaderboard;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.Bind;
import butterknife.OnClick;
import com.squareup.picasso.Picasso;
import com.androidth.general.R;
import com.androidth.general.api.leaderboard.key.LeaderboardKey;
import com.androidth.general.api.social.SocialNetworkEnum;
import com.androidth.general.api.social.UserFriendsDTO;
import com.androidth.general.fragments.authentication.AuthenticationImageButton;
import com.androidth.general.utils.GraphicUtil;
import rx.Observable;
import rx.subjects.PublishSubject;

public class FriendsLeaderboardRecyclerAdapter extends LeaderboardMarkUserRecyclerAdapter<LeaderboardItemDisplayDTO>
{
    public static final int VIEW_TYPE_SOCIAL = 2;
    public static final int VIEW_TYPE_CALL_TO_ACTION = 3;

    private final int leaderboard_friends_social_item_view;
    private final int leaderboard_friends_call_action;
    private final PublishSubject<LeaderboardFriendUserAction> inviteRequestedSubject;
    private final PublishSubject<SocialNetworkEnum> socialNetworkEnumSubject;

    public FriendsLeaderboardRecyclerAdapter(Context context, int lbmu_item_roi_mode,
            int leaderboard_friends_social_item_view, int leaderboard_friends_call_action, LeaderboardKey leaderboardKey)
    {
        super(LeaderboardItemDisplayDTO.class, context, new FriendLeaderboardItemComparator(), lbmu_item_roi_mode, lbmu_item_roi_mode,
                leaderboardKey);
        this.leaderboard_friends_social_item_view = leaderboard_friends_social_item_view;
        this.leaderboard_friends_call_action = leaderboard_friends_call_action;
        this.inviteRequestedSubject = PublishSubject.create();
        this.socialNetworkEnumSubject = PublishSubject.create();
    }

    @Override protected int getViewTypeForItem(LeaderboardItemDisplayDTO dto)
    {
        if (dto instanceof FriendLeaderboardItemDisplayDTO.Social) return VIEW_TYPE_SOCIAL;
        if (dto instanceof FriendLeaderboardItemDisplayDTO.CallToAction) return VIEW_TYPE_CALL_TO_ACTION;
        return super.getViewTypeForItem(dto);
    }

    @NonNull @Override public TypedViewHolder<LeaderboardItemDisplayDTO> onCreateViewHolder(ViewGroup parent, int viewType)
    {
        switch (viewType)
        {
            case VIEW_TYPE_SOCIAL:
                SocialFriendViewHolder lbmuItemViewHolder =
                        new SocialFriendViewHolder(
                                LayoutInflater.from(parent.getContext()).inflate(leaderboard_friends_social_item_view, parent, false), picasso);
                lbmuItemViewHolder.getFriendUserActionObservable().subscribe(inviteRequestedSubject);
                return lbmuItemViewHolder;
            case VIEW_TYPE_CALL_TO_ACTION:
                CallToActionViewHolder callToActionItemViewHolder =
                        new CallToActionViewHolder(LayoutInflater.from(parent.getContext()).inflate(leaderboard_friends_call_action, parent, false));
                callToActionItemViewHolder.getSocialNetworkEnumObservable().subscribe(socialNetworkEnumSubject);
                return callToActionItemViewHolder;
        }
        return super.onCreateViewHolder(parent, viewType);
    }

    @Override public void onBindViewHolder(TypedViewHolder<LeaderboardItemDisplayDTO> holder, int position)
    {
        super.onBindViewHolder(holder, position);
        GraphicUtil.setEvenOddBackground(position, holder.itemView);
    }

    public Observable<LeaderboardFriendUserAction> getInviteRequestedObservable()
    {
        return inviteRequestedSubject.asObservable();
    }

    public Observable<SocialNetworkEnum> getSocialNetworkEnumObservable()
    {
        return socialNetworkEnumSubject.asObservable();
    }

    private static class FriendLeaderboardItemComparator extends TypedRecyclerComparator<LeaderboardItemDisplayDTO>
    {
        @Override public int compare(LeaderboardItemDisplayDTO o1, LeaderboardItemDisplayDTO o2)
        {
            if (o1 instanceof LeaderboardMarkedUserItemDisplayDto &&
                    o2 instanceof FriendLeaderboardItemDisplayDTO.Social)
            {
                return -1;
            }

            if (o1 instanceof FriendLeaderboardItemDisplayDTO.Social &&
                    o2 instanceof LeaderboardMarkedUserItemDisplayDto)
            {
                return 1;
            }

            if (o1 instanceof FriendLeaderboardItemDisplayDTO.Social &&
                    o2 instanceof FriendLeaderboardItemDisplayDTO.Social)
            {
                UserFriendsDTO lhu = ((FriendLeaderboardItemDisplayDTO.Social) o1).userFriendsDTO;
                UserFriendsDTO rhu = ((FriendLeaderboardItemDisplayDTO.Social) o2).userFriendsDTO;
                if (lhu.equals(rhu))
                {
                    return 0;
                }

                return lhu.compareTo(rhu);
            }

            if (o1 instanceof FriendLeaderboardItemDisplayDTO.CallToAction
                    && o2 instanceof FriendLeaderboardItemDisplayDTO.CallToAction)
            {
                return 0;
            }
            if (o1 instanceof FriendLeaderboardItemDisplayDTO.CallToAction)
            {
                return 1;
            }
            if (o2 instanceof FriendLeaderboardItemDisplayDTO.CallToAction)
            {
                return -1;
            }

            if (o1 instanceof LeaderboardMarkedUserItemDisplayDto &&
                    o2 instanceof LeaderboardMarkedUserItemDisplayDto)
            {
                return ((LeaderboardMarkedUserItemDisplayDto) o1).ranking
                        - ((LeaderboardMarkedUserItemDisplayDto) o2).ranking;
            }

            throw new IllegalArgumentException("Unhandled " + o1.getClass() + " with " + o2.getClass());
        }
    }

    public static class CallToActionViewHolder extends TypedViewHolder<LeaderboardItemDisplayDTO>
    {
        private final PublishSubject<SocialNetworkEnum> socialNetworkEnumPublishSubject;

        public CallToActionViewHolder(View itemView)
        {
            super(itemView);
            socialNetworkEnumPublishSubject = PublishSubject.create();
        }

        @Override public void onDisplay(LeaderboardItemDisplayDTO friendLeaderboardUserDTO)
        {

        }

        @SuppressWarnings({"unused"}) @OnClick({
//                R.id.btn_linkedin_signin,
                R.id.btn_facebook_signin,
//                R.id.btn_twitter_signin,
                R.id.btn_qq_signin,
                R.id.btn_weibo_signin,
        }) @Nullable
        protected void onSignInButtonClicked(View view)
        {
            socialNetworkEnumPublishSubject.onNext(((AuthenticationImageButton) view).getType());
        }

        public Observable<SocialNetworkEnum> getSocialNetworkEnumObservable()
        {
            return socialNetworkEnumPublishSubject.asObservable();
        }
    }

    public static class SocialFriendViewHolder extends TypedViewHolder<LeaderboardItemDisplayDTO>
    {
        @Bind(R.id.leaderboard_user_item_network_label) ImageView networkLabel;
        @Bind(R.id.leaderboard_user_item_profile_picture) ImageView avatar;
        @Bind(R.id.leaderboard_user_item_social_name) TextView socialName;

        private final PublishSubject<LeaderboardFriendUserAction> friendUserActionPublishSubject;
        private final Picasso picasso;
        private UserFriendsDTO userFriendsDTO;

        public SocialFriendViewHolder(View itemView, Picasso picasso)
        {
            super(itemView);
            this.picasso = picasso;
            friendUserActionPublishSubject = PublishSubject.create();
        }

        @Override public void onDisplay(LeaderboardItemDisplayDTO friendLeaderboardUserDTO)
        {
            if (friendLeaderboardUserDTO instanceof FriendLeaderboardItemDisplayDTO.Social)
            {
                userFriendsDTO = ((FriendLeaderboardItemDisplayDTO.Social) friendLeaderboardUserDTO).userFriendsDTO;

                if (networkLabel != null)
                {
                    networkLabel.setBackgroundResource(userFriendsDTO.getNetworkLabelImage());
                }

                if (avatar != null)
                {
                    String url = userFriendsDTO.getProfilePictureURL();
                    if (url != null)
                    {
                        picasso.load(url)
                                .placeholder(R.drawable.superman_facebook)
                                .into(avatar);
                    }
                }

                if (socialName != null)
                {
                    socialName.setText(userFriendsDTO.name);
                }
            }
        }

        @SuppressWarnings("unused")
        @OnClick(R.id.leaderboard_user_item_invite_btn) void invite(View view)
        {
            if (userFriendsDTO != null)
            {
                friendUserActionPublishSubject.onNext(new LeaderboardFriendUserAction(userFriendsDTO));
            }
        }

        public Observable<LeaderboardFriendUserAction> getFriendUserActionObservable()
        {
            return friendUserActionPublishSubject.asObservable();
        }
    }
}
