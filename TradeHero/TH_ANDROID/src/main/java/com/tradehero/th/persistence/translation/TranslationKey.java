package com.tradehero.th.persistence.translation;

import com.tradehero.common.persistence.DTOKey;

public class TranslationKey implements DTOKey
{
    public final String from;
    public final String to;
    public final int textHashCode;

    // It does not participate to the key, but is still necessary to achieve translation
    public String translatableText;

    //<editor-fold desc="Constructors">
    public TranslationKey(String from, String to, String translatableText)
    {
        this(from, to, translatableText.hashCode(), translatableText);
    }

    public TranslationKey(String from, String to, int textHashCode, String translatableText)
    {
        this.from = from;
        this.to = to;
        this.textHashCode = textHashCode;
        this.translatableText = translatableText;
    }
    //</editor-fold>

    @Override public int hashCode()
    {
        return from.hashCode() ^
                to.hashCode() ^
                textHashCode;
    }

    @Override public boolean equals(Object obj)
    {
        if (obj == this)
        {
            return true;
        }
        if (obj == null)
        {
            return false;
        }
        return equalClass(obj) && equalFields((TranslationKey) obj);
    }

    protected boolean equalClass(Object other)
    {
        return other.getClass().equals(getClass());
    }

    protected boolean equalFields(TranslationKey other)
    {
        return from.equals(other.from) &&
                to.equals(other.to) &&
                textHashCode == other.textHashCode;
    }
}
