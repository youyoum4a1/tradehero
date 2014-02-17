package com.tradehero.th.fragments.trending.filter;

import android.os.Bundle;
import com.tradehero.th.api.market.ExchangeDTO;
import com.tradehero.th.api.security.key.TrendingSecurityListType;

/**
 * Created by xavier on 1/15/14.
 */
abstract public class TrendingFilterTypeDTO
{
    public static final String TAG = TrendingFilterTypeDTO.class.getSimpleName();
    public static final String BUNDLE_KEY_CLASS_TYPE = TrendingFilterTypeDTO.class.getName() + ".classType";
    public static final String BUNDLE_KEY_TITLE_RES_ID = TrendingFilterTypeDTO.class.getName() + ".titleResId";
    public static final String BUNDLE_KEY_TITLE_ICON_RES_ID = TrendingFilterTypeDTO.class.getName() + ".iconResId";
    public static final String BUNDLE_KEY_DESCRIPTION_RES_ID = TrendingFilterTypeDTO.class.getName() + ".descriptionResId";
    public static final String BUNDLE_KEY_EXCHANGE = TrendingFilterTypeDTO.class.getName() + ".exchange";

    public final int titleResId;
    public final int titleIconResId;
    public final int descriptionResId;

    public ExchangeDTO exchange;

    //<editor-fold desc="Constructors">
    public TrendingFilterTypeDTO(int titleResId, int titleIconResId, int descriptionResId)
    {
        this.titleResId = titleResId;
        this.titleIconResId = titleIconResId;
        this.descriptionResId = descriptionResId;
        this.exchange = new ExchangeDTO();
    }

    public TrendingFilterTypeDTO(int titleResId, int titleIconResId, int descriptionResId,
            ExchangeDTO exchangeDTO)
    {
        this.titleResId = titleResId;
        this.titleIconResId = titleIconResId;
        this.descriptionResId = descriptionResId;
        this.exchange = exchangeDTO;
    }

    public TrendingFilterTypeDTO(Bundle bundle)
    {
        this.titleResId = bundle.getInt(BUNDLE_KEY_TITLE_RES_ID);
        this.titleIconResId = bundle.getInt(BUNDLE_KEY_TITLE_ICON_RES_ID);
        this.descriptionResId = bundle.getInt(BUNDLE_KEY_DESCRIPTION_RES_ID);
        this.exchange = new ExchangeDTO(bundle.getBundle(BUNDLE_KEY_EXCHANGE));
    }
    //</editor-fold>

    abstract public TrendingFilterTypeDTO getPrevious();
    abstract public TrendingFilterTypeDTO getNext();
    abstract public TrendingSecurityListType getSecurityListType(String usableExchangeName, Integer page, Integer perPage);

    protected void putParameters(Bundle args)
    {
        args.putInt(BUNDLE_KEY_TITLE_RES_ID, this.titleResId);
        args.putInt(BUNDLE_KEY_TITLE_ICON_RES_ID, this.titleIconResId);
        args.putInt(BUNDLE_KEY_DESCRIPTION_RES_ID, this.descriptionResId);
        args.putBundle(BUNDLE_KEY_EXCHANGE, this.exchange.getArgs());
    }

    public Bundle getArgs()
    {
        Bundle args = new Bundle();
        putParameters(args);
        return args;
    }
}
