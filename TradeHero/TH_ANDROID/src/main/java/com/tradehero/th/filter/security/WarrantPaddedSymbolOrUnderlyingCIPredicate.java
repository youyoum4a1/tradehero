package com.tradehero.th.filter.security;

import com.tradehero.th.api.security.WarrantDTO;

public class WarrantPaddedSymbolOrUnderlyingCIPredicate<WarrantType extends WarrantDTO>
        extends SecurityCompactPaddedSymbolCIPredicate<WarrantType>
{
    //<editor-fold desc="Constructors">
    public WarrantPaddedSymbolOrUnderlyingCIPredicate()
    {
        super();
    }

    public WarrantPaddedSymbolOrUnderlyingCIPredicate(CharSequence pattern)
    {
        super(pattern);
    }
    //</editor-fold>

    @Override public boolean apply(WarrantType subject)
    {
        return subject != null && (super.apply(subject) ||
                this.caseInsensitiveMatches(subject.underlyingName, pattern));
    }
}
