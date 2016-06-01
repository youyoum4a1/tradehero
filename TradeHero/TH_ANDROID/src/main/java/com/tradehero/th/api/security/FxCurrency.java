package com.ayondo.academy.api.security;

import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import com.ayondo.academy.R;
import timber.log.Timber;

public enum FxCurrency
{
    AUD(R.drawable.square_au),
    CAD(R.drawable.square_ca),
    CHF(R.drawable.square_ch),
    EUR(R.drawable.square_eu),
    GBP(R.drawable.square_gb),
    JPY(R.drawable.square_jp),
    NZD(R.drawable.square_nz),
    USD(R.drawable.square_us),
    XAG(R.drawable.icn_fx_silver),
    XAU(R.drawable.icn_fx_gold),
    UNKNOWN(R.drawable.icn_fx_unknown),
    ;

    @DrawableRes public final int flag;

    //<editor-fold desc="Constructors">
    FxCurrency(@DrawableRes int flag)
    {
        this.flag = flag;
    }
    //</editor-fold>

    @NonNull public static FxCurrency create(@NonNull String name)
    {
        for (FxCurrency currency : FxCurrency.values())
        {
            if (currency.name().equals(name))
            {
                return currency;
            }
        }
        Timber.e(new IllegalArgumentException(), "Unknown currency name %s", name);
        return UNKNOWN;
    }
}
