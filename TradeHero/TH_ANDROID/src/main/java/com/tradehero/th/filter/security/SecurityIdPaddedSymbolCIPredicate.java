package com.tradehero.th.filter.security;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Created by xavier on 1/24/14.
 */
@Singleton public class SecurityIdPaddedSymbolCIPredicate extends SecurityIdSymbolCIPredicate
{
    public static final String TAG = SecurityIdPaddedSymbolCIPredicate.class.getSimpleName();

    //<editor-fold desc="Constructors">
    @Inject public SecurityIdPaddedSymbolCIPredicate()
    {
        super();
    }

    public SecurityIdPaddedSymbolCIPredicate(CharSequence pattern)
    {
        super(pattern);
    }
    //</editor-fold>

    @Override public boolean caseInsensitiveMatches(String symbol, CharSequence pattern)
    {
        return pattern == null || super.caseInsensitiveMatches(symbol, ".*" + pattern.toString() + ".*");
    }
}
