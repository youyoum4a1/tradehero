package com.tradehero.th.filter.security;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton public class SecurityIdPaddedSymbolCIPredicate extends SecurityIdSymbolCIPredicate
{
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
