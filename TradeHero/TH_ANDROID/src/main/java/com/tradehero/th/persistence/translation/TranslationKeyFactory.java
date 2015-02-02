package com.tradehero.th.persistence.translation;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import com.tradehero.th.api.discussion.AbstractDiscussionCompactDTO;
import com.tradehero.th.api.discussion.AbstractDiscussionDTO;
import com.tradehero.th.api.news.NewsItemCompactDTO;
import com.tradehero.th.api.news.NewsItemDTO;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.inject.Inject;

public class TranslationKeyFactory
{
    //<editor-fold desc="Constructors">
    @Inject public TranslationKeyFactory()
    {
    }
    //</editor-fold>

    @NonNull public TranslationKeyList createFrom(
            @NonNull AbstractDiscussionCompactDTO fromDiscussion,
            @NonNull String toLanguage)
    {
        if (fromDiscussion.langCode == null)
        {
            return new TranslationKeyList();
        }
        return createKeys(
                fromDiscussion.langCode,
                toLanguage,
                getTranslatableTexts(fromDiscussion));
    }

    @NonNull protected List<String> getTranslatableTexts(
            @NonNull AbstractDiscussionCompactDTO fromDiscussion)
    {
        List<String> texts = new ArrayList<>();
        if (fromDiscussion instanceof AbstractDiscussionDTO)
        {
            AbstractDiscussionDTO abstractDiscussionDTO = (AbstractDiscussionDTO) fromDiscussion;
            if (abstractDiscussionDTO.text != null)
            {
                texts.add(abstractDiscussionDTO.text);
            }
        }
        else if (fromDiscussion instanceof NewsItemCompactDTO)
        {
            NewsItemCompactDTO newsItemCompactDTO = (NewsItemCompactDTO) fromDiscussion;
            if (newsItemCompactDTO.caption != null)
            {
                texts.add(newsItemCompactDTO.caption);
            }
            if (newsItemCompactDTO.title != null)
            {
                texts.add(newsItemCompactDTO.title);
            }
            if (newsItemCompactDTO.description != null)
            {
                texts.add(newsItemCompactDTO.description);
            }
            if (newsItemCompactDTO instanceof NewsItemDTO)
            {
                NewsItemDTO newsItemDTO = (NewsItemDTO) newsItemCompactDTO;
                if (newsItemDTO.text != null)
                {
                    texts.add(newsItemDTO.text);
                }
                if (newsItemDTO.message != null)
                {
                    texts.add(newsItemDTO.message);
                }
            }
        }
        return texts;
    }

    @NonNull public TranslationKeyList createKeys(
            @NonNull String fromLang,
            @NonNull String toLang,
            @NonNull Collection<String> texts)
    {
        TranslationKeyList keys = new TranslationKeyList();
        for (String text: texts)
        {
            keys.add(new TranslationKey(fromLang, toLang, text));
        }
        return keys;
    }

    public boolean isValidLangCode(@Nullable String langCode)
    {
        return langCode != null
                && !TextUtils.isEmpty(langCode)
                && !langCode.equals("xxx");
    }
}
