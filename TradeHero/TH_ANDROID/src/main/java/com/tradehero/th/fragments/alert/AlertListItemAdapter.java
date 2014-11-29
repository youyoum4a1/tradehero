package com.tradehero.th.fragments.alert;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.tradehero.th.R;
import com.tradehero.th.adapters.ViewDTOSetAdapter;
import com.tradehero.th.api.alert.AlertCompactDTO;
import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.inject.HierarchyInjector;
import com.tradehero.th.widget.TextHolder;
import se.emilsjolander.stickylistheaders.StickyListHeadersAdapter;

public class AlertListItemAdapter extends ViewDTOSetAdapter<AlertCompactDTO, AlertItemView>
        implements StickyListHeadersAdapter
{
    private static final long HEADER_ID_INACTIVE = 0;
    private static final long HEADER_ID_ACTIVE = 1;

    @NonNull CurrentUserId currentUserId;
    protected final int alertResId;

    //<editor-fold desc="Constructors">
    public AlertListItemAdapter(@NonNull Context context, @NonNull CurrentUserId currentUserId, int alertResId)
    {
        super(context,
                (lhs, rhs) -> {
                    if (lhs == rhs)
                    {
                        return 0;
                    }

                    if (lhs.active && !rhs.active)
                    {
                        return -1;
                    }
                    if (!lhs.active && rhs.active)
                    {
                        return 1;
                    }

                    if (lhs.security == rhs.security)
                    {
                        return 0;
                    }
                    if (lhs.security != null && rhs.security != null)
                    {
                        return lhs.security.symbol.compareTo(rhs.security.symbol);
                    }

                    if (lhs.security == null)
                    {
                        return -1;
                    }
                    return 1;
                });
        this.currentUserId = currentUserId;
        this.alertResId = alertResId;

        HierarchyInjector.inject(context, this);
    }
    //</editor-fold>

    @Override @LayoutRes protected int getViewResId(int position)
    {
        return alertResId;
    }

    @Override public boolean hasStableIds()
    {
        return true;
    }

    @Override public View getHeaderView(int position, View convertView, ViewGroup parent)
    {
        TextHolder holder = null;
        if (convertView == null)
        {
            convertView = LayoutInflater.from(context).inflate(R.layout.alert_management_title, parent, false);
            holder = new TextHolder(convertView);
            convertView.setTag(holder);
        }
        else
        {
            holder = (TextHolder) convertView.getTag();
        }

        holder.text.setText(getHeaderId(position) == 1 ?
                        context.getString(R.string.stock_alert_active) :
                        context.getString(R.string.stock_alert_inactive_title)
        );
        return convertView;
    }

    @Override public long getHeaderId(int position)
    {
        return getItem(position).active ? HEADER_ID_ACTIVE : HEADER_ID_INACTIVE;
    }
}
