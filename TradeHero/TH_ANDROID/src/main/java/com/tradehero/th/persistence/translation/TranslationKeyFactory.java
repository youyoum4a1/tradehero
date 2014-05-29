package com.tradehero.th.persistence.translation;

import android.text.TextUtils;
import com.tradehero.th.api.discussion.AbstractDiscussionCompactDTO;
import com.tradehero.th.api.discussion.AbstractDiscussionDTO;
import com.tradehero.th.api.discussion.DiscussionDTO;
import com.tradehero.th.api.discussion.PrivateDiscussionDTO;
import com.tradehero.th.api.news.NewsItemCompactDTO;
import com.tradehero.th.api.news.NewsItemDTO;
import com.tradehero.th.api.timeline.TimelineItemDTO;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.inject.Inject;

public class TranslationKeyFactory
{
    @Inject public TranslationKeyFactory()
    {
    }

    public TranslationKeyList createFrom(AbstractDiscussionCompactDTO fromDiscussion, String toLanguage)
    {
        if (fromDiscussion == null)
        {
            return null;
        }
        return createKeys(
                fromDiscussion.langCode,
                toLanguage,
                getTranslatableTexts(fromDiscussion));
    }

    protected List<String> getTranslatableTexts(AbstractDiscussionCompactDTO fromDiscussion)
    {
        if (fromDiscussion == null)
        {
            return null;
        }
        List<String> texts = new ArrayList<>();
        if (fromDiscussion instanceof AbstractDiscussionDTO)
        {
            if (((AbstractDiscussionDTO) fromDiscussion).text != null)
            {
                texts.add(((AbstractDiscussionDTO) fromDiscussion).text);
            }
            if (fromDiscussion instanceof DiscussionDTO)
            {
                if (fromDiscussion instanceof PrivateDiscussionDTO)
                {
                }
            }
            else if (fromDiscussion instanceof TimelineItemDTO)
            {
            }
            else if (fromDiscussion instanceof NewsItemDTO)
            {
                if (((NewsItemDTO) fromDiscussion).caption != null)
                {
                    texts.add(((NewsItemDTO) fromDiscussion).caption);
                }
                if (((NewsItemDTO) fromDiscussion).title != null)
                {
                    texts.add(((NewsItemDTO) fromDiscussion).title);
                }
                if (((NewsItemDTO) fromDiscussion).description != null)
                {
                    texts.add(((NewsItemDTO) fromDiscussion).description);
                }
            }
        }
        else if (fromDiscussion instanceof NewsItemCompactDTO)
        {
            if (((NewsItemCompactDTO) fromDiscussion).caption != null)
            {
                texts.add(((NewsItemCompactDTO) fromDiscussion).caption);
            }
            if (((NewsItemCompactDTO) fromDiscussion).title != null)
            {
                texts.add(((NewsItemCompactDTO) fromDiscussion).title);
            }
            if (((NewsItemCompactDTO) fromDiscussion).description != null)
            {
                texts.add(((NewsItemCompactDTO) fromDiscussion).description);
            }
        }
        return texts;
    }


    public TranslationKeyList createKeys(String fromLang, String toLang, Collection<String> texts)
    {
        if (texts == null)
        {
            return null;
        }
        TranslationKeyList keys = new TranslationKeyList();
        for (String text: texts)
        {
            keys.add(new TranslationKey(fromLang, toLang, text));
        }
        return keys;
    }
    public boolean isValidLangCode(String langCode)
    {
        return langCode != null && !TextUtils.isEmpty(langCode) && !langCode.equals("xxx");
    }
}
