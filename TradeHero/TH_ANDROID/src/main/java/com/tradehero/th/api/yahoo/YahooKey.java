package com.tradehero.th.api.yahoo;

import android.os.Bundle;
import com.tradehero.common.persistence.AbstractStringDTOKey;
import com.tradehero.common.persistence.DTOKey;

/**
 * Created by julien on 14/10/13
 */

public class YahooKey extends AbstractStringDTOKey
{
    public final static String TAG = YahooKey.class.getName();
    public final static String BUNDLE_KEY_YAHOO_SYMBOL = YahooKey.class.getName() + ".yahooSymbol";

    public YahooKey(final String yahooSymbol)
    {
        super(yahooSymbol);
    }

    public YahooKey(Bundle args)
    {
        super(args);
    }

    @Override
    public String getBundleKey()
    {
        return BUNDLE_KEY_YAHOO_SYMBOL;
    }


    @Override public String toString()
    {
        return String.format("[%s key=%d]", TAG, key);
    }
}
