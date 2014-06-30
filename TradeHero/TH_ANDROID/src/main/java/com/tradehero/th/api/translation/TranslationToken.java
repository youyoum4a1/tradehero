package com.tradehero.th.api.translation;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.tradehero.common.persistence.DTO;
import com.tradehero.thm.R;
import com.tradehero.th.api.translation.bing.BingTranslationToken;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        defaultImpl = TranslationToken.class,
        property = "type"
)
@JsonSubTypes(
        @JsonSubTypes.Type(value = BingTranslationToken.class, name = BingTranslationToken.TOKEN_TYPE)
)
public class TranslationToken implements DTO
{
    //<editor-fold desc="Constructors">
    public TranslationToken()
    {
        super();
    }
    //</editor-fold>

    public boolean isValid()
    {
        throw new IllegalStateException("Needs to be implemented");
    }

    public int logoResId()
    {
        return R.drawable.default_image;
    }
}
