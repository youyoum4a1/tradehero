package com.tradehero.th.fragments.security;

import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Pair;
import android.view.LayoutInflater;
import com.tradehero.common.widget.dialog.THDialog;
import com.tradehero.th.R;
import com.tradehero.th.api.security.SecurityCompactDTO;

public class SecurityActionDialogFactory
{
    @NonNull public static Pair<Dialog, SecurityActionListLinear> createSecurityActionDialog(
            @NonNull Context context,
            @NonNull SecurityCompactDTO securityCompactDTO)
    {
        SecurityActionListLinear contentView = (SecurityActionListLinear) LayoutInflater.from(context)
                .inflate(R.layout.security_action_list_dialog_layout, null);
        contentView.setSecurityToActOn(securityCompactDTO);
        return Pair.create(THDialog.showUpDialog(context, contentView), contentView);
    }
}
