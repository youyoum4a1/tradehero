package com.tradehero.th.persistence.translation;

import android.text.TextUtils;
import com.tradehero.th.api.discussion.AbstractDiscussionCompactDTO;
import com.tradehero.th.api.discussion.AbstractDiscussionDTO;
import com.tradehero.th.api.news.NewsItemCompactDTO;
import javax.inject.Inject;

public class TranslationKeyFactory
{
    @Inject public TranslationKeyFactory()
    {
    }

    public TranslationKey createFrom(AbstractDiscussionCompactDTO fromDiscussion, String toLanguage)
    {
        if (fromDiscussion == null)
        {
            return null;
        }
        return new TranslationKey(
                fromDiscussion.langCode,
                toLanguage,
                getTranslatableText(fromDiscussion));
    }

    protected String getTranslatableText(AbstractDiscussionCompactDTO fromDiscussion)
    {
        if (fromDiscussion instanceof NewsItemCompactDTO)
        {
            return String.format("%1$s\n%2$s",
                    ((NewsItemCompactDTO) fromDiscussion).title,
                    ((NewsItemCompactDTO) fromDiscussion).description);
        }
        if (fromDiscussion instanceof AbstractDiscussionDTO)
        {
            return ((AbstractDiscussionDTO) fromDiscussion).text;
        }
        return "No text to translate";
    }

    public boolean isValidLangCode(String langCode)
    {
        return langCode != null && !TextUtils.isEmpty(langCode) && !langCode.equals("xxx");
    }
}
