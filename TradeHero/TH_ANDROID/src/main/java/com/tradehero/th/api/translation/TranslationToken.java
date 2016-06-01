package com.ayondo.academy.api.translation;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.tradehero.common.persistence.DTO;
import com.ayondo.academy.R;
import com.ayondo.academy.api.translation.bing.BingTranslationToken;

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
    public boolean isValid()
    {
        throw new IllegalStateException("Needs to be implemented");
    }

    public int logoResId()
    {
        return R.drawable.default_image;
    }
}
