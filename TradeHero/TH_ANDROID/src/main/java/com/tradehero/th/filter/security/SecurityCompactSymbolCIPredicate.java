package com.tradehero.th.filter.security;

import com.tradehero.common.widget.filter.CharSequencePredicate;
import com.tradehero.th.api.security.SecurityCompactDTO;

/**
 * Created by xavier on 1/24/14.
 */
public class SecurityCompactSymbolCIPredicate<SecurityCompactType extends SecurityCompactDTO>
        implements CharSequencePredicate<SecurityCompactType>
{
    public static final String TAG = SecurityCompactSymbolCIPredicate.class.getSimpleName();

    public CharSequence pattern;

    //<editor-fold desc="Constructors">
    public SecurityCompactSymbolCIPredicate()
    {
    }

    public SecurityCompactSymbolCIPredicate(CharSequence pattern)
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

    @Override public boolean apply(SecurityCompactType subject)
    {
        return subject != null && this.caseInsensitiveMatches(subject.symbol, pattern);
    }
}
