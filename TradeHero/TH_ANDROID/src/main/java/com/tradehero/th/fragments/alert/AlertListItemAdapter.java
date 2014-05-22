package com.tradehero.th.fragments.alert;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import com.tradehero.common.persistence.DTOKeyIdList;
import com.tradehero.common.persistence.DTOListCacheAdapter;
import com.tradehero.th.R;
import com.tradehero.th.api.alert.AlertCompactDTO;
import com.tradehero.th.api.alert.AlertId;
import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.persistence.alert.AlertCompactCache;
import com.tradehero.th.persistence.alert.AlertCompactListCache;
import com.tradehero.th.utils.DaggerUtils;
import dagger.Lazy;
import java.util.Collections;
import java.util.Comparator;
import javax.inject.Inject;
import se.emilsjolander.stickylistheaders.StickyListHeadersAdapter;

public class AlertListItemAdapter extends DTOListCacheAdapter<AlertId, AlertItemView>
    implements StickyListHeadersAdapter
{
    private static final long HEADER_ID_INACTIVE = 0;
    private static final long HEADER_ID_ACTIVE = 1;

    @Inject protected Lazy<AlertCompactListCache> alertCompactListCache;
    @Inject protected Lazy<AlertCompactCache> alertCompactCache;

    @Inject protected CurrentUserId currentUserId;

    protected final Context context;
    protected final int alertResId;

    public AlertListItemAdapter(Context context, int alertResId)
    {
        super(context, alertResId);
        this.context = context;

        this.alertResId = alertResId;

        DaggerUtils.inject(this);
    }

    @Override public void notifyDataSetChanged()
    {
        DTOKeyIdList<AlertId> items = getItems();
        if (items != null)
        {
            Collections.sort(items, new Comparator<AlertId>()
            {
                @Override public int compare(AlertId lhs, AlertId rhs)
                {
                    AlertCompactDTO lhsItem = alertCompactCache.get().get(lhs);
                    AlertCompactDTO rhsItem = alertCompactCache.get().get(rhs);
                    if (lhsItem == rhsItem) return 0;
                    if (lhsItem == null)
                    {
                        return -1;
                    }
                    if (rhsItem == null)
                    {
                        return 1;
                    }

                    if (lhsItem.active == rhsItem.active) return 0;
                    if (!lhsItem.active) return 1;
                    else return -1;
                }
            });
        }
        super.notifyDataSetChanged();
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

    @Override public DTOKeyIdList<AlertId> getItems()
    {
        return alertCompactListCache.get().get(currentUserId.toUserBaseKey());
    }

    @Override protected void fineTune(int position, AlertId dto, AlertItemView dtoView)
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

        holder.text.setText(getHeaderId(position) == 1 ?
                getContext().getString(R.string.stock_alert_active) :getContext().getString(R.string.stock_alert_inactive_title)
        );
        return convertView;
    }

    @Override public long getHeaderId(int position)
    {
        AlertId alertId = (AlertId) getItem(position);
        AlertCompactDTO alertCompactDTO = alertCompactCache.get().get(alertId);
        if (alertCompactDTO!= null && alertCompactDTO.active)
        {
            return HEADER_ID_ACTIVE;
        }
        else
        {
            return HEADER_ID_INACTIVE;
        }

    }
}
