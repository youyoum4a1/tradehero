package com.tradehero.th.fragments.social.hero;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.tradehero.common.utils.THLog;
import com.tradehero.th.R;
import com.tradehero.th.adapters.ArrayDTOAdapter;
import com.tradehero.th.api.social.HeroDTO;
import com.tradehero.th.widget.list.BaseListHeaderView;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

/** Created with IntelliJ IDEA. User: xavier Date: 10/14/13 Time: 4:12 PM To change this template use File | Settings | File Templates. */
public class HeroListItemAdapter extends ArrayDTOAdapter<HeroDTO, HeroListItemView>
{
    public static final String TAG = HeroListItemAdapter.class.getSimpleName();

    public static final int VIEW_TYPE_EMPTY_PLACEHOLDER = 0;
    public static final int VIEW_TYPE_HEADER_ACTIVE = 1;
    public static final int VIEW_TYPE_ITEM_ACTIVE = 2;
    public static final int VIEW_TYPE_HEADER_INACTIVE = 3;
    public static final int VIEW_TYPE_ITEM_INACTIVE = 4;

    public static final int SUPER_POSITION_OFFSET = 1;

    private final int heroEmptyPlaceholderResId;
    private final int headerActiveResId;

    private final int headerInactiveResId;
    protected List<HeroDTO> activeHeroes;
    protected List<HeroDTO> inactiveHeroes;
    private WeakReference<HeroListItemView.OnHeroStatusButtonClickedListener> heroStatusButtonClickedListener = new WeakReference<>(null);
    private WeakReference<View.OnClickListener> mostSkilledClicked = new WeakReference<>(null);

    public HeroListItemAdapter(Context context, LayoutInflater inflater, int heroEmptyPlaceholderResId, int heroLayoutResId, int headerActiveResId,
            int headerInactiveResId)
    {
        super(context, inflater, heroLayoutResId);
        this.heroEmptyPlaceholderResId = heroEmptyPlaceholderResId;
        this.headerActiveResId = headerActiveResId;
        this.headerInactiveResId = headerInactiveResId;
    }

    public void setHeroStatusButtonClickedListener(HeroListItemView.OnHeroStatusButtonClickedListener heroStatusButtonClickedListener)
    {
        this.heroStatusButtonClickedListener = new WeakReference<>(heroStatusButtonClickedListener);
    }

    public void setMostSkilledClicked(View.OnClickListener mostSkilledClicked)
    {
        this.mostSkilledClicked = new WeakReference<>(mostSkilledClicked);
    }

    @Override public boolean hasStableIds()
    {
        return true;
    }

    @Override public int getViewTypeCount()
    {
        return 5;
    }

    @Override public void setItems(List<HeroDTO> items)
    {
        super.setItems(items);
        if (items == null)
        {
            activeHeroes = null;
            inactiveHeroes = null;
            THLog.d(TAG, "Null items");
        }
        else
        {
            activeHeroes = new ArrayList<>();
            inactiveHeroes = new ArrayList<>();
            for (HeroDTO heroDTO : items)
            {
                if (heroDTO.active)
                {
                    activeHeroes.add(heroDTO);
                }
                else
                {
                    //TODO remove
                    //inactiveHeroes.add(heroDTO);
                }
            }
            THLog.d(TAG, "setItems active " + activeHeroes.size() + ", inactive " + inactiveHeroes.size());
        }
    }

    @Override public int getItemViewType(int position)
    {
        int activeCount = getActiveCount();
        if (activeCount == 0 && position == 0)
        {
            return VIEW_TYPE_EMPTY_PLACEHOLDER;
        }
        if (activeCount > 0 && position == 0)
        {
            // Active header is visible only when there are active heroes
            return VIEW_TYPE_HEADER_ACTIVE;
        }
        if (activeCount > 0 && position <= activeCount)
        {
            // Active heroes are visible only when there are any
            return VIEW_TYPE_ITEM_ACTIVE;
        }
        int inactiveCount = getInactiveCount();
        if (activeCount == 0 && inactiveCount > 0)
        {
            // Only inactive heroes are visible
            // At this stage position > 0
            if (position == 1)
            {
                // When no active, inactive come on top
                return VIEW_TYPE_HEADER_INACTIVE;
            }
            return VIEW_TYPE_ITEM_INACTIVE;
        }
        // By now, (activeCount > 0 && inactiveCount > 0) is true
        if (position == activeCount + 1)
        {
            // When there are active, inactive come after
            return VIEW_TYPE_HEADER_INACTIVE;
        }
        return VIEW_TYPE_ITEM_INACTIVE;
    }

