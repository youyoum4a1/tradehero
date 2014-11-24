package com.tradehero.th.fragments.security;

import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import com.tradehero.common.widget.dialog.THDialog;
import com.tradehero.th.R;
import com.tradehero.th.api.security.SecurityId;

public class SecurityActionDialogFactory
{
    /**
     * You can access the view from the dialog with
     * dialog.getWindow().getDecorView().findViewById(android.R.id.content);
     * @param context
     * @param securityId
     * @param menuClickedListener
     * @return
     */
    public Dialog createSecurityActionDialog(@NonNull Context context,
            @NonNull SecurityId securityId,
            @Nullable SecurityActionListLinear.OnActionMenuClickedListener menuClickedListener)
    {
        SecurityActionListLinear contentView = (SecurityActionListLinear) LayoutInflater.from(context)
                .inflate(R.layout.security_action_list_dialog_layout, null);
        contentView.setSecurityIdToActOn(securityId);
        contentView.setMenuClickedListener(menuClickedListener);
        return THDialog.showUpDialog(context, contentView);
    }
}
