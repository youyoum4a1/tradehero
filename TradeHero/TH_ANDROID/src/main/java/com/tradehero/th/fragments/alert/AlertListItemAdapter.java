package com.tradehero.th.fragments.alert;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import com.tradehero.common.persistence.DTOKeyIdList;
import com.tradehero.common.persistence.DTOListCacheAdapter;
import com.tradehero.th.R;
import com.tradehero.th.api.alert.AlertCompactDTO;
import com.tradehero.th.api.alert.AlertId;
import com.tradehero.th.api.users.CurrentUserBaseKeyHolder;
import com.tradehero.th.persistence.alert.AlertCompactCache;
import com.tradehero.th.persistence.alert.AlertCompactListCache;
import com.tradehero.th.utils.DaggerUtils;
import dagger.Lazy;
import javax.inject.Inject;
import se.emilsjolander.stickylistheaders.StickyListHeadersAdapter;

/** Created with IntelliJ IDEA. User: xavier Date: 10/14/13 Time: 4:12 PM To change this template use File | Settings | File Templates. */
public class AlertListItemAdapter extends DTOListCacheAdapter<AlertId, AlertItemView>
    implements StickyListHeadersAdapter
{
    public static final String TAG = AlertListItemAdapter.class.getName();
    private static final long HEADER_ID_INACTIVE = 0;
    private static final long HEADER_ID_ACTIVE = 1;

    @Inject protected Lazy<AlertCompactListCache> alertCompactListCache;
    @Inject protected Lazy<AlertCompactCache> alertCompactCache;

    @Inject protected CurrentUserBaseKeyHolder currentUserBaseKeyHolder;

    protected final Context context;
    protected final int alertResId;

    public AlertListItemAdapter(Context context, int alertResId)
    {
        super(context, alertResId);
        this.context = context;

        this.alertResId = alertResId;

        DaggerUtils.inject(this);
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
        return alertCompactListCache.get().get(currentUserBaseKeyHolder.getCurrentUserBaseKey());
    }

    @Override protected void fineTune(int position, AlertId dto, AlertItemView dtoView)
    {

    }

    @Override public View getHeaderView(int position, View convertView, ViewGroup parent)
    {
        TextHolder holder = null;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.alert_management_title, parent, false);
            holder = new TextHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (TextHolder) convertView.getTag();
        }

        holder.text.setText(getHeaderId(position) == 1 ?
                getContext().getString(R.string.stock_alerts_active) :getContext().getString(R.string.stock_alerts_inactive_title)
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
