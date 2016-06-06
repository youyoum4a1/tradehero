package com.androidth.general.fragments.position;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.squareup.picasso.Picasso;
import com.androidth.general.adapters.TypedRecyclerAdapter;
import com.androidth.general.api.position.PositionStatus;
import com.androidth.general.api.users.CurrentUserId;
import com.androidth.general.api.users.UserProfileDTO;
import com.androidth.general.fragments.position.partial.PositionPartialTopView;
import com.androidth.general.fragments.position.view.PositionLockedView;
import com.androidth.general.fragments.position.view.PositionNothingView;
import com.androidth.general.inject.HierarchyInjector;
import com.androidth.general.utils.GraphicUtil;
import java.util.Collection;
import java.util.Comparator;
import java.util.Map;
import javax.inject.Inject;
import rx.Observable;
import rx.subjects.PublishSubject;
import timber.log.Timber;

public class PositionItemAdapter extends TypedRecyclerAdapter<Object>
{
    public static final int VIEW_TYPE_HEADER = 0;
    public static final int VIEW_TYPE_PLACEHOLDER = 1;
    public static final int VIEW_TYPE_LOCKED = 2;
    public static final int VIEW_TYPE_POSITION = 3;

    protected Map<Integer, Integer> itemTypeToLayoutId;
    private UserProfileDTO userProfileDTO;

    @NonNull protected final CurrentUserId currentUserId;
    @NonNull protected final PublishSubject<PositionPartialTopView.CloseUserAction> userActionSubject;
    @Inject Picasso picasso;

    //<editor-fold desc="Constructors">
    public PositionItemAdapter(
            Context context,
            @NonNull Map<Integer, Integer> itemTypeToLayoutId,
            @NonNull CurrentUserId currentUserId)
    {
        super(Object.class, new PositionItemComparator());
        this.currentUserId = currentUserId;
        this.userActionSubject = PublishSubject.create();
        this.itemTypeToLayoutId = itemTypeToLayoutId;
        setHasStableIds(true);
        HierarchyInjector.inject(context, this);
    }
    //</editor-fold>

    @Override public int getItemViewType(int position)
    {
        Object item = getItem(position);
        if (item instanceof PositionLockedView.DTO)
        {
            return VIEW_TYPE_LOCKED;
        }
        else if (item instanceof PositionPartialTopView.DTO)
        {
            return VIEW_TYPE_POSITION;
        }
        else if (item instanceof PositionNothingView.DTO)
        {
            return VIEW_TYPE_PLACEHOLDER;
        }
        else if (item instanceof PositionSectionHeaderItemView.DTO)
        {
            return VIEW_TYPE_HEADER;
        }
        throw new IllegalArgumentException("Unhandled item " + item);
    }

    public boolean isEnabled(int position)
    {
        int viewType = getItemViewType(position);
        return viewType != VIEW_TYPE_HEADER
                && (viewType != VIEW_TYPE_PLACEHOLDER
                || userProfileDTO == null
                || userProfileDTO.getBaseKey().equals(currentUserId.toUserBaseKey()));
    }

    protected int getLayoutForViewType(int viewType)
    {
        return itemTypeToLayoutId.get(viewType);
    }

    @Override public long getItemId(int position)
    {
        Object item = getItem(position);
        return item == null ? 0 : item.hashCode();
    }

    public void linkWith(UserProfileDTO userProfileDTO)
    {
        this.userProfileDTO = userProfileDTO;
    }

    @NonNull public Observable<PositionPartialTopView.CloseUserAction> getUserActionObservable()
    {
        return userActionSubject.asObservable();
    }

    @Override public int add(@NonNull Object o)
    {
        updateLatestTradeOrRemove(o);
        return super.add(o);
    }

    @Override protected void addAllForBatch(@NonNull Collection<Object> collection)
    {
        for (Object o : collection)
        {
            updateLatestTradeOrRemove(o);
        }
        super.addAllForBatch(collection);
    }

    // TODO find a way to move the calculations in another thread
    private void updateLatestTradeOrRemove(@NonNull Object o)
    {
        if (mSortedList.size() > 0)
        {
            Object item;
            boolean areSame;
            for (int index = 0; index < mSortedList.size(); index++)
            {
                item = mSortedList.get(index);
                areSame = mComparator.areItemsTheSame(o, item);
                if (areSame && o instanceof PositionPartialTopView.DTO)
                {
                    ((PositionPartialTopView.DTO) item).positionDTO.latestTradeUtc = ((PositionPartialTopView.DTO) o).positionDTO.latestTradeUtc;
                    mSortedList.recalculatePositionOfItemAt(index);
                }
                else
                {
                    mSortedList.removeItemAt(index);
                }
            }
        }
    }

