package com.tradehero.th.api.market;

import android.support.annotation.Nullable;
import com.tradehero.th.R;

public enum FXLogo
{
    NONE(R.drawable.default_image, R.drawable.default_image),
    AUD_USD(R.drawable.square_au, R.drawable.square_us),
    GBP_USD(R.drawable.square_au, R.drawable.square_us),
    EUR_USD(R.drawable.square_eu, R.drawable.square_us),
    XAU_USD(R.drawable.square_us, R.drawable.square_us),
    NZD_USD(R.drawable.square_nz, R.drawable.square_us),
    XAG_USD(R.drawable.square_us, R.drawable.square_us),
    USD_CAD(R.drawable.square_us, R.drawable.square_ca),
    USD_CHF(R.drawable.square_us, R.drawable.square_ch),
    AUD_JPY(R.drawable.square_au, R.drawable.square_jp),
    GBP_JPY(R.drawable.square_au, R.drawable.square_jp),
    EUR_JPY(R.drawable.square_eu, R.drawable.square_jp),
    USD_JPY(R.drawable.square_us, R.drawable.square_jp),
    ;

    public final int firstLogo;
    public final int secondLogo;

    private FXLogo(int firstLogo, int secondLogo)
    {
        this.firstLogo = firstLogo;
        this.secondLogo = secondLogo;
    }

    public static int getFXFirstLogo(@Nullable String symbol)
    {
        if (symbol == null)
        {
            return R.drawable.default_image;
        }
        try
        {
            return FXLogo.valueOf(symbol).firstLogo;
        } catch (IllegalArgumentException ex)
        {
            return R.drawable.default_image;
        }
    }

    public static int getFXSecondLogo(@Nullable String symbol)
    {
        if (symbol == null)
        {
            return R.drawable.default_image;
        }
        try
        {
            return FXLogo.valueOf(symbol).secondLogo;
        } catch (IllegalArgumentException ex)
        {
            return R.drawable.default_image;
        }
    }
}
