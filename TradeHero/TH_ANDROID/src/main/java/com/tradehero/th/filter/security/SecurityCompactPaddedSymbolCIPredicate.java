package com.ayondo.academy.filter.security;

import com.ayondo.academy.api.security.SecurityCompactDTO;

public class SecurityCompactPaddedSymbolCIPredicate<SecurityCompactType extends SecurityCompactDTO>
        extends SecurityCompactSymbolCIPredicate<SecurityCompactType>
{
    //<editor-fold desc="Constructors">
    public SecurityCompactPaddedSymbolCIPredicate()
    {
        super();
    }

    public SecurityCompactPaddedSymbolCIPredicate(CharSequence pattern)
    {
        super(pattern);
    }
    //</editor-fold>

    public boolean caseInsensitiveMatches(String symbol, CharSequence pattern)
    {
        return super.caseInsensitiveMatches(symbol, ".*" + pattern + ".*");
    }
}
