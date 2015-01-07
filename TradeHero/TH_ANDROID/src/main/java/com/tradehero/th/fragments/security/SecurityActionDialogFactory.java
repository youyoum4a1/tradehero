package com.tradehero.th.fragments.security;

import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Pair;
import android.view.LayoutInflater;
import com.tradehero.common.widget.dialog.THDialog;
import com.tradehero.th.R;
import com.tradehero.th.api.security.SecurityCompactDTO;
import javax.inject.Inject;

public class SecurityActionDialogFactory
{
    //<editor-fold desc="Constructors">
    @Inject public SecurityActionDialogFactory()
    {
    }
    //</editor-fold>

    @NonNull public Pair<Dialog, SecurityActionListLinear> createSecurityActionDialog(
            @NonNull Context context,
            @NonNull SecurityCompactDTO securityCompactDTO)
    {
        SecurityActionListLinear contentView = (SecurityActionListLinear) LayoutInflater.from(context)
                .inflate(R.layout.security_action_list_dialog_layout, null);
        contentView.setSecurityToActOn(securityCompactDTO);
        return Pair.create(THDialog.showUpDialog(context, contentView), contentView);
    }
}
