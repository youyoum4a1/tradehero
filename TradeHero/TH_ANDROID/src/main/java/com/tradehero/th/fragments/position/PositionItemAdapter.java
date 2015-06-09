package com.tradehero.th.fragments.position;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import com.tradehero.th.api.position.PositionDTO;
import com.tradehero.th.api.position.PositionStatus;
import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.fragments.position.partial.PositionPartialTopView;
import com.tradehero.th.fragments.position.view.PositionLockedView;
import com.tradehero.th.fragments.position.view.PositionNothingView;
import com.tradehero.th.fragments.position.view.PositionView;
import com.tradehero.th.utils.GraphicUtil;
import java.util.Map;
import rx.Observable;
import rx.subjects.PublishSubject;

public class PositionItemAdapter extends ArrayAdapter<Object>
{
    public static final int VIEW_TYPE_HEADER = 0;
    public static final int VIEW_TYPE_PLACEHOLDER = 1;
    public static final int VIEW_TYPE_LOCKED = 2;
    public static final int VIEW_TYPE_OPEN_LONG = 3;
    public static final int VIEW_TYPE_OPEN_SHORT = 5;
    public static final int VIEW_TYPE_CLOSED = 7;

    protected Map<Integer, Integer> itemTypeToLayoutId;
    private UserProfileDTO userProfileDTO;

    @NonNull protected final CurrentUserId currentUserId;
    @NonNull protected final PublishSubject<PositionPartialTopView.CloseUserAction> userActionSubject;

    //<editor-fold desc="Constructors">
    public PositionItemAdapter(
            @NonNull Context context,
            @NonNull Map<Integer, Integer> itemTypeToLayoutId,
            @NonNull CurrentUserId currentUserId)
    {
        super(context, 0);
        this.itemTypeToLayoutId = itemTypeToLayoutId;
        this.currentUserId = currentUserId;
        this.userActionSubject = PublishSubject.create();
    }
    //</editor-fold>

    @Override public int getViewTypeCount()
    {
        return 9;
    }

    @Override public int getItemViewType(int position)
    {
        Object item = getItem(position);
        if (item instanceof PositionLockedView.DTO)
        {
            return VIEW_TYPE_LOCKED;
        }
        else if (item instanceof PositionPartialTopView.DTO)
        {
            return VIEW_TYPE_OPEN_LONG;
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

    protected int getItemViewType(@NonNull PositionDTO item)
    {
        Boolean isClosed = item.isClosed();
        Boolean isOpen = item.isOpen();
        if (item.isLocked())
        {
            return VIEW_TYPE_LOCKED;
        }
        else if (isClosed != null && isClosed)
        {
            return VIEW_TYPE_CLOSED;
        }
        else if (isOpen != null && isOpen)
        {
            boolean isShort = item.positionStatus != null && item.positionStatus.equals(PositionStatus.SHORT);
            if (isShort)
            {
                return VIEW_TYPE_OPEN_SHORT;
            }
            return VIEW_TYPE_OPEN_LONG;
        }

        // TODO short
        throw new IllegalArgumentException("Unhandled item " + item);
    }

    @Override public boolean areAllItemsEnabled()
    {
        return false;
    }

    @Override public boolean isEnabled(int position)
    {
        int viewType = getItemViewType(position);
        return viewType != VIEW_TYPE_HEADER
                && (viewType != VIEW_TYPE_PLACEHOLDER
                || userProfileDTO == null
                || userProfileDTO.getBaseKey().equals(currentUserId.toUserBaseKey()));
    }

    protected int getLayoutForPosition(int position)
    {
        return itemTypeToLayoutId.get(getItemViewType(position));
    }

    @Override public boolean hasStableIds()
    {
        return true;
    }

    @Override public long getItemId(int position)
    {
        Object item = getItem(position);
        return item == null ? 0 : item.hashCode();
    }

    @Override public View getView(int position, View convertView, ViewGroup parent)
    {
        int itemViewType = getItemViewType(position);
        int layoutToInflate = getLayoutForPosition(position);

        if (convertView == null)
        {
            convertView = LayoutInflater.from(getContext()).inflate(layoutToInflate, parent, false);
            if (convertView instanceof PositionPartialTopView)
            {
                ((PositionPartialTopView) convertView).getUserActionObservable().subscribe(userActionSubject);
            }
        }

        Object item = getItem(position);

        if (itemViewType == VIEW_TYPE_LOCKED)
        {
            PositionLockedView cell = (PositionLockedView) convertView;
            cell.display((PositionLockedView.DTO) item);
        }
        else if (itemViewType == VIEW_TYPE_PLACEHOLDER)
        {
            ((PositionNothingView) convertView).display((PositionNothingView.DTO) item);
        }
        else if (convertView instanceof PositionView)
        {
            ((PositionView) convertView).display((PositionView.DTO) item);
        }
        else if (convertView instanceof PositionPartialTopView)
        {
            ((PositionPartialTopView) convertView).display((PositionPartialTopView.DTO) item);
        }
        else if (convertView instanceof PositionSectionHeaderItemView)
        {
            ((PositionSectionHeaderItemView) convertView).display((PositionSectionHeaderItemView.DTO) item);
        }

        GraphicUtil.setEvenOddBackground(position, convertView);

        return convertView;
    }

    public void linkWith(UserProfileDTO userProfileDTO)
    {
        this.userProfileDTO = userProfileDTO;
    }

    @NonNull public Observable<PositionPartialTopView.CloseUserAction> getUserActionObservable()
    {
        return userActionSubject.asObservable();
    }
}
