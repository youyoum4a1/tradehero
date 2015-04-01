package com.tradehero.th.api.trade;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.tradehero.common.api.PagedDTOKey;
import com.tradehero.th.api.security.SecurityId;

public class SecurityTradeDTOListKey extends SecurityId
        implements TradeDTOListKey, PagedDTOKey
{
    private static final String BUNDLE_KEY_PAGE = SecurityTradeDTOListKey.class.getName() + ".page";
    private static final String BUNDLE_KEY_PER_PAGE = SecurityTradeDTOListKey.class.getName() + ".perPage";

    @Nullable public final Integer page;
    @Nullable public final Integer perPage;

    //<editor-fold desc="Constructors">
    public SecurityTradeDTOListKey(
            @NonNull String exchange,
            @NonNull String securitySymbol,
            @Nullable Integer page,
            @Nullable Integer perPage)
    {
        super(exchange, securitySymbol);
        this.page = page;
        this.perPage = perPage;
    }

    public SecurityTradeDTOListKey(@NonNull Bundle args)
    {
        super(args);
        if (args.containsKey(BUNDLE_KEY_PAGE))
        {
            this.page = args.getInt(BUNDLE_KEY_PAGE);
        }
        else
        {
            this.page = null;
        }
        if (args.containsKey(BUNDLE_KEY_PER_PAGE))
        {
            this.perPage = args.getInt(BUNDLE_KEY_PER_PAGE);
        }
        else
        {
            this.perPage = null;
        }
    }
    //</editor-fold>

    @Nullable @Override public Integer getPage()
    {
        return page;
    }

    @Override public int hashCode()
    {
        return super.hashCode()
                ^ (page == null ? 0 : page.hashCode())
                ^ (perPage == null ? 0 : perPage.hashCode());
    }

    @Override protected boolean equalFields(@NonNull SecurityId other)
    {
        return super.equalFields(other)
                && other instanceof SecurityTradeDTOListKey
                && equalFields((SecurityTradeDTOListKey) other);
    }

    protected boolean equalFields(@NonNull SecurityTradeDTOListKey other)
    {
        return super.equalFields(other)
                && (page == null ? other.page == null : page.equals(other.page))
                && (perPage == null ? other.perPage == null : perPage.equals(other.perPage));
    }

    @Override protected void putParameters(Bundle args)
    {
        super.putParameters(args);
        if (page == null)
        {
            args.remove(BUNDLE_KEY_PAGE);
        }
        else
        {
            args.putInt(BUNDLE_KEY_PAGE, page);
        }
        if (perPage == null)
        {
            args.remove(BUNDLE_KEY_PER_PAGE);
        }
        else
        {
            args.putInt(BUNDLE_KEY_PER_PAGE, perPage);
        }
    }
}
