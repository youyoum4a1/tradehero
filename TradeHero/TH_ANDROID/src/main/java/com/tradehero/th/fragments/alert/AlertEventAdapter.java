package com.tradehero.th.fragments.alert;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.tradehero.th.R;
import com.tradehero.th.adapters.ArrayDTOAdapter;
import com.tradehero.th.api.alert.AlertEventDTO;
import se.emilsjolander.stickylistheaders.StickyListHeadersAdapter;


public class AlertEventAdapter extends ArrayDTOAdapter<AlertEventDTO, AlertEventItemView>
        implements StickyListHeadersAdapter
{
    public AlertEventAdapter(Context context, LayoutInflater inflater, int layoutResourceId)
    {
        super(context, inflater, layoutResourceId);
    }

    @Override protected void fineTune(int position, AlertEventDTO dto, AlertEventItemView dtoView)
    {

    }

    @Override public View getHeaderView(int position, View convertView, ViewGroup parent)
    {
        TextHolder holder = null;
        if (convertView == null)
        {
            convertView = inflater.inflate(R.layout.alert_management_title, parent, false);
            holder = new TextHolder(convertView);
            convertView.setTag(holder);
        }
        else
        {
            holder = (TextHolder) convertView.getTag();
        }
        holder.text.setText(R.string.stock_alert_notification_received);
        return convertView;
    }

    @Override public long getHeaderId(int position)
    {
        return 1;
    }

    @Override public boolean areAllItemsEnabled()
    {
        return false;
    }
}
