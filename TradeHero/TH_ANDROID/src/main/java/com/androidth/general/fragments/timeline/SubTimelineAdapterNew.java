package com.androidth.general.fragments.timeline;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.androidth.general.R;
import com.androidth.general.adapters.DTOSetAdapter;
import com.androidth.general.api.pagination.RangeDTO;
import com.androidth.general.fragments.discussion.AbstractDiscussionCompactItemViewLinear;
import com.androidth.general.models.discussion.UserDiscussionAction;
import com.androidth.general.utils.Constants;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import rx.Observable;
import rx.subjects.PublishSubject;
import se.emilsjolander.stickylistheaders.StickyListHeadersAdapter;

public class SubTimelineAdapterNew extends DTOSetAdapter<Object>
        implements StickyListHeadersAdapter
{
    private static final int VIEW_TYPE_ITEM_TIMELINE = 0;
    private static final int VIEW_TYPE_TIMELINE_EMPTY = 1;
    private static final int VIEW_TYPE_LOADING = 2;

    public static final String DTO_CALL_ACTION = "CallToAction";
    public static final String DTO_LOADING = "Loading";

    @LayoutRes final int timelineResId;
    @LayoutRes final int emptyResId;
    @LayoutRes final int loadingResId;
    @NonNull private final PublishSubject<UserDiscussionAction> userActionSubject;
    @NonNull private final PublishSubject<TimelineFragment.TabType> tabTypeSubject;

    @NonNull private TimelineFragment.TabType currentTabType = TimelineFragment.TabType.TIMELINE;

    //<editor-fold desc="Constructors">
    public SubTimelineAdapterNew(
            @NonNull Context context,
            @LayoutRes int timelineResId,
            @LayoutRes int emptyResId,
            @LayoutRes int loadingResId)
    {
        super(context, new ObjectComparator());
        this.timelineResId = timelineResId;
        this.emptyResId = emptyResId;
        this.loadingResId = loadingResId;
        this.userActionSubject = PublishSubject.create();
        this.tabTypeSubject = PublishSubject.create();
    }
    //</editor-fold>

    @NonNull public List<Object> reprocess(@Nullable Collection<? extends AbstractDiscussionCompactItemViewLinear.DTO> dtos)
    {
        List<Object> objects = new ArrayList<>();
        if (showingLoadingItem() && (dtos == null || dtos.size() == 0))
        {
            objects.add(DTO_CALL_ACTION);
        }
        else
        {
            objects.addAll(dtos);
        }
        return objects;
    }

    protected boolean showingLoadingItem()
    {
        return getCount() == 0 || (getCount() == 1 && getItem(0).equals(DTO_LOADING));
    }

    @NonNull public Observable<UserDiscussionAction> getUserActionObservable()
    {
        return userActionSubject.asObservable();
    }

    @NonNull public Observable<TimelineFragment.TabType> getTabTypeObservable()
    {
        return tabTypeSubject.asObservable();
    }

    @Override public int getViewTypeCount()
    {
        return 3;
    }

    @Override public int getItemViewType(int position)
    {
        Object item = getItem(position);
        if (item instanceof AbstractDiscussionCompactItemViewLinear.DTO)
        {
            return VIEW_TYPE_ITEM_TIMELINE;
        }
        else if (item.equals(DTO_CALL_ACTION))
        {
            return VIEW_TYPE_TIMELINE_EMPTY;
        }
        else if (item.equals(DTO_LOADING))
        {
            return VIEW_TYPE_LOADING;
        }
        throw new IllegalArgumentException("Unhandled item " + item);
    }

    @Override public int appendTail(@Nullable Collection<?> newOnes)
    {
        remove(DTO_CALL_ACTION);
        remove(DTO_LOADING);
        return super.appendTail(newOnes);
    }

    @Override public int appendHead(@Nullable List<?> newOnes)
    {
        remove(DTO_CALL_ACTION);
        remove(DTO_LOADING);
        return super.appendHead(newOnes);
    }

    @Override public View getView(int position, View convertView, ViewGroup parent)
    {
        if (convertView == null)
        {
            convertView = inflate(position, parent);
        }
        if (convertView instanceof AbstractDiscussionCompactItemViewLinear)
        {
            ((AbstractDiscussionCompactItemViewLinear) convertView).display(
                    (AbstractDiscussionCompactItemViewLinear.DTO) getItem(position));
        }

        return convertView;
    }

    @NonNull protected View inflate(int position, ViewGroup parent)
    {
        View view = LayoutInflater.from(context).inflate(getViewResId(position), parent, false);
        if (view instanceof TimelineItemViewLinear)
        {
            ((TimelineItemViewLinear) view).getUserActionObservable().retry().subscribe(userActionSubject);
        }
        return view;
    }

    @LayoutRes protected int getViewResId(int position)
    {
        switch (getItemViewType(position))
        {
            case VIEW_TYPE_ITEM_TIMELINE:
                return timelineResId;
            case VIEW_TYPE_TIMELINE_EMPTY:
                return emptyResId;
            case VIEW_TYPE_LOADING:
                return loadingResId;
            default:
                throw new IllegalArgumentException("Unhandled view type " + getItemViewType(position));
        }
    }

    @NonNull public RangeDTO getLatestRange()
    {
        AbstractDiscussionCompactItemViewLinear.DTO latest = getLatest();
        if (latest == null)
        {
            return new RangeDTO(Constants.TIMELINE_ITEM_PER_PAGE, null, null);
        }
        return new RangeDTO(Constants.TIMELINE_ITEM_PER_PAGE, null, latest.viewHolderDTO.discussionDTO.id);
    }

    @Nullable public AbstractDiscussionCompactItemViewLinear.DTO getLatest()
    {
        if (getCount() > 0)
        {
            Object item = getItem(0);
            if (item instanceof AbstractDiscussionCompactItemViewLinear.DTO)
            {
                return (AbstractDiscussionCompactItemViewLinear.DTO) item;
            }
        }
        return null;
    }

    @NonNull public RangeDTO getOlderRange()
    {
        AbstractDiscussionCompactItemViewLinear.DTO older = getOldest();
        if (older == null)
        {
            return new RangeDTO(Constants.TIMELINE_ITEM_PER_PAGE, null, null);
        }
        return new RangeDTO(Constants.TIMELINE_ITEM_PER_PAGE, older.viewHolderDTO.discussionDTO.id - 1, null);
    }

    @Nullable public AbstractDiscussionCompactItemViewLinear.DTO getOldest()
    {
        if (getCount() > 0)
        {
            Object item = getItem(getCount() - 1);
            if (item instanceof AbstractDiscussionCompactItemViewLinear.DTO)
            {
                return (AbstractDiscussionCompactItemViewLinear.DTO) item;
            }
        }
        return null;
    }

    public void setCurrentTabType(@NonNull TimelineFragment.TabType currentTabType)
    {
        this.currentTabType = currentTabType;
    }

    @Override public View getHeaderView(int i, View convertView, ViewGroup viewGroup)
    {
        if (convertView == null)
        {
            convertView = LayoutInflater.from(context).inflate(R.layout.user_profile_detail_bottom_buttons, viewGroup, false);
            ((TimelineHeaderButtonView) convertView).getTabTypeObservable().subscribe(tabTypeSubject);
        }
        ((TimelineHeaderButtonView) convertView).setActive(currentTabType);
        return convertView;
    }

    @Override public long getHeaderId(int i)
    {
        return 1;
    }

    protected static class ObjectComparator implements Comparator<Object>
    {
        @Override public int compare(@NonNull Object lhs, @NonNull Object rhs)
        {
            if (lhs == rhs || lhs.equals(rhs))
            {
                return 0;
            }
            if (lhs instanceof String && !(rhs instanceof String))
            {
                return 1;
            }
            if (!(lhs instanceof String) && rhs instanceof String)
            {
                return -1;
            }
            if (lhs instanceof String)
            {
                return ((String) lhs).compareTo((String) rhs);
            }
            if (lhs instanceof AbstractDiscussionCompactItemViewLinear.DTO
                    && rhs instanceof AbstractDiscussionCompactItemViewLinear.DTO)
            {
                return Integer.valueOf(((AbstractDiscussionCompactItemViewLinear.DTO) rhs).viewHolderDTO.discussionDTO.id)
                        .compareTo(((AbstractDiscussionCompactItemViewLinear.DTO) lhs).viewHolderDTO.discussionDTO.id);
            }
            throw new IllegalArgumentException("Unhandled types " + lhs + ", " + rhs);
        }
    }
}
