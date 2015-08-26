package com.tradehero.th.fragments.leaderboard;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.Bind;
import butterknife.OnClick;
import com.squareup.picasso.Picasso;
import com.tradehero.metrics.Analytics;
import com.tradehero.metrics.AnalyticsEvent;
import com.tradehero.th.R;
import com.tradehero.th.adapters.PagedRecyclerAdapter;
import com.tradehero.th.api.leaderboard.key.FriendsPerPagedLeaderboardKey;
import com.tradehero.th.api.leaderboard.key.LeaderboardKey;
import com.tradehero.th.api.portfolio.OwnedPortfolioId;
import com.tradehero.th.fragments.leaderboard.LeaderboardItemUserAction.UserActionType;
import com.tradehero.th.fragments.timeline.UserStatisticView;
import com.tradehero.th.inject.HierarchyInjector;
import com.tradehero.th.utils.GraphicUtil;
import com.tradehero.th.utils.metrics.AnalyticsConstants;
import com.tradehero.th.utils.metrics.events.SimpleEvent;
import com.tradehero.th.widget.MarkdownTextView;
import javax.inject.Inject;
import rx.Observable;
import rx.subjects.PublishSubject;

public class LeaderboardMarkUserRecyclerAdapter<T extends LeaderboardItemDisplayDTO> extends PagedRecyclerAdapter<
        T>
{
    private static final int VIEW_TYPE_MAIN = 0;
    private static final int VIEW_TYPE_OWN = 1;

    @LayoutRes protected final int ownRankingRes;
    protected final Context context;
    protected final int itemLayoutRes;
    @NonNull protected final LeaderboardKey leaderboardKey;
    @Nullable protected OwnedPortfolioId applicablePortfolioId;

    @Inject Picasso picasso;
    @Inject Analytics analytics;

    @NonNull protected final PublishSubject<LeaderboardItemUserAction> userActionPublishSubject;

    //<editor-fold desc="Constructors">
    public LeaderboardMarkUserRecyclerAdapter(
            Class<T> klass,
            Context context,
            @LayoutRes int itemLayoutRes,
            @LayoutRes int ownRankingRes,
            @NonNull LeaderboardKey leaderboardKey)
    {
        this(klass, context, new LeaderboardMarkUserItemComparator<T>(), itemLayoutRes, ownRankingRes, leaderboardKey);
    }

    public LeaderboardMarkUserRecyclerAdapter(
            Class<T> klass,
            Context context,
            @NonNull TypedRecyclerComparator<T> itemComparator,
            @LayoutRes int itemLayoutRes,
            @LayoutRes int ownRankingRes,
            @NonNull LeaderboardKey leaderboardKey)
    {
        super(klass, itemComparator);
        this.context = context;
        this.itemLayoutRes = itemLayoutRes;
        this.leaderboardKey = leaderboardKey;
        this.ownRankingRes = ownRankingRes;
        this.userActionPublishSubject = PublishSubject.create();
        setOnItemClickedListener(new OnItemClickedListener<T>()
        {
            @Override public void onItemClicked(int position, TypedViewHolder<T> viewHolder,
                    T object)
            {
                if (viewHolder instanceof LbmuItemViewHolder
                        && object instanceof LeaderboardMarkedUserItemDisplayDto
                        && ((LeaderboardMarkedUserItemDisplayDto) object).userStatisticsDto != null)
                {
                    LbmuItemViewHolder lbmuItemViewHolder = (LbmuItemViewHolder) viewHolder;
                    LeaderboardMarkedUserItemDisplayDto dto = (LeaderboardMarkedUserItemDisplayDto) object;
                    if (lbmuItemViewHolder.userStatisticView != null)
                    {
                        boolean expand = !dto.isExpanded();
                        lbmuItemViewHolder.expandingLayout.expand(expand);
                        dto.setExpanded(expand);
                        if (expand)
                        {
                            lbmuItemViewHolder.userStatisticView.display(dto.userStatisticsDto);
                        }
                        else
                        {
                            lbmuItemViewHolder.userStatisticView.display(null);
                        }
                    }
                }
            }
        });
        HierarchyInjector.inject(context, this);
    }
    //</editor-fold>

    public void setApplicablePortfolioId(@Nullable OwnedPortfolioId ownedPortfolioId)
    {
        this.applicablePortfolioId = ownedPortfolioId;
    }

    @NonNull public Observable<LeaderboardItemUserAction> getUserActionObservable()
    {
        return userActionPublishSubject.asObservable();
    }

    @Override public int getItemViewType(int position)
    {
        LeaderboardItemDisplayDTO dto = getItem(position);
        return getViewTypeForItem(dto);
    }

    protected int getViewTypeForItem(LeaderboardItemDisplayDTO dto)
    {
        return dto instanceof LeaderboardMarkedUserItemDisplayDto && ((LeaderboardMarkedUserItemDisplayDto) dto).isMyOwnRanking() ? VIEW_TYPE_OWN
                : VIEW_TYPE_MAIN;
    }

    @Override public int getItemCount()
    {
        return super.getItemCount();
    }

    @Override public T getItem(int position)
    {
        T item = super.getItem(position);
        if (item instanceof LeaderboardMarkedUserItemDisplayDto)
        {
            LeaderboardMarkedUserItemDisplayDto markedUserDto = (LeaderboardMarkedUserItemDisplayDto) item;
            if (!markedUserDto.isMyOwnRanking())
            {
                if (markedUserDto.leaderboardUserDTO != null)
                {
                    markedUserDto.leaderboardUserDTO.setLeaderboardId(leaderboardKey.id);
                    boolean includeFoF = leaderboardKey instanceof FriendsPerPagedLeaderboardKey &&
                            ((FriendsPerPagedLeaderboardKey) leaderboardKey).includeFoF != null &&
                            ((FriendsPerPagedLeaderboardKey) leaderboardKey).includeFoF;
                    markedUserDto.leaderboardUserDTO.setIncludeFoF(includeFoF);
                }
            }
        }
        return item;
    }

    @NonNull @Override
    public TypedViewHolder<T> onCreateViewHolder(ViewGroup parent, int viewType)
    {
        LbmuItemViewHolder<T> lbmuItemViewHolder;
        switch (viewType)
        {
            case VIEW_TYPE_OWN:
                lbmuItemViewHolder =
                        createOwnRankingLbmuViewHolder(parent);
                break;
            case VIEW_TYPE_MAIN:
                lbmuItemViewHolder =
                        createLbmuItemViewholder(parent);
                break;
            default:
                throw new IllegalArgumentException("Unhandled viewType " + viewType);
        }
        lbmuItemViewHolder.getUserActionObservable().subscribe(userActionPublishSubject);
        return lbmuItemViewHolder;
    }

    @NonNull protected LbmuHeaderViewHolder<T> createOwnRankingLbmuViewHolder(ViewGroup parent)
    {
        return new LbmuHeaderViewHolder<>(LayoutInflater.from(parent.getContext()).inflate(ownRankingRes, parent, false), picasso, analytics);
    }

    @NonNull protected LbmuItemViewHolder<T> createLbmuItemViewholder(ViewGroup parent)
    {
        return new LbmuItemViewHolder<>(LayoutInflater.from(parent.getContext()).inflate(itemLayoutRes, parent, false), picasso, analytics);
    }

    @Override public void onBindViewHolder(TypedViewHolder<T> holder, int position)
    {
        super.onBindViewHolder(holder, position);
        if (position > 0 && holder instanceof LbmuItemViewHolder)
        {
            //We don't set the background for the first item since it's the header with blue background.
            position--;
            GraphicUtil.setEvenOddBackground(position, holder.itemView);
        }
    }

    private static class LeaderboardMarkUserItemComparator<T extends LeaderboardItemDisplayDTO> extends TypedRecyclerComparator<T>
    {
        @Override public int compare(T o1, T o2)
        {
            if (o1 instanceof LeaderboardMarkedUserItemDisplayDto && o2 instanceof LeaderboardMarkedUserItemDisplayDto)
            {
                LeaderboardMarkedUserItemDisplayDto dto1 = (LeaderboardMarkedUserItemDisplayDto) o1;
                LeaderboardMarkedUserItemDisplayDto dto2 = (LeaderboardMarkedUserItemDisplayDto) o2;
                if (dto1.isMyOwnRanking() && !dto2.isMyOwnRanking())
                {
                    return -1;
                }
                else if (!dto1.isMyOwnRanking() && dto2.isMyOwnRanking())
                {
                    return 1;
                }
                else if (dto1.ranking > 0 && dto2.ranking > 0)
                {
                    return dto1.ranking - dto2.ranking;
                }
            }
            return super.compare(o1, o2);
        }

        @Override public boolean areContentsTheSame(T oldItem, T newItem)
        {
            if (oldItem instanceof LeaderboardMarkedUserItemDisplayDto && newItem instanceof LeaderboardMarkedUserItemDisplayDto)
            {
                LeaderboardMarkedUserItemDisplayDto oldDto = (LeaderboardMarkedUserItemDisplayDto) oldItem;
                LeaderboardMarkedUserItemDisplayDto newDto = (LeaderboardMarkedUserItemDisplayDto) newItem;
                if (!oldDto.lbmuDisplayName.equals(newDto.lbmuDisplayName)) return false;
                if (!oldDto.lbmuRoi.equals(newDto.lbmuRoi)) return false;
                if (oldDto.lbmuRoiPeriodVisibility != newDto.lbmuRoiPeriodVisibility) return false;
                if (oldDto.lbmuRoiPeriod == null && newDto.lbmuRoiPeriod != null) return false;
                if (oldDto.lbmuRoiPeriod != null && newDto.lbmuRoiPeriod == null) return false;
                if (oldDto.lbmuRoiPeriod != null && !oldDto.lbmuRoiPeriod.equals(newDto.lbmuRoiPeriod)) return false;
                if (oldDto.isFollowing() != (newDto.isFollowing())) return false;
                return oldDto.lbmuRanking.equals(newDto.lbmuRanking)
                        && !(oldDto.lbmuDisplayPicture != null
                        ? !oldDto.lbmuDisplayPicture.equals(newDto.lbmuDisplayPicture)
                        : newDto.lbmuDisplayPicture != null);
            }
            return super.areContentsTheSame(oldItem, newItem);
        }

        @Override public boolean areItemsTheSame(T item1, T item2)
        {
            if (item1 instanceof LeaderboardMarkedUserItemDisplayDto && item2 instanceof LeaderboardMarkedUserItemDisplayDto)
            {
                LeaderboardMarkedUserItemDisplayDto dto1 = (LeaderboardMarkedUserItemDisplayDto) item1;
                LeaderboardMarkedUserItemDisplayDto dto2 = (LeaderboardMarkedUserItemDisplayDto) item2;
                if (dto1.isMyOwnRanking() && dto2.isMyOwnRanking())
                {
                    return true; //There can only be 1 header.
                }
                else
                {
                    return !(dto1.leaderboardUserDTO != null && dto2.leaderboardUserDTO != null)
                            || dto1.leaderboardUserDTO.id == dto2.leaderboardUserDTO.id;
                }
            }
            return super.areItemsTheSame(item1, item2);
        }
    }

    public static class LbmuHeaderViewHolder<T extends LeaderboardItemDisplayDTO> extends LbmuItemViewHolder<T>
    {
        public LbmuHeaderViewHolder(View itemView, Picasso picasso, Analytics analytics)
        {
            super(itemView, picasso, analytics);
        }

        @Override public void display(T dto)
        {
            super.display(dto);
        }
    }

    public static class LbmuItemViewHolder<T extends LeaderboardItemDisplayDTO> extends TypedViewHolder<T>
    {
        private final Analytics analytics;
        private final Picasso picasso;
        protected final PublishSubject<LeaderboardItemUserAction> userActionSubject;

        @Bind(R.id.leaderboard_user_item_display_name) protected TextView lbmuDisplayName;
        @Bind(R.id.lbmu_roi) protected TextView lbmuRoi;
        @Bind(R.id.lbmu_roi_period) protected TextView lbmurRoiPeriod;
        @Bind(R.id.leaderboard_user_item_profile_picture) ImageView lbmuProfilePicture;
        @Bind(R.id.leaderboard_user_item_position) TextView lbmuPosition;

        @Bind(R.id.expanding_layout) ExpandingLayout expandingLayout;
        @Bind(R.id.user_statistic_view) @Nullable UserStatisticView userStatisticView;
        @Bind(R.id.leaderboard_user_item_fof) @Nullable MarkdownTextView lbmuFoF;
        @Bind(R.id.leaderboard_user_item_follow) ImageView lbmuFollowUser;
        @Nullable protected LeaderboardMarkedUserItemDisplayDto currentDto;

        public LbmuItemViewHolder(View itemView, Picasso picasso, Analytics analytics)
        {
            super(itemView);
            this.picasso = picasso;
            userActionSubject = PublishSubject.create();
            this.analytics = analytics;
        }

        @Override public void display(T dto)
        {
            if (dto instanceof LeaderboardMarkedUserItemDisplayDto)
            {
                this.currentDto = (LeaderboardMarkedUserItemDisplayDto) dto;
                lbmuDisplayName.setText(this.currentDto.lbmuDisplayName);
                lbmuRoi.setText(this.currentDto.lbmuRoi);
                lbmurRoiPeriod.setVisibility(this.currentDto.lbmuRoiPeriodVisibility);
                lbmurRoiPeriod.setText(this.currentDto.lbmuRoiPeriod);
                lbmuPosition.setText(this.currentDto.lbmuRanking);
                lbmuPosition.setTextColor(this.currentDto.lbmuPositionColor);
                if (this.currentDto.lbmuDisplayPicture != null)
                {
                    picasso.load(this.currentDto.lbmuDisplayPicture)
                            .fit()
                            .centerCrop()
                            .placeholder(R.drawable.superman_facebook)
                            .error(R.drawable.superman_facebook)
                            .into(lbmuProfilePicture);
                }
                else
                {
                    picasso.load(R.drawable.superman_facebook).into(lbmuProfilePicture);
                }

                expandingLayout.expandWithNoAnimation(this.currentDto.isExpanded());
                if (userStatisticView != null)
                {
                    if (this.currentDto.isExpanded())
                    {
                        userStatisticView.display(this.currentDto.userStatisticsDto);
                    }
                    else
                    {
                        userStatisticView.display(null);
                    }
                }

                if (lbmuFoF != null)
                {
                    lbmuFoF.setText(this.currentDto.lbmuFoF);
                    lbmuFoF.setVisibility(this.currentDto.lbmuFoFVisibility);
                    lbmuFoF.setMovementMethod(LinkMovementMethod.getInstance());
                }

                if (lbmuFollowUser != null)
                {
                    if (this.currentDto.leaderboardUserDTO == null || this.currentDto.currentUserId.toUserBaseKey()
                            .equals(this.currentDto.leaderboardUserDTO.getBaseKey()))
                    {
                        lbmuFollowUser.setVisibility(View.GONE);
                    }
                    else
                    {
                        lbmuFollowUser.setVisibility(View.VISIBLE);
                        lbmuFollowUser.setBackgroundResource(this.currentDto.lbmuFollowBtnColorResId);
                        lbmuFollowUser.setImageResource(this.currentDto.lbmuFollowIconResId);
                    }
                }

            }
        }

        @SuppressWarnings("UnusedDeclaration")
        @OnClick({R.id.leaderboard_user_item_open_profile, R.id.leaderboard_user_item_profile_picture})
        protected void handleProfileClicked(View view)
        {
            if (this.currentDto != null)
            {
                analytics.addEvent(new SimpleEvent(AnalyticsConstants.Leaderboard_Profile));
                userActionSubject.onNext(new LeaderboardItemUserAction(this.currentDto, UserActionType.PROFILE));
            }
        }

        @SuppressWarnings("UnusedDeclaration")
        @OnClick(R.id.leaderboard_user_item_open_positions_list)
        protected void handlePositionButtonClicked(View view)
        {
            if (this.currentDto != null)
            {
                analytics.addEvent(new SimpleEvent(AnalyticsConstants.Leaderboard_Positions));
                userActionSubject.onNext(new LeaderboardItemUserAction(this.currentDto, UserActionType.POSITIONS));
            }
        }

        @SuppressWarnings("UnusedDeclaration")
        @OnClick(R.id.leaderboard_user_item_follow)
        protected void handleFollowButtonClicked(View view)
        {
            if (this.currentDto != null)
            {
                AnalyticsEvent event;
                LeaderboardItemUserAction userAction;

                if (this.currentDto.isFollowing())
                {
                    event = new SimpleEvent(AnalyticsConstants.Leaderboard_Unfollow);
                    userAction = new LeaderboardItemUserAction(this.currentDto, UserActionType.UNFOLLOW);
                }
                else
                {
                    event = new SimpleEvent(AnalyticsConstants.Leaderboard_Follow);
                    userAction = new LeaderboardItemUserAction(this.currentDto, UserActionType.FOLLOW);
                }
                analytics.addEvent(event);
                userActionSubject.onNext(userAction);
            }
        }

        @NonNull public Observable<LeaderboardItemUserAction> getUserActionObservable()
        {
            return userActionSubject.asObservable();
        }
    }
}
