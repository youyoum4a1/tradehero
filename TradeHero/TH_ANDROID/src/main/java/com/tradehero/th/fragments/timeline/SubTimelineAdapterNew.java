package com.tradehero.th.fragments.timeline;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.ViewGroup;
import com.tradehero.th.adapters.ViewDTOSetAdapter;
import com.tradehero.th.api.pagination.RangeDTO;
import com.tradehero.th.fragments.discussion.AbstractDiscussionCompactItemViewLinear;
import com.tradehero.th.models.discussion.UserDiscussionAction;
import com.tradehero.th.utils.Constants;
import rx.Observable;
import rx.subjects.PublishSubject;

public class SubTimelineAdapterNew extends ViewDTOSetAdapter<AbstractDiscussionCompactItemViewLinear.DTO, TimelineItemViewLinear>
{
    @LayoutRes final int layoutResourceId;
    @NonNull private final PublishSubject<UserDiscussionAction> userActionSubject;

    //<editor-fold desc="Constructors">
    public SubTimelineAdapterNew(@NonNull Context context,
            @LayoutRes int layoutResourceId)
    {
        super(context);
        this.layoutResourceId = layoutResourceId;
        this.userActionSubject = PublishSubject.create();
    }
    //</editor-fold>

    @NonNull public Observable<UserDiscussionAction> getUserActionObservable()
    {
        return userActionSubject.asObservable();
    }

    @Override @LayoutRes protected int getViewResId(int position)
    {
        return layoutResourceId;
    }

    @Override protected TimelineItemViewLinear inflate(int position, ViewGroup parent)
    {
        TimelineItemViewLinear view = super.inflate(position, parent);
        view.getUserActionObservable().retry().subscribe(userActionSubject);
        return view;
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
            return getItem(0);
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
            return getItem(getCount() - 1);
        }
        return null;
    }
}
