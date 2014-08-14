package com.tradehero.th.fragments.security;

import com.tradehero.th.api.security.SecurityId;
import org.jetbrains.annotations.NotNull;

class SecurityActionDTO
{
    // Ugly way because of 65k methods limit
    static final int ACTION_ID_WATCHLIST = 0;
    static final int ACTION_ID_ALERT = 1;
    static final int ACTION_ID_TRADE = 2;

    final int actionId;
    @NotNull final String title;
    @NotNull final SecurityId securityToActOn;

    //<editor-fold desc="Constructors">
    SecurityActionDTO(int actionId, @NotNull String title, @NotNull SecurityId securityToActOn)
    {
        this.actionId = actionId;
        this.title = title;
        this.securityToActOn = securityToActOn;
    }
    //</editor-fold>
}
