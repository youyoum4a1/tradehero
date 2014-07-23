package com.tradehero.th.fragments.contestcenter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import com.tradehero.th.R;
import com.tradehero.th.api.DTOView;
import com.tradehero.th.fragments.leaderboard.main.CommunityPageDTO;
import com.tradehero.th.fragments.leaderboard.main.ProviderCommunityPageDTO;
import com.tradehero.th.utils.DaggerUtils;
import se.emilsjolander.stickylistheaders.StickyListHeadersAdapter;

public class ContestItemAdapter extends ArrayAdapter<CommunityPageDTO>
        implements StickyListHeadersAdapter
{
    private final int vipViewResourceId;
    private final int normalViewResourceId;

    public ContestItemAdapter(Context context,
            int vipViewResourceId,
            int normalViewResourceId)
    {
        super(context, 0);
        this.vipViewResourceId = vipViewResourceId;
        this.normalViewResourceId = normalViewResourceId;
        DaggerUtils.inject(this);
    }

    public int getItemViewResId(int position)
    {
        CommunityPageDTO item = getItem(position);
        if (item instanceof ProviderCommunityPageDTO)
        {
            if (((ProviderCommunityPageDTO) item).providerDTO.vip)
            {
                return vipViewResourceId;
            }
            else
            {
                return normalViewResourceId;
            }
        }
        else if (item instanceof EmptyHeadLineDTO)
        {
            return R.layout.leaderboard_separator;
        }
        throw new IllegalArgumentException("Unhandled item " + getItem(position));
    }

    @SuppressWarnings("unchecked")
    @Override public View getView(int position, View convertView, ViewGroup viewGroup)
    {

        convertView = LayoutInflater.from(getContext()).inflate(getItemViewResId(position), viewGroup, false);
        if (convertView instanceof DTOView)
        {
            ((DTOView<CommunityPageDTO>) convertView).display(getItem(position));
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
}
