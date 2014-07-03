package com.tradehero.th.models.market;

import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.tradehero.th.R;
import com.tradehero.th.api.market.Country;
import com.tradehero.th.api.market.Exchange;
import com.tradehero.th.api.market.ExchangeCompactDTO;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import timber.log.Timber;

public class ExchangeCompactSpinnerDTO extends ExchangeCompactDTO implements CharSequence
{
    public static final String ALL_EXCHANGES = "allExchanges";

    @NotNull private final Resources resources;
    @Nullable private Drawable flagDrawable;

    public static String getName(@NotNull Resources resources, @NotNull Bundle args)
    {
        return args.getString(BUNDLE_KEY_NAME, resources.getString(R.string.trending_filter_exchange_all));
    }

    //<editor-fold desc="Constructors">
    public ExchangeCompactSpinnerDTO(@NotNull Resources resources)
    {
        super(-1,
                ALL_EXCHANGES,
                Country.NONE.name(),
                0,
                null,
                false,
                true,
                false);
        this.resources = resources;
    }

    public ExchangeCompactSpinnerDTO(@NotNull Resources resources, @NotNull ExchangeCompactDTO exchangeDTO)
    {
        super(exchangeDTO);
        this.resources = resources;
    }

    public ExchangeCompactSpinnerDTO(@NotNull Resources resources, @NotNull Bundle bundle)
    {
        super(bundle);
        this.resources = resources;
        this.name = getName(resources, bundle);
    }
    //</editor-fold>

    @Nullable @JsonIgnore public String getApiName()
    {
        return name.equals(ALL_EXCHANGES) ? null : name;
    }

    @NotNull @JsonIgnore public String getUsableDisplayName()
    {
        return name.equals(ALL_EXCHANGES) ? resources.getString(R.string.trending_filter_exchange_all) : name;
    }

    @Nullable @Override public Exchange getExchangeByName()
    {
        if (name.equals(ALL_EXCHANGES))
        {
            return null;
        }
        return super.getExchangeByName();
    }

    @Override @NotNull public String toString()
    {
        String usableName = getUsableDisplayName();
        if (desc == null)
        {
            return usableName;
        }
        return resources.getString(R.string.trending_filter_exchange_drop_down, usableName, desc);
    }

    //<editor-fold desc="CharSequence">
    @Override public CharSequence subSequence(int start, int end)
    {
        return toString().subSequence(start, end);
    }

    @Override public char charAt(int index)
    {
        return toString().charAt(index);
    }

    @Override public int length()
    {
        return toString().length();
    }
    //</editor-fold>

    @Nullable public Drawable getFlagDrawable()
    {
        if (flagDrawable == null)
        {
            Integer flagResId = getFlagResId();
            if (flagResId != null)
            {
                try
                {
                    flagDrawable = resources.getDrawable(flagResId);
                }
                catch (OutOfMemoryError e)
                {
                    Timber.e(e, "Inflating flag for %s", name);
                }
            }
        }
        return flagDrawable;
    }

    @Override public int hashCode()
    {
        return name.hashCode();
    }

    @Override public boolean equals(Object other)
    {
        return other instanceof ExchangeCompactSpinnerDTO &&
                equals((ExchangeCompactSpinnerDTO) other);
    }

    protected boolean equals(@NotNull ExchangeCompactSpinnerDTO other)
    {
        return name.equals(other.name);
    }
}
