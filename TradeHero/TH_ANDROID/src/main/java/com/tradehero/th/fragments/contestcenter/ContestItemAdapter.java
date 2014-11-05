package com.tradehero.th.fragments.contestcenter;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import com.tradehero.th.R;
import com.tradehero.th.api.DTOView;
import com.tradehero.th.api.competition.ProviderDTO;
import com.tradehero.th.inject.HierarchyInjector;
import android.support.annotation.NonNull;
import se.emilsjolander.stickylistheaders.StickyListHeadersAdapter;

public class ContestItemAdapter extends ArrayAdapter<ContestPageDTO>
        implements StickyListHeadersAdapter
{
    public static final int TYPE_NORMAL = 0;
    public static final int TYPE_VIP = 1;
    public static final int TYPE_HEADER = 2;

    @NonNull @LayoutRes private Integer[] typeToResIds;

    //<editor-fold desc="Constructors">
    public ContestItemAdapter(
            @NonNull Context context,
            @LayoutRes int vipViewResourceId,
            @LayoutRes int normalViewResourceId)
    {
        super(context, 0);
        typeToResIds = new Integer[3];
        typeToResIds[TYPE_HEADER] = R.layout.leaderboard_separator;
        typeToResIds[TYPE_VIP] = vipViewResourceId;
        typeToResIds[TYPE_NORMAL] = normalViewResourceId;
        HierarchyInjector.inject(context, this);
    }
    //</editor-fold>

    @Override public int getViewTypeCount()
    {
        return typeToResIds.length;
    }

    @Override public int getItemViewType(int position)
    {
        ContestPageDTO item = getItem(position);
        if (item instanceof ProviderContestPageDTO)
        {
            ProviderDTO providerDTO = ((ProviderContestPageDTO) item).providerDTO;
            if (providerDTO.vip != null && providerDTO.vip)
            {
                return TYPE_VIP;
            }
            else
            {
                return TYPE_NORMAL;
            }
        }
        else if (item instanceof EmptyHeadLineDTO)
        {
            return TYPE_HEADER;
        }
        throw new IllegalArgumentException("Unhandled item " + item);
    }

    @LayoutRes public int getItemViewResId(int position)
    {
        return typeToResIds[getItemViewType(position)];
    }

    @Override public View getView(int position, View convertView, ViewGroup viewGroup)
    {
        if (convertView == null)
        {
            convertView = LayoutInflater.from(getContext()).inflate(getItemViewResId(position), viewGroup, false);
        }
        if (convertView instanceof DTOView)
        {
            //noinspection unchecked
            ((DTOView<ContestPageDTO>) convertView).display(getItem(position));
        }
        return convertView;
    }

    //<editor-fold desc="For headers">
    @Override public View getHeaderView(int position, View convertView, ViewGroup parent)
    {
        if (convertView == null)
        {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.leaderboard_separator, parent, false);
        }
        return convertView;
    }

    @Override public long getHeaderId(int position)
    {
        return getItemViewType(position);
    }
    //</editor-fold>

    @Override public boolean areAllItemsEnabled()
    {
        return false;
    }

    @Override public boolean isEnabled(int position)
    {
        return getItem(position) instanceof ProviderContestPageDTO;
    }
}
