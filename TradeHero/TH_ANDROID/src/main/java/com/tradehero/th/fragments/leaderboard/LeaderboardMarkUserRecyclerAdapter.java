package com.tradehero.th.fragments.leaderboard;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.InjectView;
import butterknife.Optional;
import com.squareup.picasso.Picasso;
import com.tradehero.th.R;
import com.tradehero.th.adapters.PagedRecyclerAdapter;
import com.tradehero.th.api.leaderboard.key.FriendsPerPagedLeaderboardKey;
import com.tradehero.th.api.leaderboard.key.LeaderboardKey;
import com.tradehero.th.api.portfolio.OwnedPortfolioId;
import com.tradehero.th.fragments.timeline.UserStatisticView;
import com.tradehero.th.inject.HierarchyInjector;
import com.tradehero.th.utils.GraphicUtil;
import javax.inject.Inject;
import rx.Observable;
import rx.subjects.PublishSubject;

public class LeaderboardMarkUserRecyclerAdapter extends PagedRecyclerAdapter<
        LeaderboardMarkUserItemView.DTO>
{
    private static final int VIEW_TYPE_MAIN = 0;
    private static final int VIEW_TYPE_OWN = 1;

    @LayoutRes protected final int ownRankingRes;
    private final int itemLayoutRes;
    @NonNull protected final LeaderboardKey leaderboardKey;
    @Nullable protected OwnedPortfolioId applicablePortfolioId;

    @Inject Picasso picasso;

    @NonNull protected final PublishSubject<LeaderboardMarkUserItemView.UserAction> followRequestedPublish;

    //<editor-fold desc="Constructors">
    public LeaderboardMarkUserRecyclerAdapter(
            Context context,
            @LayoutRes int itemLayoutRes,
            @LayoutRes int ownRankingRes,
            @NonNull LeaderboardKey leaderboardKey)
    {
        super(LeaderboardMarkUserItemView.DTO.class, new LeaderboardMarkUserItemComparator());
        this.itemLayoutRes = itemLayoutRes;
        this.leaderboardKey = leaderboardKey;
        this.ownRankingRes = ownRankingRes;
        this.followRequestedPublish = PublishSubject.create();
        setOnItemClickedListener(new OnItemClickedListener<LeaderboardMarkUserItemView.DTO>()
        {
            @Override public void onItemClicked(int position, TypedViewHolder<LeaderboardMarkUserItemView.DTO> viewHolder,
                    LeaderboardMarkUserItemView.DTO object)
            {
                if (viewHolder instanceof LbmuItemViewHolder && object.userStatisticsDto != null)
                {
                    LbmuItemViewHolder lbmuItemViewHolder = (LbmuItemViewHolder) viewHolder;
                    if (lbmuItemViewHolder.userStatisticView != null)
                    {
                        boolean expand = !object.isExpanded();
                        lbmuItemViewHolder.expandingLayout.expand(expand);
                        object.setExpanded(expand);
                        if (expand)
                        {
                            lbmuItemViewHolder.userStatisticView.display(object.userStatisticsDto);
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

    @NonNull public Observable<LeaderboardMarkUserItemView.UserAction> getFollowRequestedObservable()
    {
        return followRequestedPublish.asObservable();
    }

    @Override public int getItemViewType(int position)
    {
        return getItem(position).isMyOwnRanking() ? VIEW_TYPE_OWN : VIEW_TYPE_MAIN;
    }

    @Override public int getItemCount()
    {
        return super.getItemCount();
    }

    @Override public LeaderboardMarkUserItemView.DTO getItem(int position)
    {
        LeaderboardMarkUserItemView.DTO item = super.getItem(position);
        if (item.isMyOwnRanking())
        {
            return item;
        }
        else
        {
            item.setRanking(position);
            if (item.leaderboardUserDTO != null)
            {
                item.leaderboardUserDTO.setLeaderboardId(leaderboardKey.id);
                boolean includeFoF = leaderboardKey instanceof FriendsPerPagedLeaderboardKey &&
                        ((FriendsPerPagedLeaderboardKey) leaderboardKey).includeFoF != null &&
                        ((FriendsPerPagedLeaderboardKey) leaderboardKey).includeFoF;
                item.leaderboardUserDTO.setIncludeFoF(includeFoF);
            }
            return item;
        }
    }

    @NonNull @Override
    public TypedViewHolder<LeaderboardMarkUserItemView.DTO> onCreateTypedViewHolder(ViewGroup parent, int viewType)
    {
        //view.getFollowRequestedObservable().subscribe(followRequestedPublish);
        switch (viewType)
        {
            case VIEW_TYPE_OWN:
                return new LbmuHeaderViewHolder(LayoutInflater.from(parent.getContext()).inflate(ownRankingRes, parent, false), picasso);
            case VIEW_TYPE_MAIN:
                return new LbmuItemViewHolder(LayoutInflater.from(parent.getContext()).inflate(itemLayoutRes, parent, false), picasso);
            default:
                throw new IllegalArgumentException("Unhandled viewType " + viewType);
        }
    }

    @Override public void onBindViewHolder(TypedViewHolder<LeaderboardMarkUserItemView.DTO> holder, int position)
    {
        super.onBindViewHolder(holder, position);
        if (position > 0 && holder instanceof LbmuItemViewHolder)
        {
            //We don't set the background for the first item since it's the header with blue background.
            position--;
            GraphicUtil.setEvenOddBackground(position, holder.itemView);
        }
    }

    private static class LeaderboardMarkUserItemComparator extends TypedRecyclerComparator<LeaderboardMarkUserItemView.DTO>
    {
        @Override protected int compare(LeaderboardMarkUserItemView.DTO o1, LeaderboardMarkUserItemView.DTO o2)
        {
            if (o1.isMyOwnRanking() && !o2.isMyOwnRanking())
            {
                return -1;
            }
            else if (!o1.isMyOwnRanking() && o2.isMyOwnRanking())
            {
                return 1;
            }
            else if (o1.ranking > 0 && o2.ranking > 0)
            {
                return o1.ranking - o2.ranking;
            }
            return super.compare(o1, o2);
        }

        @Override protected boolean areContentsTheSame(LeaderboardMarkUserItemView.DTO oldItem, LeaderboardMarkUserItemView.DTO newItem)
        {
            if (!oldItem.lbmuDisplayName.equals(newItem.lbmuDisplayName)) return false;
            if (!oldItem.lbmuRoi.equals(newItem.lbmuRoi)) return false;
            return oldItem.lbmuRanking.equals(newItem.lbmuRanking) && !(oldItem.lbmuDisplayPicture != null ? !oldItem.lbmuDisplayPicture.equals(
                    newItem.lbmuDisplayPicture) : newItem.lbmuDisplayPicture != null);
        }

        @Override protected boolean areItemsTheSame(LeaderboardMarkUserItemView.DTO item1, LeaderboardMarkUserItemView.DTO item2)
        {
            if (item1.isMyOwnRanking() && item2.isMyOwnRanking())
            {
                return true; //There can only be 1 header.
            }
            else
            {
                return !(item1.leaderboardUserDTO != null && item2.leaderboardUserDTO != null)
                        || item1.leaderboardUserDTO.id == item2.leaderboardUserDTO.id;
            }
        }
    }

    public static class LbmuHeaderViewHolder extends LbmuItemViewHolder
    {
        @InjectView(R.id.mark_expand_down) @Optional @Nullable ImageView expandMark;

        public LbmuHeaderViewHolder(View itemView, Picasso picasso)
        {
            super(itemView, picasso);
        }

        @Override public void display(LeaderboardMarkUserItemView.DTO dto)
        {
            super.display(dto);
            if (expandMark != null)
            {
                if (dto.userStatisticsDto == null)
                {
                    expandMark.setVisibility(View.GONE);
                }
                else
                {
                    expandMark.setVisibility(View.VISIBLE);
                }
            }
        }
    }

    public static class LbmuItemViewHolder extends TypedViewHolder<LeaderboardMarkUserItemView.DTO>
    {
        private final Picasso picasso;

        @InjectView(R.id.leaderboard_user_item_display_name) protected TextView lbmuDisplayName;
        @InjectView(R.id.lbmu_roi) protected TextView lbmuRoi;
        @InjectView(R.id.leaderboard_user_item_profile_picture) ImageView lbmuProfilePicture;
        @InjectView(R.id.leaderboard_user_item_position) TextView lbmuPosition;

        @InjectView(R.id.expanding_layout) ExpandingLayout expandingLayout;
        @InjectView(R.id.user_statistic_view) @Optional @Nullable UserStatisticView userStatisticView;

        public LbmuItemViewHolder(View itemView, Picasso picasso)
        {
            super(itemView);
            this.picasso = picasso;
        }

        @Override public void display(LeaderboardMarkUserItemView.DTO dto)
        {
            lbmuDisplayName.setText(dto.lbmuDisplayName);
            lbmuRoi.setText(dto.lbmuRoi);
            lbmuPosition.setText(dto.lbmuRanking);
            lbmuPosition.setTextColor(dto.lbmuPositionColor);
            if (dto.lbmuDisplayPicture != null)
            {
                picasso.load(dto.lbmuDisplayPicture)
                        .fit()
                        .centerCrop()
                        .placeholder(R.drawable.superman_facebook)
                        .error(R.drawable.superman_facebook)
                        .into(
                                lbmuProfilePicture);
            }
            else
            {
                picasso.load(R.drawable.superman_facebook).into(lbmuProfilePicture);
            }

            expandingLayout.expandWithNoAnimation(dto.isExpanded());
            if (userStatisticView != null)
            {
                if (dto.isExpanded())
                {
                    userStatisticView.display(dto.userStatisticsDto);
                }
                else
                {
                    userStatisticView.display(null);
                }
            }
        }
    }
}
