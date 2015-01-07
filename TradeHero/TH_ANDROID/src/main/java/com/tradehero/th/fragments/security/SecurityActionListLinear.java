package com.tradehero.th.fragments.security;

import android.content.Context;
import android.support.annotation.NonNull;
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
import com.tradehero.th.R;
import com.tradehero.th.api.security.SecurityCompactDTO;
import com.tradehero.th.api.security.SecurityCompactDTOUtil;
import com.tradehero.th.api.security.SecurityId;
import com.tradehero.th.api.security.compact.FxSecurityCompactDTO;
import com.tradehero.th.inject.HierarchyInjector;
import javax.inject.Inject;
import rx.Observable;
import rx.subjects.BehaviorSubject;

public class SecurityActionListLinear extends LinearLayout
{
    @Inject SecurityCompactDTOUtil securityCompactDTOUtil;

    @InjectView(R.id.security_action_title2) protected TextView shareTitleView;
    @InjectView(R.id.security_action_list_sharing_items) protected ListView listViewActionOptions;

    protected SecurityCompactDTO securityCompactDTO;
    protected ArrayAdapter<SecurityActionDTO> adapter;
    @NonNull protected BehaviorSubject<MenuAction> menuActionBehaviorSubject;

    //<editor-fold desc="Constructors">
    public SecurityActionListLinear(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        HierarchyInjector.inject(this);
        menuActionBehaviorSubject = BehaviorSubject.create();
    }
    //</editor-fold>

    @Override protected void onFinishInflate()
    {
        super.onFinishInflate();
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

    public void setSecurityToActOn(@NonNull SecurityCompactDTO securityCompact)
    {
        this.securityCompactDTO = securityCompact;
        SecurityId securityIdToActOn = securityCompact.getSecurityId();
        adapter.clear();

        if (!(securityCompact instanceof FxSecurityCompactDTO))
        {
            adapter.add(new SecurityActionDTO(
                    SecurityActionDTO.ACTION_ID_WATCHLIST,
                    getContext().getString(R.string.watchlist_add_title),
                    securityIdToActOn));
            adapter.add(new SecurityActionDTO(
                    SecurityActionDTO.ACTION_ID_ALERT,
                    getContext().getString(R.string.stock_alert_add_alert),
                    securityIdToActOn));
        }
        adapter.add(new SecurityActionDTO(
                SecurityActionDTO.ACTION_ID_TRADE,
                getContext().getString(R.string.buy_sell),
                securityIdToActOn));

        adapter.notifyDataSetChanged();
        shareTitleView.setText(securityCompactDTOUtil.getShortSymbol(securityCompactDTO));
    }

    @SuppressWarnings("UnusedDeclaration")
    @OnClick(R.id.security_action_cancel)
    protected void onCancelClicked(@SuppressWarnings("UnusedParameters") View view)
    {
        //noinspection ConstantConditions
        sendAndComplete(new MenuAction(MenuActionType.CANCEL, null));
    }

    @SuppressWarnings("UnusedDeclaration")
    @OnItemClick(R.id.security_action_list_sharing_items)
    protected void onShareOptionsItemClicked(AdapterView<?> parent, View view, int position, long id)
    {
        SecurityActionDTO actionDTO = (SecurityActionDTO) parent.getItemAtPosition(position);
        switch (actionDTO.actionId)
        {
            case SecurityActionDTO.ACTION_ID_ALERT:
                sendAndComplete(new MenuAction(MenuActionType.ADD_ALERT, securityCompactDTO));
                break;
            case SecurityActionDTO.ACTION_ID_WATCHLIST:
                sendAndComplete(new MenuAction(MenuActionType.ADD_TO_WATCHLIST, securityCompactDTO));
                break;
            case SecurityActionDTO.ACTION_ID_TRADE:
                sendAndComplete(new MenuAction(MenuActionType.BUY_SELL, securityCompactDTO));
                break;
            default:
                throw new IllegalArgumentException("Unhandled type " + actionDTO.getClass().getCanonicalName());
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

    @NonNull public Observable<MenuAction> getMenuActionObservable()
    {
        return menuActionBehaviorSubject.asObservable();
    }

    protected void sendAndComplete(@NonNull MenuAction menuAction)
    {
        menuActionBehaviorSubject.onNext(menuAction);
        menuActionBehaviorSubject.onCompleted();
    }

    public static enum MenuActionType
    {
        CANCEL, ADD_TO_WATCHLIST, ADD_ALERT, BUY_SELL
    }

    public static class MenuAction
    {
        @NonNull public final MenuActionType actionType;
        @NonNull public final SecurityCompactDTO securityCompactDTO;

        public MenuAction(@NonNull MenuActionType actionType, @NonNull SecurityCompactDTO securityCompactDTO)
        {
            this.actionType = actionType;
            this.securityCompactDTO = securityCompactDTO;
        }
    }
}
