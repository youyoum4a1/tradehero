package com.androidth.general.fragments.alert;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.Bind;
import com.androidth.general.R;
import com.androidth.general.adapters.ViewDTOSetAdapter;
import com.androidth.general.api.users.CurrentUserId;
import com.androidth.general.inject.HierarchyInjector;
import java.util.Comparator;
import se.emilsjolander.stickylistheaders.StickyListHeadersAdapter;

public class AlertListItemAdapter extends ViewDTOSetAdapter<AlertItemView.DTO, AlertItemView>
        implements StickyListHeadersAdapter
{
    private static final long HEADER_ID_INACTIVE = 0;
    private static final long HEADER_ID_ACTIVE = 1;

    @NonNull CurrentUserId currentUserId;
    @LayoutRes protected final int alertResId;

    //<editor-fold desc="Constructors">
    public AlertListItemAdapter(
            @NonNull Context context,
            @NonNull CurrentUserId currentUserId,
            @LayoutRes int alertResId)
    {
        super(context,
                new Comparator<AlertItemView.DTO>()
                {
                    @Override public int compare(AlertItemView.DTO lhs, AlertItemView.DTO rhs)
                    {
                        if (lhs == rhs
                                || lhs.alertCompactDTO == rhs.alertCompactDTO
                                || lhs.alertCompactDTO.id == rhs.alertCompactDTO.id)
                        {
                            return 0;
                        }

                        if (lhs.alertCompactDTO.security == rhs.alertCompactDTO.security)
                        {
                            return 0;
                        }

                        if (lhs.alertCompactDTO.active && !rhs.alertCompactDTO.active)
                        {
                            return -1;
                        }
                        else if (!lhs.alertCompactDTO.active && rhs.alertCompactDTO.active)
                        {
                            return 1;
                        }

                        if (lhs.alertCompactDTO.security != null && rhs.alertCompactDTO.security != null)
                        {
                            return lhs.alertCompactDTO.security.symbol.compareTo(rhs.alertCompactDTO.security.symbol);
                        }

                        if (lhs.alertCompactDTO.security == null)
                        {
                            return -1;
                        }
                        return 1;
                    }
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
        TextHolder holder;
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
        return getItem(position).alertCompactDTO.active ? HEADER_ID_ACTIVE : HEADER_ID_INACTIVE;
    }

    public static class TextHolder
    {
        @Bind(R.id.title) public TextView text;

        public TextHolder(View view)
        {
            ButterKnife.bind(this, view);
        }
    }
}
