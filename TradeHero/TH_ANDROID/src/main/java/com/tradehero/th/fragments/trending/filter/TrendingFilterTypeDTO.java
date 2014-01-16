package com.tradehero.th.fragments.trending.filter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import com.tradehero.common.utils.THLog;
import com.tradehero.th.R;
import com.tradehero.th.api.market.ExchangeDTO;
import com.tradehero.th.api.market.ExchangeStringId;
import com.tradehero.th.api.security.TrendingBasicSecurityListType;
import com.tradehero.th.api.security.TrendingSecurityListType;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by xavier on 1/15/14.
 */
abstract public class TrendingFilterTypeDTO
{
    public static final String TAG = TrendingFilterTypeDTO.class.getSimpleName();

    public final boolean hasPreviousButton;
    public final boolean hasNextButton;
    public final int titleResId;
    public final int titleIconResId;
    public final int descriptionResId;

    public ExchangeDTO exchange;

    public TrendingFilterTypeDTO(boolean hasPreviousButton, boolean hasNextButton, int titleResId, int titleIconResId, int descriptionResId)
    {
        this.hasPreviousButton = hasPreviousButton;
        this.hasNextButton = hasNextButton;
        this.titleResId = titleResId;
        this.titleIconResId = titleIconResId;
        this.descriptionResId = descriptionResId;
    }

    public TrendingFilterTypeDTO(boolean hasPreviousButton, boolean hasNextButton, int titleResId, int titleIconResId, int descriptionResId, ExchangeDTO exchangeDTO)
    {
        this.hasPreviousButton = hasPreviousButton;
        this.hasNextButton = hasNextButton;
        this.titleResId = titleResId;
        this.titleIconResId = titleIconResId;
        this.descriptionResId = descriptionResId;
        this.exchange = exchangeDTO;
    }

    abstract public TrendingFilterTypeDTO getPrevious();
    abstract public TrendingFilterTypeDTO getNext();
    abstract public TrendingSecurityListType getSecurityListType(String usableExchangeName, Integer page, Integer perPage);
}
