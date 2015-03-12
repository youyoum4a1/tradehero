package com.tradehero.th.fragments.position;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import com.tradehero.th.R;
import com.tradehero.th.api.position.PositionDTO;
import com.tradehero.th.api.position.PositionStatus;
import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.fragments.position.partial.PositionPartialTopView;
import com.tradehero.th.fragments.position.view.PositionLockedView;
import com.tradehero.th.fragments.position.view.PositionNothingView;
import com.tradehero.th.fragments.position.view.PositionView;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    @NonNull private final TabbedPositionListFragment.TabType positionType;

    @NonNull private final Map<TabbedPositionListFragment.TabType, Integer> viewTypeCorrespondance;

    //<editor-fold desc="Constructors">
    public PositionItemAdapter(
            @NonNull Context context,
            @NonNull Map<Integer, Integer> itemTypeToLayoutId,
            @NonNull CurrentUserId currentUserId,
            @NonNull TabbedPositionListFragment.TabType positionType)
    {
        super(context, 0);
        this.itemTypeToLayoutId = itemTypeToLayoutId;
        this.currentUserId = currentUserId;
        this.positionType = positionType;
        this.viewTypeCorrespondance = new HashMap<>();
        viewTypeCorrespondance.put(TabbedPositionListFragment.TabType.CLOSED, VIEW_TYPE_CLOSED);
        viewTypeCorrespondance.put(TabbedPositionListFragment.TabType.LONG, VIEW_TYPE_OPEN_LONG);
        viewTypeCorrespondance.put(TabbedPositionListFragment.TabType.SHORT, VIEW_TYPE_OPEN_SHORT);
    }
    //</editor-fold>

    @Override public int getViewTypeCount()
    {
        return 9;
    }

    @Override public int getItemViewType(int position)
    {
        Object item = getItem(position);
        if (item instanceof PositionDTO)
        {
            return getItemViewType((PositionDTO) item);
        }
        else if (item == null)
        {
            return VIEW_TYPE_PLACEHOLDER;
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
        return viewType != VIEW_TYPE_HEADER && (!(viewType == VIEW_TYPE_PLACEHOLDER && userProfileDTO != null) || userProfileDTO.getBaseKey()
                .equals(currentUserId.toUserBaseKey()));
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

    public void addAll(@Nullable List<PositionDTO> dtos)
    {

        if (dtos == null || dtos.size() == 0)
        {
            return;
        }

        ArrayList<Object> positions = new ArrayList<>();

        for (PositionDTO positionDTO : dtos)
        {
            if (getItemViewType(positionDTO) == viewTypeCorrespondance.get(positionType))
            {
                positions.add(positionDTO);
            }
        }
        addAll(positions);

    }

    @Override public View getView(int position, View convertView, ViewGroup parent)
    {
        int itemViewType = getItemViewType(position);
        int layoutToInflate = getLayoutForPosition(position);

        if (convertView == null)
        {
            convertView = LayoutInflater.from(getContext()).inflate(layoutToInflate, parent, false);
        }

        Object item = getItem(position);

        if (itemViewType == VIEW_TYPE_LOCKED)
        {
            PositionLockedView cell = (PositionLockedView) convertView;
            cell.linkWith((PositionDTO) item);
            cell.display();
        }
        else if (itemViewType == VIEW_TYPE_PLACEHOLDER)
        {
            if(convertView instanceof PositionNothingView)
            {
                ((PositionNothingView) convertView).display(isEnabled(position));
            }
        }
        else if (convertView instanceof PositionView)
        {
            preparePositionView((PositionView) convertView, item, position);
        }
        else if (convertView instanceof PositionPartialTopView)
        {
            ((PositionPartialTopView) convertView).linkWith((PositionDTO) item, false);
            ((PositionPartialTopView) convertView).display();
        }

        if ((position % 2) == 0) {
            convertView.setBackgroundColor(Color.WHITE);
        } else {
            convertView.setBackgroundResource(R.color.portfolio_header_background_color);
        }

        return convertView;
    }

    protected void preparePositionView(PositionView cell, Object item, int position)
    {
        cell.linkWith((PositionDTO) item, false);
        cell.display();
    }

    public void linkWith(UserProfileDTO userProfileDTO)
    {
        this.userProfileDTO = userProfileDTO;
    }

}
