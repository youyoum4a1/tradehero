package com.ayondo.academy.models.market;

import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.ayondo.academy.R;
import com.ayondo.academy.api.market.Country;
import com.ayondo.academy.api.market.Exchange;
import com.ayondo.academy.api.market.ExchangeCompactDTO;
import timber.log.Timber;

public class ExchangeCompactSpinnerDTO extends ExchangeCompactDTO implements CharSequence
{
    public String allExchanges = "All";

    @NonNull private final Resources resources;
    @Nullable private Drawable flagDrawable;

    public static String getName(@NonNull Resources resources, @NonNull Bundle args)
    {
        return args.getString(BUNDLE_KEY_NAME, resources.getString(R.string.trending_filter_exchange_all));
    }

    //<editor-fold desc="Constructors">
    public ExchangeCompactSpinnerDTO(@NonNull Resources resources)
    {
        super(-1,
                resources.getString(R.string.trending_filter_exchange_all),
                Country.NONE.name(),
                0,
                null,
                false,
                true,
                false);
        this.allExchanges = resources.getString(R.string.trending_filter_exchange_all);
        this.resources = resources;
    }

    public ExchangeCompactSpinnerDTO(@NonNull Resources resources, @NonNull ExchangeCompactDTO exchangeDTO)
    {
        super(exchangeDTO);
        this.resources = resources;
    }

    public ExchangeCompactSpinnerDTO(@NonNull Resources resources, @NonNull Bundle bundle)
    {
        super(bundle);
        this.resources = resources;
        this.name = getName(resources, bundle);
    }
    //</editor-fold>

    @Nullable @JsonIgnore public String getApiName()
    {
        return name.equals(allExchanges) ? null : name;
    }

    @NonNull @JsonIgnore public String getUsableDisplayName()
    {
        return name.equals(allExchanges) ? resources.getString(R.string.trending_filter_exchange_all) : name;
    }

    @Nullable @Override public Exchange getExchangeByName()
    {
        if (name.equals(allExchanges))
        {
            return null;
        }
        return super.getExchangeByName();
    }

    @Override @NonNull public String toString()
    {
        String usableName = getUsableDisplayName();
        if (desc == null)
        {
            return usableName;
        }
        return resources.getString(R.string.trending_filter_exchange_drop_down, usableName, desc);
    }

    public String getFullName()
    {
        if (desc == null)
        {
            return getUsableDisplayName();
        }
        return desc;
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

    protected boolean equals(@NonNull ExchangeCompactSpinnerDTO other)
    {
        return name.equals(other.name);
    }
}