    @NonNull @Override public TypedViewHolder<Object> onCreateViewHolder(ViewGroup parent, int viewType)
    {
        int layoutToInflate = getLayoutForViewType(viewType);
        View v = LayoutInflater.from(parent.getContext()).inflate(layoutToInflate, parent, false);

        if (viewType == VIEW_TYPE_LOCKED)
        {
            return new PositionLockedView.ViewHolder((PositionLockedView) v);
        }
        else if (viewType == VIEW_TYPE_PLACEHOLDER)
        {
            return new PositionNothingView.ViewHolder((PositionNothingView) v);
        }
        else if (viewType == VIEW_TYPE_HEADER)
        {
            return new PositionSectionHeaderItemView.ViewHolder((PositionSectionHeaderItemView) v);
        }
        else if (viewType == VIEW_TYPE_POSITION)
        {
            return new PositionPartialTopView.ViewHolder((PositionPartialTopView) v, picasso);
        }
        return null;
    }

    @Override public void onBindViewHolder(TypedViewHolder<Object> holder, int position)
    {
        super.onBindViewHolder(holder, position);
        GraphicUtil.setEvenOddBackground(position, holder.itemView);
        if (holder instanceof PositionPartialTopView.ViewHolder)
        {
            ((PositionPartialTopView.ViewHolder) holder).getUserActionObservable().subscribe(userActionSubject);
        }
    }

    private static class PositionItemComparator extends TypedRecyclerComparator<Object>
    {
        @NonNull private final Comparator<PositionStatus> positionStatusComparator;
        @NonNull private final Comparator<PositionPartialTopView.DTO> topViewComparator;

        public PositionItemComparator()
        {
            this.positionStatusComparator = new PositionStatus.StatusComparator();
            this.topViewComparator = new PositionPartialTopView.AscendingLatestTradeDateComparator();
        }

        @Override public int compare(Object o1, Object o2)
        {
            if (o1 instanceof PositionNothingView.DTO && o2 instanceof PositionNothingView.DTO)
            {
                return 0;
            }
            if (o1 instanceof PositionNothingView.DTO)
            {
                return -1;
            }
            if (o2 instanceof PositionNothingView.DTO)
            {
                return 1;
            }
            else if (o1 instanceof PositionLockedView.DTO && o2 instanceof PositionLockedView.DTO)
            {
                return Integer.valueOf(((PositionLockedView.DTO) o1).id).compareTo(((PositionLockedView.DTO) o2).id);
            }
            else if (o1 instanceof PositionLockedView.DTO)
            {
                return -1;
            }
            else if (o2 instanceof PositionLockedView.DTO)
            {
                return 1;
            }
            else if (o1 instanceof PositionSectionHeaderItemView.DTO && o2 instanceof PositionSectionHeaderItemView.DTO)
            {
                return positionStatusComparator.compare(
                        ((PositionSectionHeaderItemView.DTO) o1).status,
                        ((PositionSectionHeaderItemView.DTO) o2).status);
            }
            else if (o1 instanceof PositionPartialTopView.DTO && o2 instanceof PositionPartialTopView.DTO)
            {
                return topViewComparator.compare((PositionPartialTopView.DTO) o1, (PositionPartialTopView.DTO) o2);
            }
            else if (o1 instanceof PositionPartialTopView.DTO && o2 instanceof PositionSectionHeaderItemView.DTO)
            {
                PositionStatus s1 = ((PositionPartialTopView.DTO) o1).positionDTO.positionStatus;
                PositionStatus s2 = ((PositionSectionHeaderItemView.DTO) o2).status;
                if (s1 == null || s1.equals(s2))
                {
                    return 1;
                }
                return positionStatusComparator.compare(
                        ((PositionPartialTopView.DTO) o1).positionDTO.positionStatus,
                        ((PositionSectionHeaderItemView.DTO) o2).status);
            }
            else if (o1 instanceof PositionSectionHeaderItemView.DTO && o2 instanceof PositionPartialTopView.DTO)
            {
                if (((PositionSectionHeaderItemView.DTO) o1).status.equals(((PositionPartialTopView.DTO) o2).positionDTO.positionStatus))
                {
                    return -1;
                }
                return positionStatusComparator.compare(
                        ((PositionSectionHeaderItemView.DTO) o1).status,
                        ((PositionPartialTopView.DTO) o2).positionDTO.positionStatus);
            }
            else
            {
                Timber.e(new IllegalStateException(), "Unhandled  " + o1.getClass() + " " + o2.getClass());
                throw new IllegalStateException("Unhandled  " + o1.getClass() + " " + o2.getClass());
            }
        }