    @Override public int getCount()
    {
        int total = 0;
        int activeCount = getActiveCount();
        int inactiveCount = getInactiveCount();
        total += activeCount == 0 ? 1 : 0; // The place holder part
        total += activeCount == 0 ? 0 : (activeCount + 1); // The active part
        total += inactiveCount == 0 ? 0 : (inactiveCount + 1); // The inactive part
        return total;
    }

    public int getActiveCount()
    {
        return activeHeroes == null ? 0 : activeHeroes.size();
    }

    public int getOffsetPositionInactive(int position)
    {
        return position - 2 - getActiveCount();
    }

    public int getInactiveCount()
    {
        return inactiveHeroes == null ? 0 : inactiveHeroes.size();
    }

    @Override public long getItemId(int position)
    {
        return getItem(position).hashCode();
    }

    @Override public Object getItem(int position)
    {
        switch (getItemViewType(position))
        {
            case VIEW_TYPE_EMPTY_PLACEHOLDER:
                return "emptyPlaceholder";

            case VIEW_TYPE_HEADER_ACTIVE:
                return "headerActive";

            case VIEW_TYPE_ITEM_ACTIVE:
                return activeHeroes.get(position - 1);

            case VIEW_TYPE_HEADER_INACTIVE:
                return "headerInactive";

            case VIEW_TYPE_ITEM_INACTIVE:
                return inactiveHeroes.get(getOffsetPositionInactive(position));

            default:
                throw new UnsupportedOperationException("Not implemented");
        }
    }

    @Override public View getView(int position, View convertView, ViewGroup parent)
    {
        switch (getItemViewType(position))
        {
            case VIEW_TYPE_EMPTY_PLACEHOLDER:
                convertView = inflater.inflate(heroEmptyPlaceholderResId, parent, false);
                View mostSkilledButton = convertView.findViewById(R.id.btn_leaderboard_most_skilled);
                if (mostSkilledButton != null)
                {
                    mostSkilledButton.setOnClickListener(mostSkilledClicked.get());
                }
                break;

            case VIEW_TYPE_HEADER_ACTIVE:
                if (!(convertView instanceof BaseListHeaderView))
                {
                    convertView = inflater.inflate(headerActiveResId, parent, false);
                }
                ((BaseListHeaderView) convertView).setHeaderTextContent(context.getString(R.string.manage_heroes_active_header));
                break;

            case VIEW_TYPE_HEADER_INACTIVE:
                if (!(convertView instanceof BaseListHeaderView))
                {
                    convertView = inflater.inflate(headerInactiveResId, parent, false);
                }
                ((BaseListHeaderView) convertView).setHeaderTextContent(context.getString(R.string.manage_heroes_inactive_header));
                break;

            case VIEW_TYPE_ITEM_ACTIVE:
            case VIEW_TYPE_ITEM_INACTIVE:
                if (!(convertView instanceof HeroListItemView))
                {
                    convertView = inflater.inflate(layoutResourceId, parent, false);
                }
                ((HeroListItemView) convertView).display((HeroDTO) getItem(position));
                ((HeroListItemView) convertView).setHeroStatusButtonClickedListener(heroStatusButtonClickedListener.get());
                break;

            default:
                throw new UnsupportedOperationException("Not implemented");
        }
        return convertView;
    }

    @Override public boolean areAllItemsEnabled()
    {
        return false;
    }

    @Override public boolean isEnabled(int position)
    {
        switch (getItemViewType(position))
        {
            case VIEW_TYPE_EMPTY_PLACEHOLDER:
            case VIEW_TYPE_HEADER_ACTIVE:
            case VIEW_TYPE_HEADER_INACTIVE:
                return false;
            case VIEW_TYPE_ITEM_ACTIVE:
            case VIEW_TYPE_ITEM_INACTIVE:
                return true;
            default:
                throw new UnsupportedOperationException("Not implemented");
        }
    }

    @Override protected void fineTune(int position, HeroDTO dto, HeroListItemView dtoView)
    {
        throw new UnsupportedOperationException("Not implemented");
    }
}
