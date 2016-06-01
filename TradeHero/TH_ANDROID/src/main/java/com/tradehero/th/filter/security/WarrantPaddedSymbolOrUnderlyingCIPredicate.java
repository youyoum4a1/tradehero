package com.ayondo.academy.filter.security;

import com.ayondo.academy.api.security.compact.WarrantDTO;

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