        @Override public boolean areContentsTheSame(Object oldItem, Object newItem)
        {
            if (oldItem instanceof PositionNothingView.DTO)
            {
                return ((PositionNothingView.DTO) oldItem).description.equals(((PositionNothingView.DTO) newItem).description);
            }
            else if (oldItem instanceof PositionSectionHeaderItemView.DTO)
            {
                return ((PositionSectionHeaderItemView.DTO) oldItem).header.equals((((PositionSectionHeaderItemView.DTO) newItem).header)) &&
                        ((PositionSectionHeaderItemView.DTO) oldItem).timeBase.equals((((PositionSectionHeaderItemView.DTO) newItem).timeBase));
            }
            else if (oldItem instanceof PositionLockedView.DTO)
            {
                PositionLockedView.DTO o1 = (PositionLockedView.DTO) oldItem;
                PositionLockedView.DTO o2 = (PositionLockedView.DTO) newItem;
                return o1.positionPercent.equals(o2.positionPercent)
                        &&
                        o1.unrealisedPLValueHeader.equals(o2.unrealisedPLValueHeader)
                        &&
                        o1.unrealisedPLValue.equals(o2.unrealisedPLValue)
                        &&
                        o1.realisedPLValueHeader.equals(o2.realisedPLValueHeader)
                        &&
                        o1.realisedPLValue.equals(o2.realisedPLValue)
                        &&
                        o1.totalInvestedValue.equals(o2.totalInvestedValue);
            }
            else if (oldItem instanceof PositionPartialTopView.DTO)
            {
                PositionPartialTopView.DTO o1 = (PositionPartialTopView.DTO) oldItem;
                PositionPartialTopView.DTO o2 = (PositionPartialTopView.DTO) newItem;
                if (o1.stockLogoVisibility != o2.stockLogoVisibility) return false;
                if (o1.stockLogoRes != o2.stockLogoRes) return false;
                if (o1.flagsContainerVisibility != o2.flagsContainerVisibility) return false;
                if (o1.btnCloseVisibility != o2.btnCloseVisibility) return false;
                if (o1.companyNameVisibility != o2.companyNameVisibility) return false;
                if (o1.shareCountRowVisibility != o2.shareCountRowVisibility) return false;
                if (o1.shareCountVisibility != o2.shareCountVisibility) return false;
                if (o1.lastAmountContainerVisibility != o2.lastAmountContainerVisibility) return false;
                if (o1.positionPercentVisibility != o2.positionPercentVisibility) return false;
                if (o1.gainIndicator != o2.gainIndicator) return false;
                if (o1.gainLossColor != o2.gainLossColor) return false;
                if (o1.unrealisedPLVisibility != o2.unrealisedPLVisibility) return false;
                if (o1.lastAmountHeaderVisibility != o2.lastAmountHeaderVisibility) return false;
                if (o1.stockLogoUrl != null ? !o1.stockLogoUrl.equals(o2.stockLogoUrl) : o2.stockLogoUrl != null) return false;
                if (o1.fxPair != null ? !o1.fxPair.equals(o2.fxPair) : o2.fxPair != null) return false;
                if (!o1.stockSymbol.equals(o2.stockSymbol)) return false;
                if (!o1.companyName.equals(o2.companyName)) return false;
                if (!o1.lastPriceAndRise.equals(o2.lastPriceAndRise)) return false;
                if (!o1.shareCountHeader.equals(o2.shareCountHeader)) return false;
                if (!o1.shareCount.equals(o2.shareCount)) return false;
                if (!o1.shareCountText.equals(o2.shareCountText)) return false;
                if (!o1.positionPercent.equals(o2.positionPercent)) return false;
                if (!o1.gainLossHeader.equals(o2.gainLossHeader)) return false;
                if (!o1.gainLoss.equals(o2.gainLoss)) return false;
                if (!o1.gainLossPercent.equals(o2.gainLossPercent)) return false;
                if (o1.totalInvested != null ? !o1.totalInvested.equals(o2.totalInvested) : o2.totalInvested != null) return false;
                if (!o1.unrealisedPL.equals(o2.unrealisedPL)) return false;
                return o1.lastAmount.equals(o2.lastAmount);
            }
            else
            {
                throw new IllegalStateException("Unhandled  " + oldItem.getClass() + " " + newItem.getClass());
            }
        }

        @Override public boolean areItemsTheSame(Object item1, Object item2)
        {
            if (item1.getClass().equals(item2.getClass()))
            {
                if (item1 instanceof PositionNothingView.DTO)
                {
                    return true; //There can only be one empty view
                }
                else if (item1 instanceof PositionSectionHeaderItemView.DTO)
                {
                    return ((PositionSectionHeaderItemView.DTO) item1).type.equals((((PositionSectionHeaderItemView.DTO) item2).type));
                }
                else if (item1 instanceof PositionLockedView.DTO)
                {
                    return ((PositionLockedView.DTO) item1).id == ((PositionLockedView.DTO) item2).id;
                }
                else if (item1 instanceof PositionPartialTopView.DTO)
                {
                    return ((PositionPartialTopView.DTO) item1).positionDTO.id == ((PositionPartialTopView.DTO) item2).positionDTO.id;
                }
            }
            return false;
        }
    }
}
