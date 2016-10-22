package com.androidth.general.fragments.security;

import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import com.androidth.general.R;
import com.androidth.general.api.security.WarrantType;

public enum WarrantTabType
{
    ALL(null, R.string.warrants_all),
    CALL_ONLY(WarrantType.CALL, R.string.warrant_type_call_only),
    PUT_ONLY(WarrantType.PUT, R.string.warrant_type_put_only),
    ;

    @Nullable public final WarrantType warrantType;
    @StringRes public final int title;

    //<editor-fold desc="Constructors">
    WarrantTabType(@Nullable WarrantType warrantType, @StringRes int title)
    {
        this.warrantType = warrantType;
        this.title = title;
    }
    //</editor-fold>
}
