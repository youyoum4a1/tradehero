package com.tradehero.th.fragments.security;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import butterknife.OnItemClick;
import com.tradehero.th2.R;
import com.tradehero.th.api.security.SecurityId;
import com.tradehero.th.utils.DaggerUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class SecurityActionListLinear extends LinearLayout
{
    @InjectView(R.id.security_action_title2) protected TextView shareTitleView;
    @InjectView(R.id.security_action_cancel) protected View cancelView;
    @InjectView(R.id.security_action_list_sharing_items) protected ListView listViewActionOptions;

    protected ArrayAdapter<SecurityActionDTO> adapter;
    @Nullable protected OnActionMenuClickedListener menuClickedListener;

    //<editor-fold desc="Constructors">
    public SecurityActionListLinear(Context context)
    {
        super(context);
    }

    public SecurityActionListLinear(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public SecurityActionListLinear(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
    }
    //</editor-fold>

    @Override protected void onFinishInflate()
    {
        super.onFinishInflate();
        DaggerUtils.inject(this);
        ButterKnife.inject(this);
        adapter = new SecurityActionListAdapter(getContext());
    }

    @Override protected void onAttachedToWindow()
    {
        super.onAttachedToWindow();
        ButterKnife.inject(this);
        listViewActionOptions.setDividerHeight(1);
        listViewActionOptions.setAdapter(adapter);
    }

    @Override protected void onDetachedFromWindow()
    {
        listViewActionOptions.setAdapter(null);
        ButterKnife.reset(this);
        super.onDetachedFromWindow();
    }

    public void setSecurityIdToActOn(@NotNull SecurityId securityIdToActOn)
    {
        adapter.clear();
        adapter.add(new SecurityActionDTO(
                SecurityActionDTO.ACTION_ID_WATCHLIST,
                getContext().getString(R.string.watchlist_add_title),
                securityIdToActOn));
        adapter.add(new SecurityActionDTO(
                SecurityActionDTO.ACTION_ID_ALERT,
                getContext().getString(R.string.stock_alert_add_alert),
                securityIdToActOn));
        adapter.add(new SecurityActionDTO(
                SecurityActionDTO.ACTION_ID_TRADE,
                getContext().getString(R.string.buy_sell),
                securityIdToActOn));
        adapter.notifyDataSetChanged();
        shareTitleView.setText(String.format("%s:%s", securityIdToActOn.getExchange(), securityIdToActOn.getSecuritySymbol()));
    }

    public void setMenuClickedListener(@Nullable OnActionMenuClickedListener menuClickedListener)
    {
        this.menuClickedListener = menuClickedListener;
    }

    @OnClick(R.id.security_action_cancel)
    protected void onCancelClicked(@SuppressWarnings("UnusedParameters") View view)
    {
        OnActionMenuClickedListener listenerCopy = menuClickedListener;
        if (listenerCopy != null)
        {
            listenerCopy.onCancelClicked();
        }
    }

    @OnItemClick(R.id.security_action_list_sharing_items)
    protected void onShareOptionsItemClicked(AdapterView<?> parent, View view, int position, long id)
    {
        OnActionMenuClickedListener listenerCopy = menuClickedListener;
        if (listenerCopy != null)
        {
            SecurityActionDTO actionDTO = (SecurityActionDTO) parent.getItemAtPosition(position);
            switch (actionDTO.actionId)
            {
                case SecurityActionDTO.ACTION_ID_ALERT:
                    listenerCopy.onAddAlertRequested(actionDTO.securityToActOn);
                    break;
                case SecurityActionDTO.ACTION_ID_WATCHLIST:
                    listenerCopy.onAddToWatchlistRequested(actionDTO.securityToActOn);
                    break;
                case SecurityActionDTO.ACTION_ID_TRADE:
                    listenerCopy.onBuySellRequested(actionDTO.securityToActOn);
                    break;
                default:
                    throw new IllegalArgumentException("Unhandled type " + actionDTO.getClass().getCanonicalName());
            }
        }
    }

    protected static class SecurityActionListAdapter extends ArrayAdapter<SecurityActionDTO>
    {
        //<editor-fold desc="Constructors">
        public SecurityActionListAdapter(Context context)
        {
            super(context, R.layout.common_dialog_item_layout);
        }
        //</editor-fold>

        @Override public View getView(int position, View convertView, ViewGroup parent)
        {
            if (convertView == null)
            {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.common_dialog_item_layout, parent, false);
            }
            ((TextView) convertView.findViewById(R.id.popup_text)).setText(getItem(position).title);
            return convertView;
        }
    }

    public static interface OnActionMenuClickedListener
    {
        void onCancelClicked();
        void onAddToWatchlistRequested(@NotNull SecurityId securityId);
        void onAddAlertRequested(@NotNull SecurityId securityId);
        void onBuySellRequested(@NotNull SecurityId securityId);
    }
}
