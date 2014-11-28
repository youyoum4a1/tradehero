package com.tradehero.th.fragments.discovery;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.tradehero.th.R;
import com.tradehero.th.adapters.ArrayDTOAdapter;
import com.tradehero.th.api.games.MiniGameDefDTO;
import com.tradehero.th.widget.TextHolder;
import java.util.Collections;
import java.util.List;
import se.emilsjolander.stickylistheaders.StickyListHeadersAdapter;

public class DiscoveryGameAdapter extends ArrayDTOAdapter<MiniGameDefDTO, MiniGameDefItemView>
        implements StickyListHeadersAdapter
{
    public DiscoveryGameAdapter(@NonNull Context context, @LayoutRes int layoutResourceId)
    {
        super(context, layoutResourceId);
    }

    @Override public void setItems(@NonNull List<MiniGameDefDTO> miniGameDefDTOs)
    {
        Collections.sort(miniGameDefDTOs, (lhs, rhs) -> {
            if (rhs == lhs) return 0;
            if (lhs == null) return -1;
            if (rhs == null) return 1;
            if (lhs.comingSoon && rhs.comingSoon) return 0;
            if (lhs.comingSoon) return 1;
            else return -1;
        });
        super.setItems(miniGameDefDTOs);
    }

    @Override public View getHeaderView(int position, View convertView, ViewGroup parent)
    {
        if (getHeaderId(position) >= 0)
        {
            TextHolder holder;
            if (convertView == null)
            {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.alert_management_title, parent, false);
                holder = new TextHolder(convertView);
                convertView.setTag(holder);
            }
            else
            {
                holder = (TextHolder) convertView.getTag();
            }
            holder.text.setText(R.string.coming_soon);
            return convertView;
        }
        else
        {
            return new View(getContext());
        }
    }

    @Override public long getHeaderId(int position)
    {
        MiniGameDefDTO currentItem = (MiniGameDefDTO) getItem(position);
        for (int i = position - 1; i >= 0; --i)
        {
            MiniGameDefDTO item = (MiniGameDefDTO) getItem(i);
            if (item.comingSoon != currentItem.comingSoon)
            {
                return i;
            }
        }
        return -1;
    }

    @Override public boolean areAllItemsEnabled()
    {
        return false;
    }

    @Override public boolean isEnabled(int position)
    {
        MiniGameDefDTO item = (MiniGameDefDTO) getItem(position);
        return !item.comingSoon;
    }
}
