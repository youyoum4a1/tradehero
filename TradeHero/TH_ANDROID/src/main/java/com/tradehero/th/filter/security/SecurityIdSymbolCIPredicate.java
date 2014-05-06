package com.tradehero.th.filter.security;

import com.tradehero.common.widget.filter.CharSequencePredicate;
import com.tradehero.th.api.security.SecurityId;
import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton public class SecurityIdSymbolCIPredicate implements CharSequencePredicate<SecurityId>
{
    public CharSequence pattern;

    //<editor-fold desc="Constructors">
    @Inject public SecurityIdSymbolCIPredicate()
    {
    }

    public SecurityIdSymbolCIPredicate(CharSequence pattern)
    {
        this.pattern = pattern;
    }
    //</editor-fold>

    @Override public CharSequence getCharSequence()
    {
        return pattern;
    }

    @Override public void setCharSequence(CharSequence charSequence)
    {
        this.pattern = charSequence;
    }

    public boolean caseInsensitiveMatches(String symbol, CharSequence pattern)
    {
        return symbol != null && symbol.toLowerCase().matches(pattern.toString().toLowerCase());
    }

    @Override public boolean apply(SecurityId subject)
    {
        return subject != null && this.caseInsensitiveMatches(subject.securitySymbol, pattern);
    }
}
